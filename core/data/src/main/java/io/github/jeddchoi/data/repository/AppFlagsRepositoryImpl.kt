package io.github.jeddchoi.data.repository

import androidx.datastore.core.DataStore
import io.github.jeddchoi.data.AppFlags
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject


class AppFlagsRepositoryImpl @Inject constructor(
    private val appFlagsDataStore: DataStore<AppFlags>
) : AppFlagsRepository {
    override suspend fun setShowMainScreenOnStart(value: Boolean): Unit = withContext(Dispatchers.IO) {
        appFlagsDataStore.updateData {
            it.toBuilder().setShowMainScreenOnStart(value).build()
        }
    }

    override val getShowMainScreenOnStart: Flow<Boolean> =
        appFlagsDataStore.data.map { it.showMainScreenOnStart }.flowOn(Dispatchers.IO)
}