package io.github.jeddchoi.model

data class Seat (
    val isAvailable: Boolean = false,
    val minor: String = "",
    val name: String = "",
    val state: SeatStateType = SeatStateType.Empty,
)

