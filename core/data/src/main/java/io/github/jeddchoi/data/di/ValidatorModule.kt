package io.github.jeddchoi.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import io.github.jeddchoi.data.util.AuthInputValidator

@Module
@InstallIn(ViewModelComponent::class)
object ValidatorModule {
    @Provides
    fun provideAuthInputValidator(): AuthInputValidator {
        return AuthInputValidator
    }
}