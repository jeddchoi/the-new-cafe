package io.github.jeddchoi.data.firebase.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.dataObjects
import io.github.jeddchoi.data.firebase.model.FirebaseStore
import io.github.jeddchoi.data.firebase.model.toStore
import io.github.jeddchoi.data.repository.StoreRepository
import io.github.jeddchoi.model.Store
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FirebaseStoreRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : StoreRepository {
    override val stores: Flow<List<Store>> = firestore.collection("stores").dataObjects<FirebaseStore>().map { firebaseStores ->
        firebaseStores.map {
            Log.i("FirebaseStoreRepositoryImpl", it.toString())
            it.toStore()
        }
    }
}