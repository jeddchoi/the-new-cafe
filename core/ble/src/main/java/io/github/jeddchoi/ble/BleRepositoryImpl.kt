package io.github.jeddchoi.ble

import com.juul.kable.ConnectionLostException
import com.juul.kable.Peripheral
import com.juul.kable.Scanner
import com.juul.kable.State
import com.juul.kable.peripheral
import io.github.jeddchoi.model.BleSeat
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class BleRepositoryImpl @Inject constructor() : BleRepository {
    private val _shouldBeConnected = MutableSharedFlow<Unit>()
    val shouldBeConnected = _shouldBeConnected.asSharedFlow()

    private val _bleState = MutableStateFlow<BleState?>(null)
    override val bleState = _bleState.asStateFlow()

    init {
        Timber.i("============== CREATED!!! ==============")
    }

    override suspend fun shouldBeConnected(
        getBleSeat: suspend () -> BleSeat,
        onConnected: suspend () -> Unit,
        onDisconnected: suspend () -> Unit,
        connectionTimeout: Duration?,
        onTimeout: suspend () -> Unit,
    ) {
        Timber.i("[${coroutineContext[CoroutineName.Key]}]\n✅")

        _bleState.update {
            val bleSeat = it?.bleSeat ?: getBleSeat()
            val scanner = it?.scanner ?: Scanner {
                filters = getFilters(bleSeat)
            }

            it?.copy(
                bleSeat = bleSeat,
                scanner = scanner,
                onConnected = onConnected,
                onDisconnected = onDisconnected,
                connectionTimeout = connectionTimeout,
                onTimeout = onTimeout
            ) ?: BleState(
                bleSeat = bleSeat,
                scanner = scanner,
                onConnected = onConnected,
                onDisconnected = onDisconnected,
                connectionTimeout = connectionTimeout,
                onTimeout = onTimeout
            )
        }

        _shouldBeConnected.emit(Unit)
    }

    override suspend fun quit() {
        Timber.i("[${coroutineContext[CoroutineName.Key]}]\n✅")
        disconnect()
        _bleState.update {
            null
        }
    }


    override suspend fun scanAndConnect(
        coroutineScope: CoroutineScope,
    ) {
        bleState.value?.apply {
            Timber.i("[${coroutineContext[CoroutineName.Key]}]\nSCAN AND CONNECT $connectionTimeout")
            val peripheral = if (connectionTimeout != null) {
                withTimeoutOrNull(connectionTimeout) {
                    scanAndConnect(coroutineScope)
                } ?: return@apply bleState.value?.onTimeout?.invoke() ?: Unit
            } else {
                scanAndConnect(coroutineScope)
            }

            coroutineScope.launch(CoroutineName("Peripheral State Handler")) {

                peripheral.state.collectLatest { newState ->
                    val previousState = bleState.value?.foundPeripheralState
                    if (previousState != newState) {
                        _bleState.update { state ->
                            state?.copy(foundPeripheralState = newState)
                        }
                        Timber.i("[${coroutineContext[CoroutineName.Key]}]\n✅ $previousState -> $newState")

                        when (newState) {
                            State.Connected -> {
                                Timber.i("[${coroutineContext[CoroutineName.Key]}]\nonConnected")
                                bleState.value?.onConnected?.invoke()
                            }

                            is State.Disconnected -> {
                                Timber.i("[${coroutineContext[CoroutineName.Key]}]\nonDisconnected")
                                bleState.value?.onDisconnected?.invoke()
                            }

                            State.Connecting.Bluetooth,
                            State.Connecting.Observes,
                            State.Connecting.Services,
                            State.Disconnecting -> {
                            } // no-op
                        }
                    }
                }
            }
        }
    }

    override suspend fun BleState.scanAndConnect(coroutineScope: CoroutineScope): Peripheral {
        Timber.i("[${coroutineContext[CoroutineName.Key]}]\nScanning... ${bleSeat?.name}")
        val peripheral = foundPeripheral ?: run {
            val adv = scanner?.advertisements?.first() ?: throw Exception("No scanner")
            coroutineScope.peripheral(adv)
        }.also { _bleState.update { state -> state?.copy(foundPeripheral = it) } }
        Timber.i("[${coroutineContext[CoroutineName.Key]}]\nFound! $peripheral")

        coroutineScope.launch(CoroutineName("Connection Handler")) {
            while (true) {
                try {
                    Timber.i("[${coroutineContext[CoroutineName.Key]}]\nTry connecting... ${peripheral.name}")
                    peripheral.connect()
                    Timber.i("[${coroutineContext[CoroutineName.Key]}]\nConnected!")
                    break
                } catch (e: ConnectionLostException) {
                    Timber.e(e)
                }
            }
        }
        return peripheral
    }


    override suspend fun disconnect() {
        Timber.i("[${coroutineContext[CoroutineName.Key]}]\nDisconnecting...")
        withTimeoutOrNull(5.seconds) {
            _bleState.value?.foundPeripheral?.disconnect()
        }
    }
}


