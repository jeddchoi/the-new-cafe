package io.github.jeddchoi.data.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jeddchoi.data.util.ConnectivityManagerNetworkMonitor
import io.github.jeddchoi.data.util.NetworkMonitor
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
    }

    @Binds
    fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor,
    ): NetworkMonitor
}

