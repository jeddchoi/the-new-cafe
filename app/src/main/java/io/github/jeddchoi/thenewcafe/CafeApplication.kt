package io.github.jeddchoi.thenewcafe

import android.app.Application
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import dagger.hilt.android.HiltAndroidApp
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
            tag = null,
            message = "$message $tag",
            t = t
        )
//        println(String.format("%2\$s %1\$s", tag, message))
//        t?.printStackTrace(System.out)
    }

}