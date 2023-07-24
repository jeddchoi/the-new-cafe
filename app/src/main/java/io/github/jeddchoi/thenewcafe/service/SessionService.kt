package io.github.jeddchoi.thenewcafe.service

import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import io.github.jeddchoi.data.repository.UserSessionRepository
import io.github.jeddchoi.model.UserStateType
import io.github.jeddchoi.thenewcafe.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SessionService : LifecycleService() {
    override fun getLifecycle(): Lifecycle = lifecycle

    @Inject
    lateinit var userSessionRepository: UserSessionRepository

    var job: Job? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Action.START.name -> {
                if (job == null) {
                    job = lifecycleScope.launch {
                        withContext(Dispatchers.Default) {
                            userSessionRepository.userStateAndUsedSeatPosition.collectLatest {
                                when (it.userState) {
                                    null -> onUnAuthenticated()
                                    UserStateType.None -> onNone()
                                    UserStateType.Reserved -> onReserved()
                                    UserStateType.Occupied -> onOccupied()
                                    UserStateType.Away -> onAway()
                                    UserStateType.OnBusiness -> onBusiness()
                                }
                            }

                        }
                    }
                }
            }

            Action.QUIT.name -> stopSelf()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun onUnAuthenticated() {
        stopSelf()
    }

    private fun onNone() {
        // cancel notification
        // stop scan
        stopSelf()
    }

    private fun onReserved() {
        // show notification
        showNotification("Reserved")
        // scan ble and if found, occupy seat
    }

    private fun onOccupied() {
        // show notification
        showNotification("Occupied")
        // scan ble and if away from seat, leave
    }

    private fun onAway() {
        // show notification
        showNotification("Away")
        // scan ble and if found, resume using seat
    }

    private fun onBusiness() {
        // show notification
        showNotification("Business")
        // stop scan
    }


    private fun showNotification(content: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(io.github.jeddchoi.mypage.R.string.user_session))
            .setContentText(content)
            .build()

        startForeground(1, notification)
    }

    enum class Action {
        START,
        QUIT
    }


    companion object {
        const val CHANNEL_ID = "session_channel"
    }
}