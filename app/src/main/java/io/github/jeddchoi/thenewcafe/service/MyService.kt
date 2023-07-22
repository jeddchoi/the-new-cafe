package io.github.jeddchoi.thenewcafe.service

import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleService

class MyService : LifecycleService() {
    override fun getLifecycle(): Lifecycle = lifecycle


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Action.RESERVE.name -> {


            }

            Action.OCCUPY.name -> {

            }

            Action.START_BUSINESS.name -> {

            }

            Action.LEAVE_AWAY.name -> {

            }

            Action.QUIT.name -> stopSelf()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun reserve() {

    }

    enum class Action {
        RESERVE,
        OCCUPY,
        START_BUSINESS,
        LEAVE_AWAY,
        QUIT
    }
}