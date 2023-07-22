package io.github.jeddchoi.data.firebase.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.github.jeddchoi.data.firebase.model.FirebaseUserStateChange
import io.github.jeddchoi.data.firebase.model.toUserStateChange
import io.github.jeddchoi.data.repository.CurrentUserRepository
import io.github.jeddchoi.data.repository.UserSessionHistoryDetailRepository
import io.github.jeddchoi.model.UserStateChange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class FirebaseUserSessionHistoryDetailRepositoryImpl @Inject constructor(
    val currentUserRepository: CurrentUserRepository,
) : UserSessionHistoryDetailRepository {
    private val database: FirebaseDatabase = Firebase.database
    override fun getStateChanges(sessionId: String): Flow<List<UserStateChange>> {
        Timber.v("✅ $sessionId")
        return currentUserRepository.currentUserId.flatMapLatest { currentUserId ->
            if (currentUserId != null) {
                val ref =
                    database.getReference("seatFinder/${currentUserId}/stateChanges/$sessionId")
                callbackFlow {
                    val valueEventListener = object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            trySend(
                                dataSnapshot.children.mapNotNull {
                                    it.getValue(
                                        FirebaseUserStateChange::class.java
                                    )?.toUserStateChange()
                                }.toList()
                            ).isSuccess
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            close(databaseError.toException())
                        }
                    }

                    ref.addValueEventListener(valueEventListener)

                    awaitClose {
                        ref.removeEventListener(valueEventListener)
                    }
                }
            } else { // not signed in
                flowOf(emptyList())
            }
        }.flowOn(Dispatchers.IO).onEach { Timber.v("💥 $it") }
    }

}