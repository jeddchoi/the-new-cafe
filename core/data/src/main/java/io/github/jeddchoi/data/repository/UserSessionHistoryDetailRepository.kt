package io.github.jeddchoi.data.repository

import io.github.jeddchoi.model.UserStateChange
import kotlinx.coroutines.flow.Flow

interface UserSessionHistoryDetailRepository {

    fun getStateChanges(sessionId: String) : Flow<List<UserStateChange>>
}