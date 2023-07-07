package io.github.jeddchoi.data.firebase.model

import io.github.jeddchoi.model.Store
import kotlinx.serialization.Serializable

@Serializable
data class FirebaseStore(
    val acceptsReservation: Boolean? = false,
    val name: String? = "",
    val photoUrl: String? = "",
    val totalAvailableSeats: Int? = 0,
    val totalSeats: Int? = 0,
    val totalSections: Int? = 0,
    val uuid: String? = "",
)


fun FirebaseStore.toStore() = Store(
    acceptsReservation = acceptsReservation ?: false,
    name = name ?: "Not provided store name",
    photoUrl = photoUrl,
    totalAvailableSeats = totalAvailableSeats ?: 0,
    totalSeats = totalSeats ?: 0,
    totalSections = totalSections ?: 0,
    uuid = uuid ?: "",
)