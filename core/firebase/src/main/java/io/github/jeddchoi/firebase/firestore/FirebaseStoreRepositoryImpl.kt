package io.github.jeddchoi.firebase.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.dataObjects
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.github.jeddchoi.data.repository.StoreRepository
import io.github.jeddchoi.model.BleSeat
import io.github.jeddchoi.model.Seat
import io.github.jeddchoi.model.SeatPosition
import io.github.jeddchoi.model.Section
import io.github.jeddchoi.model.Store
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class FirebaseStoreRepositoryImpl @Inject constructor() : StoreRepository {
    private val firestore: FirebaseFirestore = Firebase.firestore
    override val stores: Flow<List<Store>> =
        firestore.collection("stores").dataObjects<FirebaseStore>().map { firebaseStores ->
            firebaseStores.map { it.toStore() }
        }.flowOn(Dispatchers.IO).onEach { Timber.v("💥 $it") }

    override fun getStoreDetail(storeId: String): Flow<Store?> {
        Timber.v("✅ $storeId")
        return firestore.document("stores/${storeId}").dataObjects<FirebaseStore>().map {
            it?.toStore()
        }.flowOn(Dispatchers.IO).onEach { Timber.v("💥 $it") }
    }

    override fun sections(storeId: String): Flow<List<Section>> {
        Timber.v("✅ $storeId")
        return firestore.collection("stores/${storeId}/sections").dataObjects<FirebaseSection>()
            .map { firebaseSections ->
                firebaseSections.map {
                    it.toSection()
                }
            }.flowOn(Dispatchers.IO).onEach { Timber.v("💥 $it") }
    }

    override fun getSectionDetail(storeId: String, sectionId: String): Flow<Section?> {
        Timber.v("✅ $storeId $sectionId")
        return firestore.document("stores/${storeId}/sections/${sectionId}")
            .dataObjects<FirebaseSection>()
            .map {
                it?.toSection()
            }.flowOn(Dispatchers.IO).onEach { Timber.v("💥 $it") }
    }

    override fun seats(storeId: String, sectionId: String): Flow<List<Seat>> {
        Timber.v("✅ $storeId $sectionId")
        return firestore.collection("stores/${storeId}/sections/${sectionId}/seats")
            .dataObjects<FirebaseSeat>()
            .map { firebaseSeats ->
                Timber.v(firebaseSeats.joinToString("\n"))
                firebaseSeats.map {
                    it.toSeat()
                }
            }.flowOn(Dispatchers.IO).onEach { Timber.v("💥 $it") }
    }

    override fun getSeatDetail(storeId: String, sectionId: String, seatId: String): Flow<Seat?> {
        Timber.v("✅ $storeId $sectionId $seatId")
        return firestore.document("stores/${storeId}/sections/${sectionId}/seats/${seatId}")
            .dataObjects<FirebaseSeat>()
            .map { it?.toSeat() }.flowOn(Dispatchers.IO).onEach { Timber.v("💥 $it") }
    }

    override suspend fun getBleSeat(seatPosition: SeatPosition): BleSeat? {
        var document = firestore.document("stores/${seatPosition.storeId}")
        val uuid = document.get().await().get("uuid", String::class.java)
        document = document.collection("sections").document(seatPosition.sectionId)
        val major = document.get().await().get("major", String::class.java)
        document = document.collection("seats").document(seatPosition.seatId)
        val seat = document.get().await().toObject(FirebaseSeat::class.java)
        val minor = seat?.minor
        val name = seat?.name
        val macAddress = seat?.macAddress
        return if (uuid != null && major != null && minor != null && name != null && macAddress != null) {
            BleSeat(uuid, major, minor, name, macAddress)
        } else {
            null
        }
    }
}