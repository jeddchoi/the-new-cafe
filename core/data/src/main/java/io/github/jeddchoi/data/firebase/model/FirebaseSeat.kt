package io.github.jeddchoi.data.firebase.model

import com.google.firebase.firestore.DocumentId
import io.github.jeddchoi.model.Seat
import io.github.jeddchoi.model.SeatStateType
import kotlinx.serialization.Serializable

@Serializable
data class FirebaseSeat(
    @DocumentId val id: String = "",
    val name: String? = null,
    val minor: String? = null,
    val state: String? = null,
    @field:JvmField val isAvailable: Boolean? = null,
    val userId: String? = null,
    val reserveEndTime: Long? = null,
    val occupyEndTime: Long? = null,
)


fun FirebaseSeat.toSeat() = Seat(
    id = id,
    name = name ?: "Not provided",
    minor = minor ?: "Not provided",
    state = SeatStateType.getByValue(state) ?: SeatStateType.Empty,
    isAvailable = isAvailable ?: false,
    userId = userId,
    reserveEndTime = reserveEndTime,
    occupyEndTime = occupyEndTime,
)