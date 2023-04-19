package io.github.jeddchoi.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import io.github.jeddchoi.data.repository.AuthRepository
import io.github.jeddchoi.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {
    override suspend fun signInWithEmail(email: String, password: String): Result<Unit> {
        auth.signInWithEmailAndPassword(email, password).await()
        return Result.success(Unit)
    }

    override suspend fun registerWithEmail(
        email: String,
        displayName: String,
        password: String
    ): Result<Unit> {
        auth.createUserWithEmailAndPassword(email, password).await()
        userProfileChangeRequest {
            this.displayName = displayName
        }
        return Result.success(Unit)
    }

    override suspend fun signInWithGoogle(idToken: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun logout() {
        auth.signOut()
    }

    override fun isUserSignedIn(): Boolean {
        return auth.currentUser != null
    }

    override fun getUserId(): String? {
        return auth.currentUser?.uid
    }

    override fun getCurrentUser(): Flow<User?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(
                auth.currentUser?.let {
                    User(
                        emailAddress = it.email ?: "Email not provided",
                        displayName = it.displayName ?: "Display name not provided",
                        id = it.uid,
                    )
                }
            )
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        auth.sendPasswordResetEmail(email).await()
        return Result.success(Unit)
    }
}