package io.github.jeddchoi.data.firebase.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.dataObjects
import io.github.jeddchoi.data.firebase.model.FirebaseSeatPosition
import io.github.jeddchoi.data.repository.SeatRepository
import javax.inject.Inject

class FirebaseSeatRepositoryImpl @Inject constructor(
    val firestore: FirebaseFirestore
) : SeatRepository {

    val seat = firestore.document("").dataObjects<FirebaseSeatPosition>()
}