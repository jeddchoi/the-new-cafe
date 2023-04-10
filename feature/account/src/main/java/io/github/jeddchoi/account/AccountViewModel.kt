package io.github.jeddchoi.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jeddchoi.ui.feature.UiState
import kotlinx.coroutines.flow.*

internal class AccountViewModel(

) : ViewModel() {

    private val _uiState = flow {
        emit(AccountUiStateData("Account"))
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
    fun signOut() {

    }
}


internal data class AccountUiStateData(
    val data: String
)