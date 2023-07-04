package io.github.jeddchoi.data.repository

import androidx.datastore.core.DataStore
import io.github.jeddchoi.data.AppFlags
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class AppFlagsRepositoryImpl @Inject constructor(
    private val appFlagsDataStore: DataStore<AppFlags>
): AppFlagsRepository {
    override suspend fun setShowAuthScreenOnStart(value: Boolean) {
        appFlagsDataStore.updateData {
            it.toBuilder().setShowAuthScreenOnStart(value).build()
        }
    }

    override val getShowAuthScreenOnStart: Flow<Boolean> = appFlagsDataStore.data.map { it.showAuthScreenOnStart }
}