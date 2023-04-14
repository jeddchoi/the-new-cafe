package io.github.jeddchoi.data.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jeddchoi.data.repository.AuthRepository
import io.github.jeddchoi.data.repository.fake.FakeAuthRepositoryImpl
import io.github.jeddchoi.data.util.AuthInputValidator
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Singleton
    @Binds
    abstract fun bindsAuthRepository(
        authRepository: FakeAuthRepositoryImpl,
    ): AuthRepository



}

@Module
@InstallIn(SingletonComponent::class)
object ValidatorModule {

    @Provides
    fun provideAuthInputValidator(): AuthInputValidator {
        return AuthInputValidator
    }
}