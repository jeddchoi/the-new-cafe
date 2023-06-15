package io.github.jeddchoi.data.repository

import android.util.Log
import io.github.jeddchoi.data.service.FirebaseSeatFinderServiceImpl
import io.github.jeddchoi.data.firebase.model.FirebaseSeatPosition
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeatRepository @Inject constructor(

    private val functions: FirebaseSeatFinderServiceImpl,
) {
}