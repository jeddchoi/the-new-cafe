package io.github.jeddchoi.model

data class Section(
    val id: String = "",
    val major: String = "",
    val name: String = "",
    val totalAvailableSeats: Int = 0,
    val totalSeats: Int = 0,
)