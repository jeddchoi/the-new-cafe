package io.github.jeddchoi.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jeddchoi.ui.UiState
import kotlinx.coroutines.flow.*

class OrderViewModel : ViewModel() {

    private val _uiState: Flow<OrderUiStateData> = flow {
        emit(OrderUiStateData("Order"))
    }


    val uiState: StateFlow<UiState<OrderUiStateData>>
        get() = _uiState.map<OrderUiStateData, UiState<OrderUiStateData>> {
            UiState.Success(it)
        }.catch {
            emit(UiState.Error(it))
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            UiState.Loading()
        )
}


data class OrderUiStateData(
    val data : String
)