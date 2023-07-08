package io.github.jeddchoi.data.firebase.model

import io.github.jeddchoi.model.SeatFinderRequestType
import io.github.jeddchoi.model.SeatPosition
import io.github.jeddchoi.model.UserSession
import io.github.jeddchoi.model.UserStateType
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class FirebaseTimerInfo(
    val willRequestType: String = SeatFinderRequestType.ReserveSeat.name,
    val endTime: Long = 0,
    val taskName: String = "",
)

@Serializable
data class FirebasePartialUserState(
    val startTime: Long = 0,
    val state: String = UserStateType.None.name,
    val timer: FirebaseTimerInfo? = null,
)


@Serializable
data class FirebaseCurrentSession(
    val sessionId: String = "",
    val seatPosition: SeatPosition = SeatPosition(),
    val startSessionTime: Long = 0,
    val hasFailure: Boolean = false,
    val mainState: FirebasePartialUserState = FirebasePartialUserState(),
    val subState: FirebasePartialUserState? = null,
) {
    fun getCurrentStateStr() = subState?.state ?: mainState.state
    fun getCurrentStateStartTime() = subState?.startTime ?: mainState.startTime
    fun getCurrentStateEndTime() =
        if (subState != null) subState.timer?.endTime else mainState.timer?.endTime

    fun getRequestTypeStrAfterCurrentState() =
        if (subState != null) subState.timer?.willRequestType else mainState.timer?.willRequestType


}

fun FirebaseCurrentSession?.toUserSession() =
    if (this == null)
        UserSession.None
    else
        UserSession.UsingSeat(
            sessionId = sessionId,
            hasFailure = hasFailure,
            startSessionTime = Instant.fromEpochMilliseconds(startSessionTime),
            endSessionTime = mainState.timer?.endTime?.let { Instant.fromEpochMilliseconds(it) },
            startTime = Instant.fromEpochMilliseconds(getCurrentStateStartTime()),
            endTime = getCurrentStateEndTime()?.let { Instant.fromEpochMilliseconds(it) },
            currentState = UserStateType.getByValue(getCurrentStateStr()),
            requestTypeAfterCurrentState = getRequestTypeStrAfterCurrentState()?.let {
                SeatFinderRequestType.getByValue(
                    it
                )
            },
            seatPosition = seatPosition,
        )