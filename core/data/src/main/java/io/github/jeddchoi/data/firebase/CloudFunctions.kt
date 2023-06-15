package io.github.jeddchoi.data.firebase

import android.util.Log

import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import io.github.jeddchoi.model.SeatPosition
import io.github.jeddchoi.model.UserStatusChange
import io.github.jeddchoi.model.UserStatusChangeCause
import io.github.jeddchoi.model.UserStatusType
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

const val CLOUD_FUNCTION_RESERVE_SEAT = "OnCall-reserveSeat"

@Singleton
class CloudFunctions @Inject constructor(
    private val functions: FirebaseFunctions
) {

    suspend fun reserveSeat(
        seatPosition: SeatPosition,
        durationInSeconds: Int
    ): String {
        try {
            val result = functions.getHttpsCallable(CLOUD_FUNCTION_RESERVE_SEAT).call(
                Json.encodeToString(
                    UserStatusChange(
                        prevStatus = UserStatusType.None,
                        targetStatus = UserStatusType.Reserved,
                        cause = UserStatusChangeCause.UserAction,
                        requestTimestamp = Clock.System.now().epochSeconds,
                        seatPos = seatPosition,
                        durationInSeconds = durationInSeconds,
                    )
                )
            ).await()

            return result.data.toString()
        } catch (e: FirebaseFunctionsException) {
            val code = e.code
            val details = e.details
            val message = e.message
            Log.e(
                "CloudFunctions",
                "Error code: $code, details: $details, message: $message"
            )
            return "Not returning result"
        }
    }
}