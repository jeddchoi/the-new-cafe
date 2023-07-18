package io.github.jeddchoi.data.firebase.repository

import android.util.Log
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FirebaseStoreRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : StoreRepository {
    override val stores =
        firestore.collection("stores").dataObjects<FirebaseStore>().map { firebaseStores ->
            firebaseStores.map {
                it.toStore()
            }
        }.flowOn(Dispatchers.IO)

    override fun getStoreDetail(storeId: String) =
        firestore.document("stores/${storeId}").dataObjects<FirebaseStore>().map {
            it?.toStore()
        }.flowOn(Dispatchers.IO)

    override fun sections(storeId: String) =
        firestore.collection("stores/${storeId}/sections").dataObjects<FirebaseSection>()
            .map { firebaseSections ->
                firebaseSections.map {
                    it.toSection()
                }
            }.flowOn(Dispatchers.IO)

    override fun getSectionDetail(storeId: String, sectionId: String) =
        firestore.document("stores/${storeId}/sections/${sectionId}").dataObjects<FirebaseSection>()
            .map {
                it?.toSection()
            }.flowOn(Dispatchers.IO)

    override fun seats(storeId: String, sectionId: String): Flow<List<Seat>> {
        Log.i("FirebaseStoreRepositoryImpl#seats", "seats $storeId $sectionId")
        return firestore.collection("stores/${storeId}/sections/${sectionId}/seats")
            .dataObjects<FirebaseSeat>()
            .map { firebaseSeats ->
                Log.i("FirebaseStoreRepositoryImpl#seats", firebaseSeats.joinToString("\n"))
                firebaseSeats.map {
                    it.toSeat()
                }
            }.flowOn(Dispatchers.IO)
    }

    override fun getSeatDetail(storeId: String, sectionId: String, seatId: String) =
        firestore.document("stores/${storeId}/sections/${sectionId}/seats/${seatId}")
            .dataObjects<FirebaseSeat>()
            .map {
                Log.i("FirebaseStoreRepositoryImpl#getSeatDetail", it.toString())
                it?.toSeat()
            }.flowOn(Dispatchers.IO)

}