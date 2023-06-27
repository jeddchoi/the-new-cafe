package io.github.jeddchoi.data.firebase.repository

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.values
import io.github.jeddchoi.data.repository.CurrentUserRepository
import io.github.jeddchoi.data.repository.UserSessionRepository
import io.github.jeddchoi.model.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.transform
import javax.inject.Inject


class FirebaseUserSessionRepositoryImpl @Inject constructor(
    private val currentUserRepository: CurrentUserRepository,
    private val database: FirebaseDatabase,
) : UserSessionRepository {
    override val userSession: Flow<UserSession?> = currentUserRepository.currentUserId.transform {
        if (it != null) {
            emitAll(database.getReference("seatFinder/session/${it}").values())
        } else {
            emit(null)
        }
    }
}