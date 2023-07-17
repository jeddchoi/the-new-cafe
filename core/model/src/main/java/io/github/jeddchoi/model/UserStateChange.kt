package io.github.jeddchoi.model

import kotlinx.datetime.Instant

data class UserStateChange(
    val requestType: SeatFinderRequestType,
    val success: Boolean,
    val resultSeatState: SeatStateType,
    val resultUserState: UserStateType,
    val reason: UserStateChangeReason,
    val timestamp: Instant,
)
