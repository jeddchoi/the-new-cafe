package io.github.jeddchoi.data.repository

import io.github.jeddchoi.model.Store
import kotlinx.coroutines.flow.Flow

interface StoreRepository {
    val stores: Flow<List<Store>>
}