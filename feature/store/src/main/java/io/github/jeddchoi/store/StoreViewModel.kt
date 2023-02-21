package io.github.jeddchoi.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jeddchoi.ui.UiState
import kotlinx.coroutines.flow.*

class StoreViewModel : ViewModel() {

    private val _uiState: Flow<SeatsUiStateData> = flow {
        emit(SeatsUiStateData("Seats"))
    }


    val uiState: StateFlow<UiState<SeatsUiStateData>>
        get() = _uiState.map<SeatsUiStateData, UiState<SeatsUiStateData>> {
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
    val data : String
)