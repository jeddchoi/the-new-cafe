package io.github.jeddchoi.data.repository

import io.github.jeddchoi.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    val userProfile: Flow<UserProfile?>
}