package io.github.jeddchoi.data.service

import io.github.jeddchoi.data.firebase.model.FirebaseSeatPosition

interface SeatFinderService {
    suspend fun reserveSeat(
        seatPosition: FirebaseSeatPosition,
        endTime: Long? = null,
        durationInSeconds: Int? = null
    ): ResultCode

    suspend fun occupySeat(
        endTime: Long? = null,
        durationInSeconds: Int? = null
    ): ResultCode

    suspend fun quit(): ResultCode

    suspend fun doBusiness(
        endTime: Long? = null,
        durationInSeconds: Int? = null
    ): ResultCode

    suspend fun shiftToBusiness(
        endTime: Long? = null,
        durationInSeconds: Int? = null
    ): ResultCode

    suspend fun leaveAway(
        endTime: Long? = null,
        durationInSeconds: Int? = null
    ): ResultCode

    suspend fun resumeUsing(): ResultCode

    suspend fun changeReservationTimeoutTime(
        endTime: Long? = null,
        durationInSeconds: Int? = null
    ): ResultCode

    suspend fun changeOccupyTimeoutTime(
        endTime: Long? = null,
        durationInSeconds: Int? = null
    ): ResultCode

    suspend fun changeBusinessTimeoutTime(
        endTime: Long? = null,
        durationInSeconds: Int? = null
    ): ResultCode

    suspend fun changeAwayTimeoutTime(
        endTime: Long? = null,
        durationInSeconds: Int? = null
    ): ResultCode
}