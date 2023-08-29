package io.github.jeddchoi.firebase.functions

import io.github.jeddchoi.model.SeatPosition
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
data class FirebaseSeatFinderRequest(
    val requestType: FirebaseSeatFinderRequestType,
    @EncodeDefault val seatPosition: SeatPosition? = null,
    @EncodeDefault val endTime: Long? = null,
    @EncodeDefault val durationInSeconds: Int? = null,
)
