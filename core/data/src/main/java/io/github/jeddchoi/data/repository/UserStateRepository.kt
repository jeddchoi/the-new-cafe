package io.github.jeddchoi.data.repository

interface UserStateRepository {
    suspend fun createUserState(userId: String)
}