package io.github.jeddchoi.thenewcafe.service

import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import io.github.jeddchoi.ble.BleRepositoryImpl
import io.github.jeddchoi.data.repository.StoreRepository
import io.github.jeddchoi.data.repository.UserPresenceRepository
import io.github.jeddchoi.data.repository.UserSessionRepository
import io.github.jeddchoi.data.service.seatfinder.SeatFinderService
import io.github.jeddchoi.model.DisplayedUserSession
import io.github.jeddchoi.model.UserStateAndUsedSeatPosition
import io.github.jeddchoi.model.UserStateType
import io.github.jeddchoi.thenewcafe.R
import io.github.jeddchoi.thenewcafe.ui.MainActivity
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

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


    @Inject
    lateinit var bleRepository: BleRepositoryImpl

    private var handleNotificationJob: Job? = null


    private val _userSession = MutableStateFlow<UserStateAndUsedSeatPosition?>(null)

    private var startedObserveConnection = false

    // This would be called multiple times
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.i("✅")
        when (intent?.action) {
            Action.START.name -> {
                lifecycleScope.launch(CoroutineName("ShouldBeConnected Handler")) {
                    bleRepository.shouldBeConnected.collectLatest {
                        bleRepository.scanAndConnect(lifecycleScope)
                    }
                }

                lifecycleScope.launch(CoroutineName("UserState Handler")) {
                    userSessionRepository.userStateAndUsedSeatPosition.distinctUntilChanged().collectLatest {
                        Timber.i("[${coroutineContext[CoroutineName.Key]}]\nUserState : $it")
                        when (it) {
                            null,
                            UserStateAndUsedSeatPosition.None -> {
                                userPresenceRepository.stopObserveUserPresence()
                                bleRepository.quit()
                            }

                            is UserStateAndUsedSeatPosition.UsingSeat -> {
                                when (it.userState) {
                                    UserStateType.None -> throw IllegalStateException()
                                    UserStateType.Reserved -> {
                                        userPresenceRepository.stopObserveUserPresence()
                                        bleRepository.shouldBeConnected(
                                            getBleSeat = {
                                                storeRepository.getBleSeat(it.seatPosition) ?: throw IllegalStateException()
                                            },
                                            onConnected = {
                                                seatFinderService.occupySeat()
                                            }
                                        )
                                    }

                                    UserStateType.Occupied -> {
                                        userPresenceRepository.observeUserPresence()
                                        bleRepository.shouldBeConnected(
                                            getBleSeat = {
                                                storeRepository.getBleSeat(it.seatPosition) ?: throw IllegalStateException()
                                            },
                                            onDisconnected = {
                                                seatFinderService.leaveAway()
                                            },
                                            connectionTimeout = 3.minutes,
                                            onTimeout = {
                                                Timber.i("[${coroutineContext[CoroutineName.Key]}]\n⏰ timeout ======")
                                                seatFinderService.leaveAway()
                                            }
                                        )
                                    }

                                    UserStateType.Away -> {
                                        userPresenceRepository.stopObserveUserPresence()
                                        bleRepository.shouldBeConnected(
                                            getBleSeat = {
                                                storeRepository.getBleSeat(it.seatPosition) ?: throw IllegalStateException()
                                            },
                                            onConnected = {
                                                seatFinderService.resumeUsing()
                                            }
                                        )
                                    }

                                    UserStateType.OnBusiness -> {
                                        userPresenceRepository.stopObserveUserPresence()
                                        bleRepository.disconnect()
                                    }
                                }
                            }
                        }
                    }
                }
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


    enum class Action {
        START,
        QUIT
    }


    companion object {
        const val CHANNEL_ID = "session_channel"
    }
}