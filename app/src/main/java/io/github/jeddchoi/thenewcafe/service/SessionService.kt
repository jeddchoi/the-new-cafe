package io.github.jeddchoi.thenewcafe.service

import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.benasher44.uuid.bytes
import com.benasher44.uuid.uuidFrom
import com.juul.kable.ConnectionLostException
import com.juul.kable.Filter
import com.juul.kable.Peripheral
import com.juul.kable.Scanner
import com.juul.kable.State
import com.juul.kable.peripheral
import dagger.hilt.android.AndroidEntryPoint
import io.github.jeddchoi.data.repository.StoreRepository
import io.github.jeddchoi.data.repository.UserPresenceRepository
import io.github.jeddchoi.data.repository.UserSessionRepository
import io.github.jeddchoi.data.service.seatfinder.SeatFinderService
import io.github.jeddchoi.model.DisplayedUserSession
import io.github.jeddchoi.model.SeatPosition
import io.github.jeddchoi.model.UserStateAndUsedSeatPosition
import io.github.jeddchoi.model.UserStateType
import io.github.jeddchoi.thenewcafe.R
import io.github.jeddchoi.thenewcafe.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class SessionService : LifecycleService() {

    @Inject
    lateinit var storeRepository: StoreRepository

    @Inject
    lateinit var userSessionRepository: UserSessionRepository

    @Inject
    lateinit var seatFinderService: SeatFinderService

    @Inject
    lateinit var userPresenceRepository: UserPresenceRepository


    private var handleNotificationJob: Job? = null
    private var handleBleJob: Job? = null
    private var observeBleStateJob: Job? = null
    private var connectBleJob: Job? = null


    private val _peripheral = MutableStateFlow<Peripheral?>(null)

    private val peripheralState = _peripheral.flatMapLatest {
        it?.state ?: flowOf(null)
    }.onEach { Timber.v("💥") }
        .stateIn(lifecycleScope, SharingStarted.WhileSubscribed(5000), null)


    private val _userSession = MutableStateFlow<UserStateAndUsedSeatPosition?>(null)

    private var startedObserveConnection = false

    // This would be called multiple times
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.v("✅")
        when (intent?.action) {
            Action.START.name -> {
                handleBleConnection()
                handleNotification()
            }

            Action.QUIT.name -> stopSelf()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun handleNotification() {
        Timber.v("${handleNotificationJob?.isCompleted}")
        if (handleNotificationJob == null || handleNotificationJob?.isCompleted == true) {
            handleNotificationJob = lifecycleScope.launch {
                userSessionRepository.userSessionWithTimer.collectLatest {
                    Timber.v("💥 $it")
                    when (it) {
                        DisplayedUserSession.None,
                        null -> {
                            stopSelf()
                        }

                        is DisplayedUserSession.UsingSeat -> {
                            showNotification(
                                title = it.state.name,
                                content = getString(
                                    R.string.remaining_time,
                                    it.currentStateTimer.remainingTime?.coerceAtLeast(
                                        Duration.ZERO
                                    )?.toIsoString() ?: getString(R.string.unlimited)
                                ),
                                maxProgress = it.currentStateTimer.totalTime?.inWholeSeconds?.toInt(),
                                progress = it.currentStateTimer.remainingTime?.inWholeSeconds?.toInt()
                            )

                        }
                    }
                }
            }
        }
    }


    private fun showNotification(
        title: String,
        content: String,
        maxProgress: Int?,
        progress: Int?
    ) {
        Timber.v("✅ $title $content $maxProgress $progress")
        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            "https://io.github.jeddchoi.thenewcafe/mypage".toUri(),
            applicationContext,
            MainActivity::class.java
        )

        val deepLinkPendingIntent: PendingIntent? =
            TaskStackBuilder.create(applicationContext).run {
                addNextIntentWithParentStack(deepLinkIntent)
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            }


        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content)
            .setContentIntent(deepLinkPendingIntent)

        if (maxProgress != null && progress != null) {
            builder.setProgress(maxProgress, progress, false)
        }

        startForeground(1, builder.build())
    }

    private fun handleBleConnection() {
        Timber.v("${handleBleJob?.isCompleted}")
        if (handleBleJob == null || handleBleJob?.isCompleted == true) {
            handleBleJob = lifecycleScope.launch(Dispatchers.IO) {
                userSessionRepository.userStateAndUsedSeatPosition.distinctUntilChanged()
                    .collectLatest { userSession ->
                        Timber.v("💥 $userSession")
                        _userSession.update { userSession }

                        if (userSession?.userState == UserStateType.Occupied) {
                            observePresence()
                        } else {
                            userPresenceRepository.stopObserveUserPresence()
                        }

                        when (userSession) {
                            null,
                            UserStateAndUsedSeatPosition.None -> {
                                withTimeoutOrNull(5_000) {
                                    _peripheral.value?.disconnect()
                                    stopSelf()
                                }
                            }

                            is UserStateAndUsedSeatPosition.UsingSeat -> {
                                when (userSession.userState) {
                                    UserStateType.None -> throw IllegalStateException()

                                    UserStateType.Reserved,
                                    UserStateType.Occupied,
                                    UserStateType.Away -> {
                                        observeConnectionState(this)
                                        scanAndConnectBleIfDisconnected(
                                            this,
                                            userSession.seatPosition,
                                            userSession.userState
                                        )
                                    }

                                    UserStateType.OnBusiness -> {
                                        withTimeoutOrNull(5_000) {
                                            _peripheral.value?.disconnect()
                                        }
                                    }
                                }
                            }
                        }
                    }
            }
        }
    }


    private fun scanAndConnectBleIfDisconnected(
        bleCoroutineScope: CoroutineScope,
        seatPosition: SeatPosition,
        currentUserState: UserStateType,
    ) {
        Timber.v("✅ ${_peripheral.value}")

        connectBleJob?.cancel()
        connectBleJob = bleCoroutineScope.launch {
            if (currentUserState == UserStateType.Occupied) {
                withTimeoutOrNull(60.seconds) {
                    scanAndConnect(seatPosition, bleCoroutineScope)
                } ?: run {
                    seatFinderService.leaveAway()
                }
            } else {
                scanAndConnect(seatPosition, bleCoroutineScope)
            }
        }
    }

    private suspend fun scanAndConnect(
        seatPosition: SeatPosition,
        bleCoroutineScope: CoroutineScope
    ) {
        if (_peripheral.value == null) {
            val bleSeat =
                storeRepository.getBleSeat(seatPosition) ?: throw IllegalStateException()
            val major = bleSeat.major.toInt()
            val minor = bleSeat.minor.toInt()
            val scanner = Scanner {
                filters = listOf(
                    Filter.Service(BEACON_SERVICE_UUID),
                    Filter.Name(bleSeat.name),
                    Filter.Address(bleSeat.macAddress),
                    Filter.ManufacturerData(
                        id = MANUFACTURER_ID,
                        data = byteArrayOf(
                            0, 0, 0, 0,
                            *uuidFrom(bleSeat.uuid).bytes,
                            major.shr(8).toByte(),
                            major.shr(0).toByte(),
                            minor.shr(8).toByte(),
                            minor.shr(0).toByte(),
                            0
                        ),
                        dataMask = MASK_UUID_MAJOR_MINOR,
                    ),
                )
            }
            Timber.i("Scanning...")
            val adv = scanner.advertisements.first()
            Timber.i("Found $adv!")

            _peripheral.update {
                bleCoroutineScope.peripheral(adv)
            }
        }


        while (true) {
            try {
                Timber.i("Try connecting...")
                _peripheral.value?.connect()
                Timber.i("Connected!")
                break
            } catch (e: ConnectionLostException) {
                Timber.e(e)
            }
        }
    }

    private fun observeConnectionState(
        coroutineScope: CoroutineScope,
    ) {
        Timber.v("✅ ${observeBleStateJob?.isCompleted}")
        observeBleStateJob?.cancel()
        observeBleStateJob = coroutineScope.launch {
            peripheralState.collectLatest {
                when (it) {
                    null,
                    State.Connecting.Bluetooth,
                    State.Connecting.Observes,
                    State.Connecting.Services,
                    is State.Disconnected -> {
                    }

                    State.Connected -> {
                        when (val userSession = _userSession.value) {
                            null,
                            UserStateAndUsedSeatPosition.None -> {
                            }

                            is UserStateAndUsedSeatPosition.UsingSeat -> {
                                when (userSession.userState) {
                                    UserStateType.None,
                                    UserStateType.OnBusiness -> throw IllegalStateException()

                                    UserStateType.Reserved -> {
                                        seatFinderService.occupySeat()
                                    }

                                    UserStateType.Occupied -> {}
                                    UserStateType.Away -> {
                                        seatFinderService.resumeUsing()
                                    }
                                }
                            }
                        }
                    }

                    State.Disconnecting -> {
                        when (val userSession = _userSession.value) {
                            null,
                            UserStateAndUsedSeatPosition.None -> {
                            }

                            is UserStateAndUsedSeatPosition.UsingSeat -> {
                                when (userSession.userState) {
                                    UserStateType.None -> throw IllegalStateException()

                                    UserStateType.Reserved -> {}
                                    UserStateType.Occupied -> {
                                        seatFinderService.leaveAway()
                                    }

                                    UserStateType.Away -> {}
                                    UserStateType.OnBusiness -> {}
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    private fun observePresence() {
        Timber.v("✅")
        if (!startedObserveConnection) {
            userPresenceRepository.observeUserPresence()
            startedObserveConnection = true
        }
    }

    enum class Action {
        START,
        QUIT
    }


    companion object {
        const val CHANNEL_ID = "session_channel"
        val BEACON_SERVICE_UUID = uuidFrom("9FD42000-E46F-7C9A-57B1-2DA365E18FA1")
        val MANUFACTURER_ID = byteArrayOf(0, 0x59) // Nordic

        val MASK_UUID_MAJOR_MINOR = byteArrayOf(
            0, 0, 0, 0,
            -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, 0,
        )
    }
}