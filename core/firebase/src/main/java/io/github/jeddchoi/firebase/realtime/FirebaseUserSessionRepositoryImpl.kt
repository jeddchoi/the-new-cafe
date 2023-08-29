package io.github.jeddchoi.firebase.realtime

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.values
import com.google.firebase.ktx.Firebase
import io.github.jeddchoi.data.repository.CurrentUserRepository
import io.github.jeddchoi.data.repository.UserSessionRepository
import io.github.jeddchoi.data.util.TickHandler
import io.github.jeddchoi.model.DisplayedUserSession
import io.github.jeddchoi.model.UserSession
import io.github.jeddchoi.model.UserStateAndUsedSeatPosition
import io.github.jeddchoi.model.toDisplayedUserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject


class FirebaseUserSessionRepositoryImpl @Inject constructor(
    private val tickHandler: TickHandler,
    private val currentUserRepository: CurrentUserRepository,
) : UserSessionRepository {
    private val database: FirebaseDatabase = Firebase.database
    override val userSession: Flow<UserSession?> =
        currentUserRepository.currentUserId.flatMapLatest { currentUserId ->
            if (currentUserId != null) {

                database.getReference("seatFinder/${currentUserId}/session")
                    .values<FirebaseCurrentSession>()
                    .map { session ->
                        session.toUserSession()
                    }
            } else { // not signed in
                flowOf(null)
            }
        }.flowOn(Dispatchers.IO).onEach { Timber.v("💥 $it") }

    override val userStateAndUsedSeatPosition = userSession.map {
        when (it) {
            is UserSession.UsingSeat -> {
                UserStateAndUsedSeatPosition.UsingSeat(
                    seatPosition = it.seatPosition,
                    userState = it.currentState
                )
            }

            UserSession.None -> {
                UserStateAndUsedSeatPosition.None
            }

            null -> {
                null
            }
        }
    }.onEach { Timber.v("💥 $it") }

    override val userSessionWithTimer: Flow<DisplayedUserSession?> =
        tickHandler.tickFlow.combine(userSession.onEach { Timber.v("💥 $it") }) { current, userSession ->
            userSession?.toDisplayedUserSession(current)
        }

}


