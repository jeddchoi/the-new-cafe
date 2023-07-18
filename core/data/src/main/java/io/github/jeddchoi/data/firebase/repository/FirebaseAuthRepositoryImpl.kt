package io.github.jeddchoi.data.firebase.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import io.github.jeddchoi.data.repository.AuthRepository
import io.github.jeddchoi.data.repository.UserProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val userProfileRepository: UserProfileRepository,
) : AuthRepository {


    override suspend fun signInWithEmail(email: String, password: String): Result<Unit> =
        withContext(Dispatchers.IO) {

            auth.signInWithEmailAndPassword(email, password).await()
            return@withContext Result.success(Unit)
        }


    override suspend fun registerWithEmail(
        email: String,
        displayName: String,
        password: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        Log.d("registerWithEmail", "email: $email, displayName: $displayName, password: $password")
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val createdUser = result?.user
        if (createdUser != null) {
            val request = userProfileChangeRequest {
                this.displayName = displayName
            }
            createdUser.updateProfile(request).await()

            userProfileRepository.createUserProfile(
                displayName = displayName,
                emailAddress = email,
                isAnonymous = false,
            )
            auth.signInWithEmailAndPassword(email, password).await()
            return@withContext Result.success(Unit)
        } else {
            return@withContext Result.failure(RuntimeException("Registration failed"))
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            TODO("Not yet implemented")
        }

    override suspend fun logout() = withContext(Dispatchers.IO) {
        Log.d("FirebaseAuthRepositoryImpl", "logout")
        auth.signOut()
    }


    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            auth.sendPasswordResetEmail(email).await()
            return@withContext Result.success(Unit)
        }
}