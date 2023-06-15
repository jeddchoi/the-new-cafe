package io.github.jeddchoi.data.firebase

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.snapshots
import com.google.firebase.database.ktx.values
import io.github.jeddchoi.data.repository.UserStatusRepository
import io.github.jeddchoi.model.UserStatus
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class FirebaseUserStatusRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase,
) : UserStatusRepository{
    val user = database.reference.child("").values<UserStatus>().map { it?.seatPos }
    override suspend fun createUserStatus(userId: String) {
        database.reference
    }

    fun observeUserState(userId: String) = database.reference.child("user_state").values<UserStatus>()
}