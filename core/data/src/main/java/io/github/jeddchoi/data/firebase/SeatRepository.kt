package io.github.jeddchoi.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.dataObjects
import io.github.jeddchoi.data.firebase.model.FirebaseSeatPosition
import javax.inject.Inject

class SeatRepository @Inject constructor(
    val firestore: FirebaseFirestore
) {

    val seat = firestore.document("").dataObjects<FirebaseSeatPosition>()
}