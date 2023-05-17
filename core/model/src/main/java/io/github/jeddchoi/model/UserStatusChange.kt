package io.github.jeddchoi.model

import kotlinx.datetime.Clock
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
    val prevStatus: UserStatusType = UserStatusType.None,
    val targetStatus: UserStatusType = UserStatusType.None,
    val cause: UserStatusChangeCause = UserStatusChangeCause.UserAction,
    val requestTimestamp: Long = Clock.System.now().epochSeconds,
    val seatPos: SeatPosition? = null,
    val endTimestamp: Long? = null,
    val durationInSeconds: Int? = null,
)
