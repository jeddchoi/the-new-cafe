package io.github.jeddchoi.model

data class UserStateAndUsedSeatPosition(
    val seatPosition: SeatPosition? = null,
    val userState: UserStateType? = null,
)