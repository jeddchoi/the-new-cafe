package io.github.jeddchoi.data.firebase.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.dataObjects
import io.github.jeddchoi.data.firebase.model.FirebaseSeat
import io.github.jeddchoi.data.firebase.model.FirebaseSection
import io.github.jeddchoi.data.firebase.model.FirebaseStore
import io.github.jeddchoi.data.firebase.model.toSeat
import io.github.jeddchoi.data.firebase.model.toSection
import io.github.jeddchoi.data.firebase.model.toStore
import io.github.jeddchoi.data.repository.StoreRepository
import io.github.jeddchoi.model.Seat
import io.github.jeddchoi.model.Section
import io.github.jeddchoi.model.Store
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class FirebaseStoreRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : StoreRepository {
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
}