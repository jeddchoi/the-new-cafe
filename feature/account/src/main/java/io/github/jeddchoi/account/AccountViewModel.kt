package io.github.jeddchoi.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jeddchoi.ui.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

class AccountViewModel : ViewModel() {

    private val _uiState = flow {
        delay(1000)
        emit(AccountUiStateData("Account"))
        delay(1000)
        emit(AccountUiStateData(" "))
        delay(1000)
        throw RuntimeException("Wow")
    }


    val uiState: StateFlow<UiState<AccountUiStateData>> =
        _uiState.map {
            if (it.data.isBlank()) UiState.Empty
            else UiState.Success(it)
        }.catch {
            emit(UiState.Error(it))
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            UiState.Loading()
        )
}


data class AccountUiStateData(
    val data: String
)