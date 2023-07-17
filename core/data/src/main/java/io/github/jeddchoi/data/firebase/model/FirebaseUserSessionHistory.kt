package io.github.jeddchoi.data.firebase.model

import io.github.jeddchoi.model.SeatPosition
import io.github.jeddchoi.model.UserSessionHistory
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class FirebaseUserSessionHistory(
    val startTime: Long? = null,
    val endTime: Long? = null,
    val hasFailure: Boolean? = null,
    val seatPosition: SeatPosition? = null,
)


fun FirebaseUserSessionHistory.toUserSessionHistory(sessionId: String): UserSessionHistory {
    return UserSessionHistory(
        sessionId = sessionId,
        startTime = startTime?.let { Instant.fromEpochMilliseconds(it) } ?: Instant.DISTANT_PAST,
        endTime = endTime?.let { Instant.fromEpochMilliseconds(it) },
        hasFailure = hasFailure ?: false,
        seatPosition = seatPosition ?: SeatPosition(),
    )
}