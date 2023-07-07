package io.github.jeddchoi.data.firebase.repository

import com.google.firebase.firestore.FirebaseFirestore
import io.github.jeddchoi.data.repository.SeatRepository
import javax.inject.Inject

class FirebaseSeatRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : SeatRepository {


}