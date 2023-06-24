package io.github.jeddchoi.data.firebase

import com.google.android.gms.tasks.Tasks.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import io.github.jeddchoi.data.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

const val REFERENCE_USER_STATUS_NAME = "users_status"


@Singleton
class FirebaseAuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase,
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
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val createdUser = result?.user
        if (createdUser != null) {
            val request = userProfileChangeRequest {
                this.displayName = displayName
            }
            createdUser.updateProfile(request).await()

//            val newUserState = UserState()
//            Log.i("FirebaseAuthRepositoryImpl", "newUserStatus: $newUserState")
//            Log.i("FirebaseAuthRepositoryImpl", "uid: ${createdUser.uid}")
//            database.reference.child(REFERENCE_USER_STATUS_NAME).child(createdUser.uid).setValue(newUserState).await()
        }
        return Result.success(Unit)
    }

    override suspend fun signInWithGoogle(idToken: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun logout() {
        auth.signOut()
    }


    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        auth.sendPasswordResetEmail(email).await()
        return Result.success(Unit)
    }
}