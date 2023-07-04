package io.github.jeddchoi.data.repository

import kotlinx.coroutines.flow.Flow

interface AppFlagsRepository {

    suspend fun setShowAuthScreenOnStart(value: Boolean)
    val getShowAuthScreenOnStart : Flow<Boolean>
}