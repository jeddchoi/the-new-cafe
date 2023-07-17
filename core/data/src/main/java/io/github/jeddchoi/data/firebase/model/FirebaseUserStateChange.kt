package io.github.jeddchoi.data.firebase.model

import io.github.jeddchoi.model.SeatFinderRequestType
import io.github.jeddchoi.model.SeatStateType
import io.github.jeddchoi.model.UserStateChange
import io.github.jeddchoi.model.UserStateChangeReason
import io.github.jeddchoi.model.UserStateType
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class FirebaseUserStateChange(
    val requestType: String? = null,
    val success: Boolean? = null,
    val resultSeatState: String? = null,
    val resultUserState: String? = null,
    val reason: String? = null,
    val timestamp: Long? = null,
)


fun FirebaseUserStateChange.toUserStateChange() = UserStateChange(
    requestType = SeatFinderRequestType.getByValue(requestType) ?: SeatFinderRequestType.Quit,
    success = success ?: false,
    resultSeatState = SeatStateType.getByValue(resultSeatState),
    resultUserState = UserStateType.getByValue(resultUserState),
    reason = UserStateChangeReason.getByValue(reason),
    timestamp = timestamp?.let { Instant.fromEpochMilliseconds(it) } ?: Instant.DISTANT_PAST,
)