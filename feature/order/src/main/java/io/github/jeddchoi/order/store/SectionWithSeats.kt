package io.github.jeddchoi.order.store

import io.github.jeddchoi.model.Seat
import io.github.jeddchoi.model.Section


data class SectionWithSeats(
    val section: Section,
    val seats: List<Seat> = emptyList(),
)
