package io.github.jeddchoi.data.firebase.repository

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.values
import io.github.jeddchoi.data.firebase.model.FirebaseCurrentSession
import io.github.jeddchoi.data.firebase.model.toUserSession
import io.github.jeddchoi.data.repository.CurrentUserRepository
import io.github.jeddchoi.data.repository.UserSessionRepository
import io.github.jeddchoi.model.UserSession
import io.github.jeddchoi.model.UserStateAndUsedSeatPosition
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import javax.inject.Inject


class FirebaseUserSessionRepositoryImpl @Inject constructor(
    private val currentUserRepository: CurrentUserRepository,
    private val database: FirebaseDatabase,
) : UserSessionRepository {
    override val userSession: Flow<UserSession?> = currentUserRepository.currentUserId.transform {
        if (it != null) {
            emitAll(
                database.getReference("seatFinder/session/${it}").values<FirebaseCurrentSession>()
                    .map { session ->
                        session.toUserSession()
                    })
        } else { // not signed in
            emit(null)
        }
    }
    override val userStateAndUsedSeatPosition= userSession.map {
        UserStateAndUsedSeatPosition(
            seatPosition = if (it is UserSession.UsingSeat) it.seatPosition else null,
            userState = it?.currentState
        )
    }.distinctUntilChanged()

}


