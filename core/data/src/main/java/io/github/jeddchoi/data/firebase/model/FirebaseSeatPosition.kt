package io.github.jeddchoi.data.firebase.model

import kotlinx.serialization.Serializable

@Serializable
data class FirebaseSeatPosition(
    val storeId: String = "store_1",
    val sectionId: String = "section_1",
    val seatId: String = "seat_1",
)