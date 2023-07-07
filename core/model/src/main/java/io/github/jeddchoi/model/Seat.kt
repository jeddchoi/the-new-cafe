package io.github.jeddchoi.model

data class Seat (
    val id : String = "",
    val name: String = "",
    val minor: String = "",
    val state: SeatStateType = SeatStateType.Empty,
    val isAvailable: Boolean = false,
    val userId: String? = null,
    val reserveEndTime: Long? = null,
    val occupyEndTime: Long? = null,
)

