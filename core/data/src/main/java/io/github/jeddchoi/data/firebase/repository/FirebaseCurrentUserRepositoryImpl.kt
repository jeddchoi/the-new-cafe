package io.github.jeddchoi.data.firebase.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.github.jeddchoi.data.repository.CurrentUserRepository
import io.github.jeddchoi.model.CurrentUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


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
    }.flowOn(Dispatchers.IO)

    override val currentUser: StateFlow<CurrentUser?> =
        _currentUser.stateIn(
            coroutineScope,
            SharingStarted.WhileSubscribed(5000),
            auth.currentUser?.toCurrentUser()
        )

    override val currentUserId: Flow<String?> =
        _currentUser.map { it?.authId }.onEach { Log.e("FirebaseCurrentUserRepositoryImpl", "currentUserId: $it") }.distinctUntilChanged()
            .onEach { Log.e("FirebaseCurrentUserRepositoryImpl", "after currentUserId: $it") }

}

private fun FirebaseUser.toCurrentUser() = CurrentUser(
    authId = this.uid,
    displayName = this.displayName ?: "Display name not provided",
    emailAddress = this.email ?: "Email not provided",
    isEmailVerified = this.isEmailVerified,
)