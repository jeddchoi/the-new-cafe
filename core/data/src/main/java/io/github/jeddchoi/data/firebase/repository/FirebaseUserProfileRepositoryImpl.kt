package io.github.jeddchoi.data.firebase.repository

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.values
import io.github.jeddchoi.data.firebase.model.FirebasePrivateInfo
import io.github.jeddchoi.data.firebase.model.FirebaseUserProfile
import io.github.jeddchoi.data.firebase.model.toUserProfile
import io.github.jeddchoi.data.repository.CurrentUserRepository
import io.github.jeddchoi.data.repository.UserProfileRepository
import io.github.jeddchoi.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import javax.inject.Inject

class FirebaseUserProfileRepositoryImpl @Inject constructor(
    private val currentUserRepository: CurrentUserRepository,
    private val database: FirebaseDatabase,
) : UserProfileRepository {
    override val userProfile: Flow<UserProfile?> = currentUserRepository.currentUserId.transform {
        if (it != null) {
            emitAll(
                database.getReference("users/${it}/profile").values<FirebaseUserProfile>()
                    .map { profile ->
                        profile?.toUserProfile() ?: UserProfile()
                    })
        } else {
            emit(null)
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun createUserProfile(
        displayName: String,
        emailAddress: String,
        isAnonymous: Boolean
    ) {
        withContext(Dispatchers.IO) {
            Log.d(
                "FirebaseUserProfileRepositoryImpl",
                "createUserProfile $displayName $emailAddress $isAnonymous"
            )
            val current = Clock.System.now().toEpochMilliseconds()
            currentUserRepository.getUserId()?.let {
                Log.d("FirebaseUserProfileRepositoryImpl", "createUserProfile $it")
                database.getReference("users/${it}/profile").setValue(
                    FirebaseUserProfile(
                        displayName = displayName,
                        emailAddress = emailAddress,
                        privateInfo = FirebasePrivateInfo(
                            isAnonymous = isAnonymous,
                            creationTime = current,
                            lastSignInTime = current,
                            emailVerified = false,
                        ),
                    )
                )
            }
        }
    }

    override suspend fun updateUserProfile(updateValues: Map<String, Any>) {
        withContext(Dispatchers.IO) {
            currentUserRepository.getUserId()?.let {
                database.getReference("users/${it}/profile").updateChildren(updateValues)
            }
        }
    }

}