package io.github.jeddchoi.firebase.realtime

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.values
import com.google.firebase.ktx.Firebase
import io.github.jeddchoi.data.repository.CurrentUserRepository
import io.github.jeddchoi.data.repository.UserProfileRepository
import io.github.jeddchoi.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import timber.log.Timber
import javax.inject.Inject

class FirebaseUserProfileRepositoryImpl @Inject constructor(
    private val currentUserRepository: CurrentUserRepository,
) : UserProfileRepository {
    private val database: FirebaseDatabase = Firebase.database
    override val userProfile: Flow<UserProfile?> =
        currentUserRepository.currentUserId.flatMapLatest {
            if (it != null) {
                database.getReference("users/${it}/profile").values<FirebaseUserProfile>()
                    .map { profile ->
                        profile?.toUserProfile() ?: UserProfile()
                    }
            } else {
                flowOf(null)
            }
        }.flowOn(Dispatchers.IO).onEach { Timber.v("💥 $it") }

    override suspend fun createUserProfile(
        displayName: String,
        emailAddress: String,
        isAnonymous: Boolean
    ) {
        Timber.v("✅ $displayName, $emailAddress, $isAnonymous")
        withContext(Dispatchers.IO) {
            val current = Clock.System.now().toEpochMilliseconds()
            currentUserRepository.getUserId()?.let {
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
        Timber.v("✅ $updateValues")
        withContext(Dispatchers.IO) {
            currentUserRepository.getUserId()?.let {
                database.getReference("users/${it}/profile").updateChildren(updateValues)
            }
        }
    }

}