package io.github.jeddchoi.data.firebase.model

import io.github.jeddchoi.data.service.SeatFinderRequestType
import io.github.jeddchoi.model.UserStateChangeReason
import io.github.jeddchoi.model.UserStateType
import kotlinx.serialization.Serializable

@Serializable
data class TimerInfo(
    val endTime: Long = 0,
    val taskName: String = "",
    val willRequestType: SeatFinderRequestType = SeatFinderRequestType.ReserveSeat,
)

sealed interface IFirebaseState {
    val state: UserStateType
    val reason: UserStateChangeReason
    val startTime: Long
    val timer: TimerInfo?

    @Serializable
    data class FirebaseOverallState(
        override val state: UserStateType = UserStateType.None,
        override val reason: UserStateChangeReason = UserStateChangeReason.UserAction,
        override val startTime: Long = 0,
        override val timer: TimerInfo? = null,
        val seatPosition: String? = null,
    ) : IFirebaseState

    @Serializable
    data class FirebaseTemporaryState(
        override val state: UserStateType = UserStateType.None,
        override val reason: UserStateChangeReason = UserStateChangeReason.UserAction,
        override val startTime: Long = 0,
        override val timer: TimerInfo? = null,
    ) : IFirebaseState
}

@Serializable
data class FirebaseUserStatus(
    val overall: IFirebaseState.FirebaseOverallState = IFirebaseState.FirebaseOverallState(),
    val temporary: IFirebaseState.FirebaseTemporaryState? = null,
)

@Serializable
data class FirebaseUserState(
    @field:JvmField
    val isOnline: Boolean = false,
    val name: String = "",
    val status: FirebaseUserStatus? = null,
)
