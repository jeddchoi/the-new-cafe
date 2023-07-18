package io.github.jeddchoi.data.firebase.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.github.jeddchoi.data.firebase.model.FirebaseUserStateChange
import io.github.jeddchoi.data.firebase.model.toUserStateChange
import io.github.jeddchoi.data.repository.CurrentUserRepository
import io.github.jeddchoi.data.repository.UserSessionHistoryDetailRepository
import io.github.jeddchoi.model.UserStateChange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class FirebaseUserSessionHistoryDetailRepositoryImpl @Inject constructor(
    val currentUserRepository: CurrentUserRepository,
    val database: FirebaseDatabase,
) : UserSessionHistoryDetailRepository {
    override fun getStateChanges(sessionId: String): Flow<List<UserStateChange>> =
        currentUserRepository.currentUserId.transform { currentUserId ->
            if (currentUserId != null) {
                val ref =
                    database.getReference("seatFinder/stateChanges/${currentUserId}/$sessionId")
                emitAll(
                    callbackFlow<List<UserStateChange>> {
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
                )


            } else { // not signed in
                emit(emptyList())
            }
        }.flowOn(Dispatchers.IO)

}