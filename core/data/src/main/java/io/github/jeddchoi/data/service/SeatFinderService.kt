package io.github.jeddchoi.data.service

import io.github.jeddchoi.data.firebase.model.FirebaseSeatPosition

interface SeatFinderService {
    suspend fun reserveSeat(
        seatPosition: FirebaseSeatPosition,
        endTime: Long? = null,
        durationInSeconds: Int? = null
    ): SeatFinderResult

    suspend fun occupySeat(
        endTime: Long? = null,
        durationInSeconds: Int? = null
    ): SeatFinderResult

    suspend fun quit(): SeatFinderResult

    suspend fun doBusiness(
        endTime: Long? = null,
        durationInSeconds: Int? = null
    ): SeatFinderResult

    suspend fun shiftToBusiness(
        endTime: Long? = null,
        durationInSeconds: Int? = null
    ): SeatFinderResult

    suspend fun leaveAway(
        endTime: Long? = null,
        durationInSeconds: Int? = null
    ): SeatFinderResult

    suspend fun resumeUsing(): SeatFinderResult

    suspend fun changeReservationTimeoutTime(
        endTime: Long? = null,
        durationInSeconds: Int? = null
    ): SeatFinderResult

    suspend fun changeOccupyTimeoutTime(
        endTime: Long? = null,
        durationInSeconds: Int? = null
    ): SeatFinderResult

    suspend fun changeBusinessTimeoutTime(
        endTime: Long? = null,
        durationInSeconds: Int? = null
    ): SeatFinderResult

    suspend fun changeAwayTimeoutTime(
        endTime: Long? = null,
        durationInSeconds: Int? = null
    ): SeatFinderResult
}