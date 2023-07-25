package io.github.jeddchoi.data.service.seatfinder

import io.github.jeddchoi.model.SeatFinderUserRequestType
import io.github.jeddchoi.model.SeatPosition

interface SeatFinderService {
    suspend fun requestInSession(
        seatFinderRequestType: SeatFinderUserRequestType,
        endTime: Long? = null,
        durationInSeconds: Int? = null
    ) : SeatFinderResult

    suspend fun reserveSeat(
        seatPosition: SeatPosition,
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

    suspend fun leaveAway(
        endTime: Long? = null,
        durationInSeconds: Int? = null
    ): SeatFinderResult

    suspend fun resumeUsing(): SeatFinderResult

    suspend fun changeReservationEndTime(
        endTime: Long? = null,
        durationInSeconds: Int? = null
    ): SeatFinderResult

    suspend fun changeOccupyEndTime(
        endTime: Long? = null,
        durationInSeconds: Int? = null
    ): SeatFinderResult

    suspend fun changeBusinessEndTime(
        endTime: Long? = null,
        durationInSeconds: Int? = null
    ): SeatFinderResult

    suspend fun changeAwayEndTime(
        endTime: Long? = null,
        durationInSeconds: Int? = null
    ): SeatFinderResult
}