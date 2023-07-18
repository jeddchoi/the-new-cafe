package io.github.jeddchoi.model

data class Store(
    val id: String = "",
    val acceptsReservation: Boolean = false,
    val name: String = "",
    val photoUrl: String? = null,
    val totalAvailableSeats: Int = 0,
    val totalSeats: Int = 0,
    val totalSections: Int = 0,
    val uuid: String = "",
) {
    fun seatsStat() = "${totalAvailableSeats}/${totalSeats}"
}
