package io.github.jeddchoi.actionlog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jeddchoi.ui.UiState
import kotlinx.coroutines.flow.*

class ActionLogViewModel : ViewModel() {

    private val _uiState: Flow<ActionLogUiStateData> = flow {
        emit(ActionLogUiStateData("first"))
    }


    val uiState: StateFlow<UiState<ActionLogUiStateData>>
        get() = _uiState.map<ActionLogUiStateData, UiState<ActionLogUiStateData>> {
            UiState.Success(it)
        }.catch {
            emit(UiState.Error(it))
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            UiState.Loading()
        )
}


data class ActionLogUiStateData(
    val data : String
)