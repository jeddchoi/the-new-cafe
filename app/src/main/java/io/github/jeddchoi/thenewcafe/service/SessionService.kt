package io.github.jeddchoi.thenewcafe.service

import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.benasher44.uuid.uuidFrom
import com.juul.kable.ConnectionLostException
import com.juul.kable.Filter
import com.juul.kable.Peripheral
import com.juul.kable.Scanner
import com.juul.kable.State
import com.juul.kable.peripheral
import dagger.hilt.android.AndroidEntryPoint
import io.github.jeddchoi.data.repository.StoreRepository
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
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
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

@AndroidEntryPoint
class SessionService : LifecycleService() {

    @Inject
    lateinit var storeRepository: StoreRepository

    @Inject
    lateinit var userSessionRepository: UserSessionRepository

    @Inject
    lateinit var seatFinderService: SeatFinderService


    private var handleNotificationJob: Job? = null
    private var handleBleJob: Job? = null
    private var observeBleStateJob: Job? = null


    //    override fun getLifecycle(): Lifecycle = lifecycle
    private val _peripheral = MutableStateFlow<Peripheral?>(null)

    private val peripheralState = _peripheral.flatMapLatest {
        it?.state ?: flowOf(null)
    }.onEach { Timber.i("peripheralState == $it") }
        .stateIn(lifecycleScope, SharingStarted.WhileSubscribed(5000), null)


    private val _userSession = MutableStateFlow<UserStateAndUsedSeatPosition?>(null)


    // This would be called multiple times
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.i("✅")
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
        Timber.i("handleNotification == ${handleNotificationJob?.isCompleted}")
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
        Timber.i("handleBleConnection == ${handleBleJob?.isCompleted}")
        if (handleBleJob == null || handleBleJob?.isCompleted == true) {
            handleBleJob = lifecycleScope.launch(Dispatchers.IO) {
                userSessionRepository.userStateAndUsedSeatPosition.distinctUntilChanged()
                    .collectLatest { userSession ->
                        Timber.i("💥 $userSession")
                        _userSession.update { userSession }
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
                                    UserStateType.Reserved -> {
                                        observeConnectionState(this)
                                        scanAndConnectBleIfDisconnected(
                                            this,
                                            userSession.seatPosition
                                        )
                                    }

                                    UserStateType.Occupied -> {
                                        observeConnectionState(this)
                                        scanAndConnectBleIfDisconnected(
                                            this,
                                            userSession.seatPosition
                                        )
                                    }

                                    UserStateType.Away -> {
                                        observeConnectionState(this)
                                        scanAndConnectBleIfDisconnected(
                                            this,
                                            userSession.seatPosition
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


    private suspend fun scanAndConnectBleIfDisconnected(
        bleCoroutineScope: CoroutineScope,
        seatPosition: SeatPosition,
    ) {
        if (_peripheral.value == null) {
            Timber.i("peripheral == null")
            val bleSeat = storeRepository.getBleSeat(seatPosition)
            Timber.d("start scan $bleSeat")
            val scanner = Scanner {
                filters = listOf(
//                        Filter.ManufacturerData(
//                            id = byteArrayOf(0x00, 0x59),
//                            data = byteArrayOf(),
//                            dataMask = byteArrayOf(),
//                        ),
                    Filter.Service(uuidFrom("9FD42000-E46F-7C9A-57B1-2DA365E18FA1")),
                    Filter.Name("My beacon1"),
                    Filter.Address("EC:60:5E:E5:34:AC") // A1
                )
            }
            val adv = scanner.advertisements.first()
            Timber.i("Advertisement $adv")

            _peripheral.update {
                bleCoroutineScope.peripheral(adv)
            }

        }
        while (true) {
            try {
                Timber.i("TRY CONNECTING...")
                _peripheral.value?.connect()
                break
            } catch (e: ConnectionLostException) {
                Timber.e(e)
            }
        }
        Timber.i("CONNECTED")
    }

    private fun observeConnectionState(
        coroutineScope: CoroutineScope,
    ) {
        Timber.i("observeConnectionState ${observeBleStateJob?.isCompleted}")
        if (observeBleStateJob == null || observeBleStateJob?.isCompleted == true) {
            observeBleStateJob = coroutineScope.launch {
                peripheralState.collectLatest {
                    when (it) {
                        null,
                        State.Connecting.Bluetooth,
                        State.Connecting.Observes,
                        State.Connecting.Services,
                        State.Disconnecting -> {
                        }

                        State.Connected -> {
                            Timber.i("connected when ${_userSession.value}")
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

                        is State.Disconnected -> {
                            Timber.i("disconnected when ${_userSession.value}")
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
    }

    enum class Action {
        START,
        QUIT
    }


    companion object {
        const val CHANNEL_ID = "session_channel"
    }
}