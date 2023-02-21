package io.github.jeddchoi.stores

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jeddchoi.ui.UiState
import kotlinx.coroutines.flow.*

class StoresViewModel : ViewModel() {

    private val _uiState: Flow<StoresUiStateData> = flow {
        emit(StoresUiStateData("Stores"))
    }


    val uiState: StateFlow<UiState<StoresUiStateData>>
        get() = _uiState.map<StoresUiStateData, UiState<StoresUiStateData>> {
            UiState.Success(it)
        }.catch {
            emit(UiState.Error(it))
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            UiState.Loading()
        )
}


data class StoresUiStateData(
    val data : String
)