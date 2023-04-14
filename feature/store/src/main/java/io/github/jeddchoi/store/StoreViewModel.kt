package io.github.jeddchoi.store

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jeddchoi.ui.model.FeedbackState
import io.github.jeddchoi.ui.model.Message
import io.github.jeddchoi.ui.model.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

internal class StoreViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val storeArgs: StoreArgs = StoreArgs(savedStateHandle)

    private val _uiState: Flow<SeatsUiStateData> = flow {
        delay(1000)
        emit(SeatsUiStateData("Seats"))
    }


    val uiState: StateFlow<UiState<SeatsUiStateData>> =
        _uiState.map<SeatsUiStateData, UiState<SeatsUiStateData>> {
            UiState.Success(it)
        }.catch {
            emit(UiState.Error(it))
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            UiState.InitialLoading
        )
}


internal data class SeatsUiStateData(
    val data: String,
    override val isBusy: Boolean = false,
    override val canContinue: Boolean = true,
    override val messages: List<Message> = emptyList()
) : FeedbackState {
    override fun copy(
        isBusy: Boolean,
        canContinue: Boolean,
        messages: List<Message>
    ): SeatsUiStateData = SeatsUiStateData(data, isBusy, canContinue, messages)
}