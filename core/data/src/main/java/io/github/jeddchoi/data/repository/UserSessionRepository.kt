package io.github.jeddchoi.data.repository

import io.github.jeddchoi.model.UserSession
import kotlinx.coroutines.flow.Flow

interface UserSessionRepository {
    val userSession: Flow<UserSession?>
}