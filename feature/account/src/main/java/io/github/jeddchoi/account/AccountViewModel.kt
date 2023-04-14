package io.github.jeddchoi.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jeddchoi.ui.model.FeedbackState
import io.github.jeddchoi.ui.model.Message
import io.github.jeddchoi.ui.model.UiState
import kotlinx.coroutines.flow.*

internal class AccountViewModel(

) : ViewModel() {

    private val _uiState = flow {
        emit(AccountUiStateData("Account"))
    }

    val uiState: StateFlow<UiState<AccountUiStateData>> =
        _uiState.map {
            if (it.data.isBlank()) UiState.EmptyResult
            else UiState.Success(it)
        }.catch {
            emit(UiState.Error(it))
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            UiState.InitialLoading
        )

    fun signOut() {

    }
}


internal data class AccountUiStateData(
    val data: String,
    override val isBusy: Boolean = false,
    override val canContinue: Boolean = true,
    override val messages: List<Message> = emptyList()
) : FeedbackState {
    override fun copy(
        isBusy: Boolean,
        canContinue: Boolean,
        messages: List<Message>
    ): FeedbackState = AccountUiStateData(data, isBusy, canContinue, messages)
}