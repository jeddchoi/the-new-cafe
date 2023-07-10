package io.github.jeddchoi.data.firebase.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.github.jeddchoi.data.repository.CurrentUserRepository
import io.github.jeddchoi.model.CurrentUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseCurrentUserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val coroutineScope: CoroutineScope,
) : CurrentUserRepository {
    override fun isUserSignedIn() = auth.currentUser != null

    override fun getUserId() = currentUser.value?.authId

    private val _currentUser = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(
                auth.currentUser?.toCurrentUser()
            )
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }

    override val currentUser: StateFlow<CurrentUser?> =
        _currentUser.stateIn(
            coroutineScope,
            SharingStarted.WhileSubscribed(5000),
            auth.currentUser?.toCurrentUser()
        )
    override val currentUserId: Flow<String?> =
        currentUser.map { it?.authId }.distinctUntilChanged()

}

private fun FirebaseUser.toCurrentUser() = CurrentUser(
    authId = this.uid,
    displayName = this.displayName ?: "Display name not provided",
    emailAddress = this.email ?: "Email not provided",
    isEmailVerified = this.isEmailVerified,
)