package io.github.jeddchoi.data.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jeddchoi.data.firebase.FirebaseAuthRepositoryImpl
import io.github.jeddchoi.data.firebase.FirebaseCurrentUserRepositoryImpl
import io.github.jeddchoi.data.repository.AuthRepository
import io.github.jeddchoi.data.repository.CurrentUserRepository
import io.github.jeddchoi.data.service.seatfinder.FirebaseSeatFinderServiceImpl
import io.github.jeddchoi.data.service.seatfinder.SeatFinderService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.ExperimentalSerializationApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindsAuthRepository(
        authRepository: FirebaseAuthRepositoryImpl,
    ): AuthRepository


    @ExperimentalSerializationApi
    @Binds
    abstract fun bindsSeatFinderService(
        seatFinderService: FirebaseSeatFinderServiceImpl,
    ): SeatFinderService


    @Singleton // this shares an internal state that requires that same instance to be used within a certain scope
    @Binds
    abstract fun bindsCurrentUserRepository(
        currentUserRepository: FirebaseCurrentUserRepositoryImpl,
    ): CurrentUserRepository


    @Singleton // Provide always the same instance
    @Provides
    fun providesCoroutineScope(): CoroutineScope {
        // Run this code when providing an instance of CoroutineScope
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
}

