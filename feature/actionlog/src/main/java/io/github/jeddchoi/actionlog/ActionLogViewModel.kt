package io.github.jeddchoi.actionlog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jeddchoi.ui.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

class ActionLogViewModel : ViewModel() {

    private val _uiState: Flow<ActionLogUiStateData> = flow {
        delay(1000)
        emit(ActionLogUiStateData("ActionLog"))
    }


    val uiState: StateFlow<UiState<ActionLogUiStateData>> =
        _uiState.map<ActionLogUiStateData, UiState<ActionLogUiStateData>> {
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
    val data: String
)