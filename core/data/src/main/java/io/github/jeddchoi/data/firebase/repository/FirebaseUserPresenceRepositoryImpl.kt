package io.github.jeddchoi.data.firebase.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.github.jeddchoi.data.repository.CurrentUserRepository
import io.github.jeddchoi.data.repository.UserPresenceRepository
import timber.log.Timber
import javax.inject.Inject


class FirebaseUserPresenceRepositoryImpl @Inject constructor(
    private val currentUserRepository: CurrentUserRepository,
) : UserPresenceRepository {
    private val database: FirebaseDatabase = Firebase.database

    private val connectionRef = database.getReference(".info/connected")
    private var listener: com.google.firebase.database.ValueEventListener? = null

    override fun observeUserPresence() {
        Timber.i("✅")
        val currentUserId = currentUserRepository.getUserId() ?: return
        val disconnectedOnOccupiedDatabaseRef =
            database.getReference("seatFinder/${currentUserId}/session/disconnectedOnOccupied")

        listener = object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Timber.i("✅")
                // If we're not currently connected, don't do anything.
                if (!(snapshot.value as Boolean)) {
                    return
                }

                // If we are currently connected, then use the 'onDisconnect()'
                // method to add a set which will only trigger once this
                // client has disconnected by closing the app,
                // losing internet, or any other means.
                disconnectedOnOccupiedDatabaseRef.let { ref ->
                    ref.onDisconnect().setValue(true).continueWithTask {
                        Timber.i("✅")

                        // The promise returned from .onDisconnect().set() will
                        // resolve as soon as the server acknowledges the onDisconnect()
                        // request, NOT once we've actually disconnected:
                        // https://firebase.google.com/docs/reference/js/firebase.database.OnDisconnect

                        // We can now safely set ourselves as 'online' knowing that the
                        // server will mark us as offline once we lose connection.
                        ref.removeValue()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException())
            }
        }

        connectionRef.addValueEventListener(listener!!)
    }

    override fun stopObserveUserPresence() {
        Timber.i("✅")
        val currentUserId = currentUserRepository.getUserId() ?: return
        val disconnectedOnOccupiedDatabaseRef =
            database.getReference("seatFinder/${currentUserId}/session/disconnectedOnOccupied")

        disconnectedOnOccupiedDatabaseRef.onDisconnect().cancel()
        disconnectedOnOccupiedDatabaseRef.removeValue()
        listener?.let {
            connectionRef.removeEventListener(it)
        }
    }
}