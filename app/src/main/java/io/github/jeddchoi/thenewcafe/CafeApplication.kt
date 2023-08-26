package io.github.jeddchoi.thenewcafe

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import dagger.hilt.android.HiltAndroidApp
import io.github.jeddchoi.thenewcafe.service.SessionService
import timber.log.Timber


@HiltAndroidApp
class CafeApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork() // or .detectAll() for all detectable problems
                    .detectCustomSlowCalls()
                    .penaltyLog()
                    .build()
            )
            StrictMode.setVmPolicy(
                VmPolicy.Builder()

                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build()
            )
            Timber.plant(TimberDebugTree())
        }

        createNotificationChannel()
    }


    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            SessionService.CHANNEL_ID,
            "Session Notifications",
            NotificationManager.IMPORTANCE_LOW
        )

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}

class TimberDebugTree : Timber.DebugTree() {

    override fun createStackElementTag(element: StackTraceElement): String {
        // returns a clickable Android Studio link
        return String.format(
            "\n%4\$s.%1\$s(%2\$s:%3\$s)\n\n",
            element.methodName,
            element.fileName,
            element.lineNumber,
            super.createStackElementTag(element)
        )
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        super.log(
            priority = priority,
            tag = TAG,
            message = "[${Thread.currentThread().name}]\n$message $tag",
            t = t
        )
//        println(String.format("%2\$s %1\$s", tag, message))
//        t?.printStackTrace(System.out)
    }

    companion object {
        const val TAG = "MY_DEBUG"
    }

}

