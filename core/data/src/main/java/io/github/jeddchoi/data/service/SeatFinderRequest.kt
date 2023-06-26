package io.github.jeddchoi.data.service

import io.github.jeddchoi.data.firebase.model.FirebaseSeatPosition
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
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
@ExperimentalSerializationApi
@Serializable
data class SeatFinderRequest(
    val requestType: SeatFinderRequestType,
    @EncodeDefault val seatPosition: FirebaseSeatPosition? = null,
    @EncodeDefault val endTime: Long? = null,
    @EncodeDefault val durationInSeconds: Int? = null,
)
