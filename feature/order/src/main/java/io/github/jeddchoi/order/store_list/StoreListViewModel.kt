package io.github.jeddchoi.order.store_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jeddchoi.data.repository.StoreRepository
import io.github.jeddchoi.model.Store
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class StoreListViewModel @Inject constructor(
    private val storeRepository: StoreRepository
) : ViewModel() {
    val uiState = storeRepository.stores
        .map {
            if (it.isEmpty()) StoreListUiState.EmptyList
            else StoreListUiState.Success(it)
        }
        .catch { emit(StoreListUiState.Error(it)) }
        .onEach { Timber.v("💥 $it") }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StoreListUiState.Loading)
}


sealed class StoreListUiState {
    object Loading : StoreListUiState()
    object EmptyList : StoreListUiState()
    data class Success(val stores: List<Store>) : StoreListUiState()
    data class Error(val exception: Throwable) : StoreListUiState()
}