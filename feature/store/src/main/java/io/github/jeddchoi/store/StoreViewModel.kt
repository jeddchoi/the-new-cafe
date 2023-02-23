package io.github.jeddchoi.store

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jeddchoi.ui.feature.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

class StoreViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val storeArgs: StoreArgs = StoreArgs(savedStateHandle)

    private val _uiState: Flow<SeatsUiStateData> = flow {
        delay(1000)
        emit(SeatsUiStateData("Seats"))
    }


    val uiState: StateFlow<UiState<SeatsUiStateData>> =
        _uiState.map<SeatsUiStateData, UiState<SeatsUiStateData>> {
            UiState.Success(it)
        }.catch {
            emit(UiState.Error(it))
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            UiState.Loading()
        )
}


data class SeatsUiStateData(
    val data: String
)