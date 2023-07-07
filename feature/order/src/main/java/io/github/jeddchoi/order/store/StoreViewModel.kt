package io.github.jeddchoi.order.store

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jeddchoi.data.repository.StoreRepository
import io.github.jeddchoi.model.Store
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
internal class StoreViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val storeArgs = StoreArgs(savedStateHandle)

    private val storeDetail = storeRepository.getStoreDetail(storeArgs.storeId)
    private val sectionWithSeats =
        storeRepository.sections(storeArgs.storeId).flatMapLatest { sections ->
            val temp = sections.map { section ->
                storeRepository.seats(storeArgs.storeId, section.id)
                    .map { seats -> section to seats }
            }
            combine(temp) { combined ->
                combined.map { (section, seats) ->
                    SectionWithSeats(section, seats)
                }
            }
        }

    val uiState = combine(
        storeDetail,
        sectionWithSeats
    ) { store, sections ->
        if (store == null) {
            StoreUiState.NotFound
        } else {
            StoreUiState.Success(store, sections)
        }
    }
        .catch { StoreUiState.Error(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StoreUiState.Loading)
}

sealed class StoreUiState {
    object Loading : StoreUiState()
    object NotFound : StoreUiState()
    data class Success(val store: Store, val sectionWithSeats: List<SectionWithSeats>) : StoreUiState()
    data class Error(val exception: Throwable) : StoreUiState()
}