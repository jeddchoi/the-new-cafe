package io.github.jeddchoi.data.repository

import androidx.datastore.core.DataStore
import io.github.jeddchoi.data.AppFlags
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class AppFlagsRepositoryImpl @Inject constructor(
    private val appFlagsDataStore: DataStore<AppFlags>
): AppFlagsRepository {
    override suspend fun setShowMainScreenOnStart(value: Boolean) {
        appFlagsDataStore.updateData {
            it.toBuilder().setShowMainScreenOnStart(value).build()
        }
    }

    override val getShowMainScreenOnStart: Flow<Boolean> = appFlagsDataStore.data.map { it.showMainScreenOnStart }
}