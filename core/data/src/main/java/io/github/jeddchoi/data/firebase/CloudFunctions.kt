package io.github.jeddchoi.data.firebase

import com.google.firebase.functions.FirebaseFunctions
import io.github.jeddchoi.model.SeatPosition
import io.github.jeddchoi.model.UserStatusChange
import io.github.jeddchoi.model.UserStatusChangeCause
import io.github.jeddchoi.model.UserStatusType
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

const val FUNCTION_ON_USER_STATUS_CHANGE_REQ = "onUserStatusChangeReq"

@Singleton
class CloudFunctions @Inject constructor(
    private val functions: FirebaseFunctions
) {
    suspend fun reserveSeat(uid: String, seatPosition: SeatPosition, expiresIn: Int): String {
        return functions.getHttpsCallable(FUNCTION_ON_USER_STATUS_CHANGE_REQ).call(
            Json.encodeToString(
                UserStatusChange(
                    prevStatus = UserStatusType.None,
                    status = UserStatusType.Reserved,
                    cause = UserStatusChangeCause.UserAction,
                    updatedAt = System.currentTimeMillis(),
                    expiresIn = expiresIn,
                    seatPos = seatPosition,
                )
            )
        ).continueWith { task ->
            val result = task.result?.data as String
            result
        }.await()
    }
}