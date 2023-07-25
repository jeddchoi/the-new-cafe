package io.github.jeddchoi.data.service.seatfinder

import io.github.jeddchoi.data.firebase.model.FirebaseSeatFinderRequestType
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
data class SeatFinderRequest(
    val requestType: FirebaseSeatFinderRequestType,
    @EncodeDefault val seatPosition: SeatPosition? = null,
    @EncodeDefault val endTime: Long? = null,
    @EncodeDefault val durationInSeconds: Int? = null,
)
