package io.github.jeddchoi.data.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jeddchoi.data.proto.appFlagsDataStore
import io.github.jeddchoi.data.repository.AppFlagsRepository
import io.github.jeddchoi.data.repository.AppFlagsRepositoryImpl
import io.github.jeddchoi.data.util.ConnectivityManagerNetworkMonitor
import io.github.jeddchoi.data.util.NetworkMonitor
import io.github.jeddchoi.data.util.TickHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    companion object {
        @Singleton // Provide always the same instance
        @Provides
        fun providesCoroutineScope(): CoroutineScope {
            // Run this code when providing an instance of CoroutineScope
            return CoroutineScope(SupervisorJob() + Dispatchers.Default)
        }

        @Provides
        fun providesAppFlagsDatastore(
            @ApplicationContext context: Context
        ) = context.appFlagsDataStore

        @Singleton
        @Provides
        fun providesTickHandler(
            coroutineScope: CoroutineScope,
        ) = TickHandler(coroutineScope)
    }

    @Binds
    fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor,
    ): NetworkMonitor

    @Binds
    fun bindsAppFlagsRepository(
        appFlagsRepositoryImpl: AppFlagsRepositoryImpl
    ): AppFlagsRepository

}

