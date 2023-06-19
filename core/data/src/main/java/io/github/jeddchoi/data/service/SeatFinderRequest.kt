package io.github.jeddchoi.data.service

import io.github.jeddchoi.data.firebase.model.FirebaseSeatPosition
import kotlinx.serialization.Serializable

/**
 * Seat finder request
 *
 * @property requestType
 * @property seatPosition
 * @property endTime
 * @property durationInSeconds
 * @constructor Create empty Seat finder request
 */
@Serializable
data class SeatFinderRequest(
    val requestType: SeatFinderRequestType,
    val seatPosition: FirebaseSeatPosition? = null,
    val endTime: Long? = null,
    val durationInSeconds: Int? = null,
)
