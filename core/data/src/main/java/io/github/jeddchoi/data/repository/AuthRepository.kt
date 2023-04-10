package io.github.jeddchoi.data.repository

import io.github.jeddchoi.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun getCurrentUser() : Flow<User>


    suspend fun signIn(email: String, password: String)

    suspend fun register(email: String, firstName: String, lastName: String, password: String)

    suspend fun signOut()


}