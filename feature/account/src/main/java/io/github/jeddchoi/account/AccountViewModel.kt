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
    }


    val uiState: StateFlow<UiState<AccountUiStateData>> =
        _uiState.map<AccountUiStateData, UiState<AccountUiStateData>> {
            UiState.Success(it)
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