package io.github.jeddchoi.data.service.seatfinder

import io.github.jeddchoi.data.firebase.model.FirebaseCurrentSession
import io.github.jeddchoi.data.firebase.model.FirebaseSeat
import io.github.jeddchoi.model.UserStateType
import kotlinx.datetime.Instant
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable

@Serializable
data class FirebaseTransactionResult<T>(
    @EncodeDefault val before: T? = null,
    @EncodeDefault val after: T? = null,
    val resultCode: String = ResultCode.REJECTED.name,
)

@Serializable
data class FirebaseRequestResult(
    val sessionResult: FirebaseTransactionResult<FirebaseCurrentSession>? = null,
    val seatResult: FirebaseTransactionResult<FirebaseSeat>? = null,
) {
    fun toSeatFinderResult() = SeatFinderResult(
        resultCode = ResultCode.getByValue(sessionResult?.resultCode ?: seatResult?.resultCode),
        afterSeatName = seatResult?.after?.name,
        startSessionTime = sessionResult?.after?.startSessionTime?.let {
            Instant.fromEpochMilliseconds(
                it
            )
        },
        previousState = sessionResult?.before?.getCurrentStateStr()
            ?.let { UserStateType.getByValue(it) } ?: UserStateType.None,
        currentState = sessionResult?.after?.getCurrentStateStr()
            ?.let { UserStateType.getByValue(it) } ?: UserStateType.None,
        startCurrentStateTime = sessionResult?.after?.getCurrentStateStartTime()?.let {
            Instant.fromEpochMilliseconds(it)
        },
        endCurrentStateTime = sessionResult?.after?.getCurrentStateEndTime()?.let {
            Instant.fromEpochMilliseconds(
                it
            )
        },
        requestTypeAfterCurrentState = sessionResult?.after?.getRequestTypeStrAfterCurrentState()?.let {
            SeatFinderRequestType.getByValue(it)
        }
    )

}