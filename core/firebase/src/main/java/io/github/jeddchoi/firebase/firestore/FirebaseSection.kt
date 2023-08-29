package io.github.jeddchoi.firebase.firestore

import com.google.firebase.firestore.DocumentId
import io.github.jeddchoi.model.Section
import kotlinx.serialization.Serializable

@Serializable
data class FirebaseSection(
    @DocumentId val id: String = "",
    val major: String? = null,
    val name: String? = null,
    val totalAvailableSeats: Int? = null,
    val totalSeats: Int? = null,
)


fun FirebaseSection.toSection() = Section(
    id = id,
    major = major ?: "Not provided",
    name = name ?: "Not provided",
    totalAvailableSeats = totalAvailableSeats ?: 0,
    totalSeats = totalSeats ?: 0,
)