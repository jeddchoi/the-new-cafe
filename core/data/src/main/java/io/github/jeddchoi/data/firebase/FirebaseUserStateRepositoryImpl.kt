package io.github.jeddchoi.data.firebase

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.values
import io.github.jeddchoi.data.repository.UserStateRepository
import io.github.jeddchoi.model.UserState
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FirebaseUserStateRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase,
) : UserStateRepository{
    val user = database.reference.child("").values<UserState>().map { it }
    override suspend fun createUserState(userId: String) {
        database.reference
    }

    fun observeUserState(userId: String) = database.reference.child("user_state").values<UserState>()
}