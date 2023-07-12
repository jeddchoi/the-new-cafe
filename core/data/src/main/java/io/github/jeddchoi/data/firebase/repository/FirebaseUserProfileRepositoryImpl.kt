package io.github.jeddchoi.data.firebase.repository

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.values
import io.github.jeddchoi.data.firebase.model.FirebasePrivateInfo
import io.github.jeddchoi.data.firebase.model.FirebaseUserProfile
import io.github.jeddchoi.data.firebase.model.toUserProfile
import io.github.jeddchoi.data.repository.CurrentUserRepository
import io.github.jeddchoi.data.repository.UserProfileRepository
import io.github.jeddchoi.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
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
                        profile?.toUserProfile()
                    })
        } else { // not signed in
            emit(null)
        }
    }

    override suspend fun createUserProfile(
        displayName: String,
        emailAddress: String,
        isAnonymous: Boolean
    ) {
        currentUserRepository.getUserId()?.let {
            database.getReference("users/${it}/profile").setValue(
                mapOf(
                    FirebaseUserProfile::displayName.name to displayName,
                    FirebaseUserProfile::emailAddress.name to emailAddress,
                    "${FirebaseUserProfile::privateInfo.name}/${FirebasePrivateInfo::creationTime}" to ServerValue.TIMESTAMP,
                    "${FirebaseUserProfile::privateInfo.name}/${FirebasePrivateInfo::isAnonymous}" to isAnonymous,
                    "${FirebaseUserProfile::privateInfo.name}/${FirebasePrivateInfo::lastSignInTime}" to ServerValue.TIMESTAMP,
                )
            )
        }
    }

    override suspend fun updateUserProfile(updateValues: Map<String, Any>) {
        currentUserRepository.getUserId()?.let {
            database.getReference("users/${it}/profile").updateChildren(updateValues)
        }
    }

}