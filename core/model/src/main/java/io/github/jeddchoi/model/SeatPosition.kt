package io.github.jeddchoi.model

import kotlinx.serialization.Serializable

@Serializable
data class SeatPosition(
    val storeId: String = "store_1",
    val sectionId: String = "section_1",
    val seatId: String = "seat_1",
)

enum class SeatStatusType {
    None,
    Reserved,
    Occupied,
    Vacant,
    OnTask,
    Blocked,
}