package io.github.jeddchoi.firebase.realtime

import io.github.jeddchoi.firebase.functions.FirebaseSeatFinderRequestType
import io.github.jeddchoi.firebase.functions.toSeatFinderRequestType
import io.github.jeddchoi.model.SeatPosition
import io.github.jeddchoi.model.UserSession
import io.github.jeddchoi.model.UserStateType
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class FirebaseTimerInfo(
    val willRequestType: String? = null,
    val endTime: Long? = null,
    val taskName: String? = null,
)

@Serializable
data class FirebasePartialUserState(
    val startTime: Long? = null,
    val state: String? = null,
    val timer: FirebaseTimerInfo? = null,
)


@Serializable
data class FirebaseCurrentSession(
    val sessionId: String? = null,
    val seatPosition: SeatPosition? = null,
    val startSessionTime: Long? = null,
    val hasFailure: Boolean? = null,
    val disconnectedOnOccupied: Boolean? = null,
    val mainState: FirebasePartialUserState? = null,
    val subState: FirebasePartialUserState? = null,
) {
    fun getCurrentStateStr() = subState?.state ?: mainState?.state
    fun getCurrentStateStartTime() = subState?.startTime ?: mainState?.startTime
    fun getCurrentStateEndTime() =
        if (subState != null) subState.timer?.endTime else mainState?.timer?.endTime

    fun getRequestTypeStrAfterCurrentState() =
        if (subState != null) subState.timer?.willRequestType else mainState?.timer?.willRequestType


}

fun FirebaseCurrentSession?.toUserSession() =
    if (this == null)
        UserSession.None
    else
        UserSession.UsingSeat(
            sessionId = sessionId ?: "",
            hasFailure = hasFailure ?: false,
            startSessionTime = startSessionTime?.let { Instant.fromEpochMilliseconds(it) }
                ?: Instant.DISTANT_PAST,
            endSessionTime = mainState?.timer?.endTime?.let { Instant.fromEpochMilliseconds(it) },
            startTime = getCurrentStateStartTime()?.let { Instant.fromEpochMilliseconds(it) }
                ?: Instant.DISTANT_PAST,
            endTime = getCurrentStateEndTime()?.let { Instant.fromEpochMilliseconds(it) },
            currentState = UserStateType.getByValue(getCurrentStateStr()),
            requestTypeAfterCurrentState = getRequestTypeStrAfterCurrentState()?.let {
                FirebaseSeatFinderRequestType.getByValue(it)?.toSeatFinderRequestType()
            },
            seatPosition = seatPosition ?: SeatPosition(),
        )
