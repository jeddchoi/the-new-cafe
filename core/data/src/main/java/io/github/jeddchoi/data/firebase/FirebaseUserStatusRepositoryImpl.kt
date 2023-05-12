package io.github.jeddchoi.data.firebase

import com.google.firebase.database.FirebaseDatabase
import io.github.jeddchoi.data.repository.UserStatusRepository
import javax.inject.Inject

class FirebaseUserStatusRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase,
) : UserStatusRepository{
    override suspend fun createUserStatus(userId: String) {
        database.reference
    }
}