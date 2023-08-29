package io.github.jeddchoi.firebase.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject


class FirebaseCurrentUserRepositoryImpl @Inject constructor(
    private val coroutineScope: CoroutineScope,
) : CurrentUserRepository {
    private val auth: FirebaseAuth = Firebase.auth

    override fun isUserSignedIn() = auth.currentUser != null

    override fun getUserId() = auth.currentUser?.uid

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
    }.flowOn(Dispatchers.IO).onEach { Timber.v("💥 $it") }

    override val currentUser: StateFlow<CurrentUser?> =
        _currentUser.onEach { Timber.v("💥 $it") }
            .stateIn(
                coroutineScope,
                SharingStarted.WhileSubscribed(5_000),
                null
            )

    override val currentUserId: Flow<String?> =
        flow {
            _currentUser.collect { user ->
                emit(user?.authId)
            }
        }
            .distinctUntilChanged()
            .onEach { Timber.v("💥 $it") }
}

private fun FirebaseUser.toCurrentUser() = CurrentUser(
    authId = this.uid,
    displayName = this.displayName ?: "Display name not provided",
    emailAddress = this.email ?: "Email not provided",
    isEmailVerified = this.isEmailVerified,
)