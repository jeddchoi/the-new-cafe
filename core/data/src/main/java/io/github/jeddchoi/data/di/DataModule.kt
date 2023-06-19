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
import io.github.jeddchoi.data.service.FirebaseSeatFinderServiceImpl
import io.github.jeddchoi.data.service.SeatFinderService
import io.github.jeddchoi.data.util.AuthInputValidator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Singleton
    @Binds
    abstract fun bindsAuthRepository(
        authRepository: FirebaseAuthRepositoryImpl,
    ): AuthRepository


    @Singleton
    @Binds
    abstract fun bindsCurrentUserRepository(
        currentUserRepository: FirebaseCurrentUserRepositoryImpl,
    ): CurrentUserRepository

    @Singleton
    @Binds
    abstract fun bindsSeatFinderService(
        seatFinderService: FirebaseSeatFinderServiceImpl,
    ): SeatFinderService


}

@Module
@InstallIn(SingletonComponent::class)
object ValidatorModule {

    @Singleton // Provide always the same instance
    @Provides
    fun providesCoroutineScope(): CoroutineScope {
        // Run this code when providing an instance of CoroutineScope
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    @Provides
    fun provideAuthInputValidator(): AuthInputValidator {
        return AuthInputValidator
    }
}