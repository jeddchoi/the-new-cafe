package io.github.jeddchoi.data.repository

import androidx.paging.PagingData
import io.github.jeddchoi.model.UserSessionHistory
import kotlinx.coroutines.flow.Flow

interface UserSessionHistoryRepository {
    fun getHistories() : Flow<PagingData<UserSessionHistory>>
}
