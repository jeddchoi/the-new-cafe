package io.github.jeddchoi.model

import kotlinx.serialization.Serializable

@Serializable
data class SeatPosition(
    val storeId: String,
    val sectionId: String,
    val seatId: String,
)

enum class SeatStatusType {
    None,
    Reserved,
    Occupied,
    Vacant,
    OnTask,
    Blocked,
}