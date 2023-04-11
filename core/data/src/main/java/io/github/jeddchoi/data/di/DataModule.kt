package io.github.jeddchoi.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jeddchoi.data.repository.AuthRepository
import io.github.jeddchoi.data.repository.fake.FakeAuthRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    fun bindsAuthRepository(
        authRepository: FakeAuthRepositoryImpl,
    ): AuthRepository
}
