package io.github.jeddchoi.data.repository

import android.util.Log
import io.github.jeddchoi.data.firebase.CloudFunctions
import io.github.jeddchoi.model.SeatPosition
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeatRepository @Inject constructor(
    private val authRepository: AuthRepository,
    private val functions: CloudFunctions,
) {

    suspend fun reserveSeat(seatPosition: SeatPosition, expiresIn: Int) {
        authRepository.getUserId()?.let {
            Log.d("ReserveSeat", "id = $it")
            val result = functions.reserveSeat(it, seatPosition, expiresIn)
            Log.i("ReserveSeat", "result = $result")
        }
    }
}