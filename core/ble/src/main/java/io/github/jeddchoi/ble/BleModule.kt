package io.github.jeddchoi.ble

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface BleModule {

    companion object {
        @Singleton // Provide always the same instance
        @Provides
        fun providesBleRepository(): BleRepository {
            return BleRepositoryImpl()
        }
    }
}