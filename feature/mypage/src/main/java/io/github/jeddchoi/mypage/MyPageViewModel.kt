package io.github.jeddchoi.mypage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jeddchoi.ui.UiState
import kotlinx.coroutines.flow.*


class MyPageViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val myPageArgs: MyPageArgs = MyPageArgs(savedStateHandle)

    private val _uiState: Flow<MyPageUiStateData> = flow {
        emit(MyPageUiStateData(myPageArgs.tabId))
    }


    val uiState: StateFlow<UiState<MyPageUiStateData>>
        get() = _uiState.map<MyPageUiStateData, UiState<MyPageUiStateData>> {
            UiState.Success(it)
        }.catch {
            emit(UiState.Error(it))
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            UiState.Loading()
        )
}

data class MyPageUiStateData(
    val tabId: String
)

