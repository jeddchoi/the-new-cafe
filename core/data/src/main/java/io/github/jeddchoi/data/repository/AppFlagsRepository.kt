package io.github.jeddchoi.data.repository

import kotlinx.coroutines.flow.Flow

interface AppFlagsRepository {

    suspend fun setShowMainScreenOnStart(value: Boolean)
    val getShowMainScreenOnStart : Flow<Boolean>
}