//private fun handleBleConnection() {
//    Timber.i("${handleBleJob?.isCompleted}")
//    if (handleBleJob == null || handleBleJob?.isCompleted == true) {
//        handleBleJob = lifecycleScope.launch(Dispatchers.IO) {
//            userSessionRepository.userStateAndUsedSeatPosition.distinctUntilChanged()
//                .collectLatest { userSession ->
//                    Timber.v("💥 $userSession")
//                    _userSession.update { userSession }
//
//                    if (userSession?.userState == UserStateType.Occupied) {
//                        observePresence()
//                    } else {
//                        userPresenceRepository.stopObserveUserPresence()
//                    }
//
//                    when (userSession) {
//                        null,
//                        UserStateAndUsedSeatPosition.None -> {
//                            withTimeoutOrNull(5_000) {
//                                bleRepository.quit()
//                                stopSelf()
//                            }
//                        }
//
//                        is UserStateAndUsedSeatPosition.UsingSeat -> {
//                            when (userSession.userState) {
//                                UserStateType.None -> throw IllegalStateException()
//
//                                UserStateType.Reserved -> {
//                                    val bleSeat = storeRepository.getBleSeat(userSession.seatPosition)
//                                    bleRepository.initialize(bleSeat)
//                                    bleRepository.shouldBeConnected(
//                                        onConnected = {
//                                            seatFinderService.occupySeat()
//                                        }
//                                    )
//                                }
//                                UserStateType.Occupied -> {
//                                    bleRepository.shouldBeConnected(
//                                        onDisconnecting = {
//                                            seatFinderService.leaveAway()
//                                        }
//                                    )
//                                }
//                                UserStateType.Away -> {
//                                    bleRepository.shouldBeConnected(
//                                        onConnected = {
//                                            seatFinderService.resumeUsing()
//                                        }
//                                    )
//                                }
//
//                                UserStateType.OnBusiness -> {
//                                    bleRepository.disconnect()
//                                }
//                            }
//                        }
//                    }
//                }
//        }
//    }
//}


//    private fun scanAndConnectBleIfDisconnected(
//        bleCoroutineScope: CoroutineScope,
//        seatPosition: SeatPosition,
//        currentUserState: UserStateType,
//    ) {
////        Timber.i("✅ ${_peripheral.value}")
//
//        connectBleJob?.cancel()
//        connectBleJob = bleCoroutineScope.launch {
//            if (currentUserState == UserStateType.Occupied) {
//                withTimeoutOrNull(60.seconds) {
//                    scanAndConnect(seatPosition, bleCoroutineScope)
//                } ?: run {
//                    seatFinderService.leaveAway()
//                }
//            } else {
//                scanAndConnect(seatPosition, bleCoroutineScope)
//            }
//        }
//    }
//
//    private suspend fun scanAndConnect(
//        seatPosition: SeatPosition,
//        bleCoroutineScope: CoroutineScope
//    ) {
//        Timber.i("✅ $seatPosition")
//        if (_peripheral.value == null) {
//            val bleSeat =
//                storeRepository.getBleSeat(seatPosition) ?: throw IllegalStateException()
//            val major = bleSeat.major.toInt()
//            val minor = bleSeat.minor.toInt()
//            val scanner = Scanner {
//                filters = listOf(
//                    Filter.Service(BEACON_SERVICE_UUID),
//                    Filter.Name(bleSeat.name),
//                    Filter.Address(bleSeat.macAddress),
//                    Filter.ManufacturerData(
//                        id = MANUFACTURER_ID,
//                        data = byteArrayOf(
//                            0, 0, 0, 0,
//                            *uuidFrom(bleSeat.uuid).bytes,
//                            major.shr(8).toByte(),
//                            major.shr(0).toByte(),
//                            minor.shr(8).toByte(),
//                            minor.shr(0).toByte(),
//                            0
//                        ),
//                        dataMask = MASK_UUID_MAJOR_MINOR,
//                    ),
//                )
//            }
//            Timber.i("Scanning...")
//            val adv = scanner.advertisements.first()
//            Timber.i("Found $adv!")
//
//            _peripheral.update {
//                bleCoroutineScope.peripheral(adv)
//            }
//        }
//
//
//        while (true) {
//            try {
//                Timber.i("Try connecting...")
//                _peripheral.value?.connect()
//                Timber.i("Connected!")
//                break
//            } catch (e: ConnectionLostException) {
//                Timber.e(e)
//            }
//        }
//    }
//
//    private fun observeConnectionState(
//        coroutineScope: CoroutineScope,
//    ) {
//        Timber.i("✅ ${observeBleStateJob?.isCompleted}")
//        observeBleStateJob?.cancel()
//        observeBleStateJob = coroutineScope.launch {
//            peripheralState.collectLatest {
//                when (it) {
//                    null,
//                    State.Connecting.Bluetooth,
//                    State.Connecting.Observes,
//                    State.Connecting.Services,
//                    is State.Disconnected -> {
//                    }
//
//                    State.Connected -> {
//                        when (val userSession = _userSession.value) {
//                            null,
//                            UserStateAndUsedSeatPosition.None -> {
//                            }
//
//                            is UserStateAndUsedSeatPosition.UsingSeat -> {
//                                when (userSession.userState) {
//                                    UserStateType.None,
//                                    UserStateType.OnBusiness -> throw IllegalStateException()
//
//                                    UserStateType.Reserved -> {
//                                        seatFinderService.occupySeat()
//                                    }
//
//                                    UserStateType.Occupied -> {}
//                                    UserStateType.Away -> {
//                                        seatFinderService.resumeUsing()
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    State.Disconnecting -> {
//                        when (val userSession = _userSession.value) {
//                            null,
//                            UserStateAndUsedSeatPosition.None -> {
//                            }
//
//                            is UserStateAndUsedSeatPosition.UsingSeat -> {
//                                when (userSession.userState) {
//                                    UserStateType.None -> throw IllegalStateException()
//
//                                    UserStateType.Reserved -> {}
//                                    UserStateType.Occupied -> {
//                                        seatFinderService.leaveAway()
//                                    }
//
//                                    UserStateType.Away -> {}
//                                    UserStateType.OnBusiness -> {}
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//        }
//    }


//private fun observePresence() {
//    Timber.i("✅")
//    if (!startedObserveConnection) {
//        userPresenceRepository.observeUserPresence()
//        startedObserveConnection = true
//    }
//}