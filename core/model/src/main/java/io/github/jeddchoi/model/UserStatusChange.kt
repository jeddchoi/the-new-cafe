package io.github.jeddchoi.model

import kotlinx.serialization.Serializable

@Serializable
data class UserStatusChange(
    val prevStatus: UserStatusType,
    val status: UserStatusType,
    val cause: UserStatusChangeCause,
    val updatedAt: Long,
    val expiresIn: Int? = null,
    val expiresAt: Long? = null,
    val expirationTask: String? = null,
    val seatPos: SeatPosition? = null,
)
