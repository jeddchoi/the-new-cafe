package io.github.jeddchoi.model

sealed class UserStateAndUsedSeatPosition {
    abstract val userState: UserStateType
    object None : UserStateAndUsedSeatPosition() {
        override val userState: UserStateType = UserStateType.None
    }

    data class UsingSeat(
        val seatPosition: SeatPosition = SeatPosition(),
        override val userState: UserStateType,
        ) : UserStateAndUsedSeatPosition()
}