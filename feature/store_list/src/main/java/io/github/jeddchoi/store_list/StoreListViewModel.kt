package io.github.jeddchoi.store_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jeddchoi.ui.model.FeedbackState
import io.github.jeddchoi.ui.model.Message
import io.github.jeddchoi.ui.model.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

class StoreListViewModel : ViewModel() {

    private val _uiState: Flow<StoreListUiStateData> = flow {
        delay(1000)
        emit(StoreListUiStateData("Stores"))
    }


    val uiState: StateFlow<UiState<StoreListUiStateData>> =
        _uiState.map<StoreListUiStateData, UiState<StoreListUiStateData>> {
            UiState.Success(it)
        }.catch {
            emit(UiState.Error(it))
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            UiState.InitialLoading
        )
}


data class StoreListUiStateData(
    val data: String,
    override val isBusy: Boolean = false,
    override val canContinue: Boolean = true,
    override val messages: List<Message> = emptyList()
) : FeedbackState {
    override fun copy(
        isBusy: Boolean,
        canContinue: Boolean,
        messages: List<Message>
    ): FeedbackState = StoreListUiStateData(data, isBusy, canContinue, messages)
}