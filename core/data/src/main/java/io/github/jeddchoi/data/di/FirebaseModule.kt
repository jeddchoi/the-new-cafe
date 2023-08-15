package io.github.jeddchoi.data.di

import androidx.paging.PagingConfig
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jeddchoi.data.firebase.repository.FirebaseAuthRepositoryImpl
import io.github.jeddchoi.data.firebase.repository.FirebaseCurrentUserRepositoryImpl
import io.github.jeddchoi.data.firebase.repository.FirebaseStoreRepositoryImpl
import io.github.jeddchoi.data.firebase.repository.FirebaseUserPresenceRepositoryImpl
import io.github.jeddchoi.data.firebase.repository.FirebaseUserProfileRepositoryImpl
import io.github.jeddchoi.data.firebase.repository.FirebaseUserSessionHistoryDetailRepositoryImpl
import io.github.jeddchoi.data.firebase.repository.FirebaseUserSessionHistoryRepositoryImpl
import io.github.jeddchoi.data.firebase.repository.FirebaseUserSessionRepositoryImpl
import io.github.jeddchoi.data.firebase.repository.UserSessionHistoryPagingSource.Companion.PAGE_SIZE
import io.github.jeddchoi.data.firebase.service.FirebaseSeatFinderServiceImpl
import io.github.jeddchoi.data.repository.AuthRepository
import io.github.jeddchoi.data.repository.CurrentUserRepository
import io.github.jeddchoi.data.repository.StoreRepository
import io.github.jeddchoi.data.repository.UserPresenceRepository
import io.github.jeddchoi.data.repository.UserProfileRepository
import io.github.jeddchoi.data.repository.UserSessionHistoryDetailRepository
import io.github.jeddchoi.data.repository.UserSessionHistoryRepository
import io.github.jeddchoi.data.repository.UserSessionRepository
import io.github.jeddchoi.data.service.seatfinder.SeatFinderService
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class FirebaseModule {
    companion object {
        @Provides
        fun provideProductsRepository(
            currentUserRepository: CurrentUserRepository,
            config: PagingConfig,
        ): UserSessionHistoryRepository = FirebaseUserSessionHistoryRepositoryImpl(
            config = config,
            currentUserRepository = currentUserRepository
        )


        @Provides
        fun providePagingConfig() = PagingConfig(
            pageSize = PAGE_SIZE,
            enablePlaceholders = false,
        )
    }

    @Binds
    abstract fun bindsAuthRepository(
        authRepository: FirebaseAuthRepositoryImpl,
    ): AuthRepository


    @Binds
    abstract fun bindsSeatFinderService(
        seatFinderService: FirebaseSeatFinderServiceImpl,
    ): SeatFinderService


    @Singleton // this shares an internal state that requires that same instance to be used within a certain scope
    @Binds
    abstract fun bindsCurrentUserRepository(
        currentUserRepository: FirebaseCurrentUserRepositoryImpl,
    ): CurrentUserRepository

    @Binds
    abstract fun bindsSessionRepository(
        sessionRepositoryImpl: FirebaseUserSessionRepositoryImpl,
    ): UserSessionRepository

    @Binds
    abstract fun bindsStoreRepository(
        storeRepositoryImpl: FirebaseStoreRepositoryImpl,
    ): StoreRepository

    @Binds
    abstract fun bindsUserProfileRepository(
        userProfileRepositoryImpl: FirebaseUserProfileRepositoryImpl,
    ): UserProfileRepository

    @Binds
    abstract fun bindsUserSessionHistoryDetailRepository(
        historyDetailRepositoryImpl: FirebaseUserSessionHistoryDetailRepositoryImpl,
    ): UserSessionHistoryDetailRepository

    @Binds
    abstract fun bindsUserPresenceRepository(
        repositoryImpl: FirebaseUserPresenceRepositoryImpl,
    ): UserPresenceRepository

}