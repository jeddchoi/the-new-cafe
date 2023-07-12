package io.github.jeddchoi.data.repository

import io.github.jeddchoi.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    val userProfile: Flow<UserProfile?>

    suspend fun createUserProfile(
        displayName: String,
        emailAddress: String,
        isAnonymous: Boolean,
    )

    suspend fun updateUserProfile(updateValues: Map<String, Any>)
}