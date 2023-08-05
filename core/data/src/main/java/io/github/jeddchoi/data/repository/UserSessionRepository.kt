package io.github.jeddchoi.data.repository

import io.github.jeddchoi.model.DisplayedUserSession
import io.github.jeddchoi.model.UserSession
import io.github.jeddchoi.model.UserStateAndUsedSeatPosition
import kotlinx.coroutines.flow.Flow

interface UserSessionRepository {
    val userSession: Flow<UserSession?>
    val userStateAndUsedSeatPosition: Flow<UserStateAndUsedSeatPosition?>
    val userSessionWithTimer: Flow<DisplayedUserSession?>
}