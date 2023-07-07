package io.github.jeddchoi.data.repository

import io.github.jeddchoi.model.Seat
import io.github.jeddchoi.model.Section
import io.github.jeddchoi.model.Store
import kotlinx.coroutines.flow.Flow

interface StoreRepository {
    val stores: Flow<List<Store>>
    fun getStoreDetail(storeId: String): Flow<Store?>

    fun sections(storeId: String) : Flow<List<Section>>
    fun getSectionDetail(storeId: String, sectionId: String) : Flow<Section?>

    fun seats(storeId: String, sectionId: String) : Flow<List<Seat>>
    fun getSeatDetail(storeId: String, sectionId: String, seatId: String) : Flow<Seat?>
}