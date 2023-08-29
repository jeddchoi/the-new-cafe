package io.github.jeddchoi.firebase.firestore

import com.google.firebase.firestore.DocumentId
import io.github.jeddchoi.model.Store
import kotlinx.serialization.Serializable

@Serializable
data class FirebaseStore(
    @DocumentId val id: String = "",
    val acceptsReservation: Boolean? = null,
    val name: String? = null,
    val photoUrl: String? = null,
    val totalAvailableSeats: Int? = null,
    val totalSeats: Int? = null,
    val totalSections: Int? = null,
    val uuid: String? = null,
)


fun FirebaseStore.toStore() = Store(
    id = id,
    acceptsReservation = acceptsReservation ?: false,
    name = name ?: "Not provided",
    photoUrl = photoUrl,
    totalAvailableSeats = totalAvailableSeats ?: 0,
    totalSeats = totalSeats ?: 0,
    totalSections = totalSections ?: 0,
    uuid = uuid ?: "Not provided",
)