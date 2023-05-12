package io.github.jeddchoi.data.repository

interface UserStatusRepository {
    suspend fun createUserStatus(userId: String)
}