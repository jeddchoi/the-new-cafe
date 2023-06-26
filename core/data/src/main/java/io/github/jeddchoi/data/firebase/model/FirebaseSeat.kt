package io.github.jeddchoi.data.firebase.model

import kotlinx.serialization.Serializable

@Serializable
data class FirebaseSeat(
    val name: String,
    val minor: String,
    val state: String,
    val isAvailable: Boolean,
    val userId: String?,
    val reserveEndTime: Long?,
    val occupyEndTime: Long?,
)