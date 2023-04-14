package io.github.jeddchoi.actionlog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jeddchoi.ui.model.FeedbackState
import io.github.jeddchoi.ui.model.Message
import io.github.jeddchoi.ui.model.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

internal class ActionLogViewModel : ViewModel() {

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
            UiState.InitialLoading
        )
}


internal data class ActionLogUiStateData(
    val data: String,
    override val isBusy: Boolean = false,
    override val canContinue: Boolean = true,
    override val messages: List<Message> = emptyList()
) : FeedbackState {
    override fun copy(
        isBusy: Boolean,
        canContinue: Boolean,
        messages: List<Message>
    ): ActionLogUiStateData = ActionLogUiStateData(data, isBusy, canContinue, messages)
}