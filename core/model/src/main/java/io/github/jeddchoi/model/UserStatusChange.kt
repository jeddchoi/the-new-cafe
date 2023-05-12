package io.github.jeddchoi.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * User status change
 *
 * @property prevStatus
 * @property targetStatus
 * @property cause
 * @property requestTimestamp
 * @property expiresInSeconds
 * @property expiresAt
 * @property seatPos
 * @constructor Create empty User status change
 */
@Serializable
data class UserStatusChange(
    val prevStatus: UserStatusType,
    val targetStatus: UserStatusType,
    val cause: UserStatusChangeCause,
    val requestTimestamp: Instant,
    val seatPos: SeatPosition? = null,
    val endTime: Instant? = null,
    val durationInSeconds: Int? = null,
)
