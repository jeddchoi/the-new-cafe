package io.github.jeddchoi.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jeddchoi.ui.UiState
import io.github.jeddchoi.ui.asUiState
import kotlinx.coroutines.flow.*


//@HiltViewModel
class MyPageViewModel : ViewModel() {

    private val _uiState: Flow<MyPageUiState> = flow {
        emit(null)
        emit("Hello")
    }.asUiState()
    val uiState: StateFlow<MyPageUiState>
        get() = _uiState.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            UiState.Loading()
        )
}


typealias MyPageUiState = UiState<String?>