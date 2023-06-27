package io.github.jeddchoi.data.repository

import io.github.jeddchoi.data.service.seatfinder.FirebaseSeatFinderServiceImpl
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeatRepository @Inject constructor(

    private val functions: FirebaseSeatFinderServiceImpl,
) {
}