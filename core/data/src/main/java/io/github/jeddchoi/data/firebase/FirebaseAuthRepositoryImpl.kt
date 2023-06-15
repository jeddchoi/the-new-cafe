package io.github.jeddchoi.data.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import io.github.jeddchoi.data.repository.AuthRepository
import io.github.jeddchoi.model.User
import io.github.jeddchoi.model.UserStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

const val REFERENCE_USER_STATUS_NAME = "users_status"


@Singleton
class FirebaseAuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase,
) : AuthRepository {
    val currentUser = callbackFlow {
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

    override suspend fun signInWithEmail(email: String, password: String): Result<Unit> {
        auth.signInWithEmailAndPassword(email, password).await()
        return Result.success(Unit)
    }

    override suspend fun registerWithEmail(
        email: String,
        displayName: String,
        password: String
    ): Result<Unit> {

        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val createdUser = result?.user
        if (createdUser != null) {
            val request = userProfileChangeRequest {
                this.displayName = displayName
            }
            createdUser.updateProfile(request).await()
            val newUserStatus = UserStatus()
            Log.i("FirebaseAuthRepositoryImpl", "newUserStatus: $newUserStatus")
            Log.i("FirebaseAuthRepositoryImpl", "uid: ${createdUser.uid}")
            database.reference.child(REFERENCE_USER_STATUS_NAME).child(createdUser.uid).setValue(newUserStatus).await()
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