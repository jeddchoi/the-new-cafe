package io.github.jeddchoi.order.store

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jeddchoi.data.repository.StoreRepository
import io.github.jeddchoi.model.Store
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
internal class StoreViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val storeArgs = StoreArgs(savedStateHandle)

    val uiState = storeRepository.getStoreDetail(storeArgs.storeId)
        .map {
            if (it == null) StoreUiState.NotFound
            else StoreUiState.Success(it)
        }
        .catch { StoreUiState.Error(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StoreUiState.Loading)

}
sealed class StoreUiState {
    object Loading : StoreUiState()
    object NotFound : StoreUiState()
    data class Success(val store: Store) : StoreUiState()
    data class Error(val exception: Throwable) : StoreUiState()
}