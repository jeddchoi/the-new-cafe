package io.github.jeddchoi.data.firebase

import com.google.firebase.auth.FirebaseAuth
import io.github.jeddchoi.data.repository.CurrentUserRepository
import io.github.jeddchoi.model.CurrentUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseCurrentUserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val coroutineScope: CoroutineScope,
) : CurrentUserRepository {
    override fun isUserSignedIn() = currentUser.value != null

    override fun getUserId() = currentUser.value?.authId

    private val _currentUser = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(
                auth.currentUser?.let {
                    CurrentUser(
                        authId = it.uid,
                        displayName = it.displayName ?: "Display name not provided",
                        emailAddress = it.email ?: "Email not provided",
                        isEmailVerified = it.isEmailVerified,
                    )
                }
            )
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }

    override val currentUser: StateFlow<CurrentUser?> =
        _currentUser.stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), null)

}