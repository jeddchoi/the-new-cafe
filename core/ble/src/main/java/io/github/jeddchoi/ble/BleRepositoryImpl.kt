package io.github.jeddchoi.ble

import com.juul.kable.ConnectionLostException
import com.juul.kable.Peripheral
import com.juul.kable.Scanner
import com.juul.kable.State
import com.juul.kable.peripheral
import io.github.jeddchoi.model.BleSeat
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
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
                onTimeout = onTimeout,
                wasConnectedSuccessfully = false,
            ) ?: BleState(
                bleSeat = bleSeat,
                scanner = scanner,
                onConnected = onConnected,
                onDisconnected = onDisconnected,
                connectionTimeout = connectionTimeout,
                onTimeout = onTimeout,
                wasConnectedSuccessfully = false,
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
                            state?.copy(foundPeripheralState = newState, wasConnectedSuccessfully = newState == State.Connected)
                        }
                        Timber.i("[${coroutineContext[CoroutineName.Key]}]\n✅ $previousState -> $newState")

                        when (newState) {
                            State.Connected -> {
                                bleState.value?.onConnected?.invoke()?.also {
                                    Timber.i("[${coroutineContext[CoroutineName.Key]}]\nonConnected")
                                    cancel()
                                }
                            }

                            is State.Disconnected -> {
                                if (bleState.value?.wasConnectedSuccessfully == true) {
                                    bleState.value?.onDisconnected?.invoke()?.also {
                                        Timber.i("[${coroutineContext[CoroutineName.Key]}]\nonDisconnected")
                                        cancel()
                                    }
                                }
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
            _bleState.update {
                it?.copy(onConnected = {}, onDisconnected = {}, onTimeout = {}, connectionTimeout = null)
            }
            _bleState.value?.foundPeripheral?.disconnect()
        }
    }
}
