package io.github.jeddchoi.mystatus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jeddchoi.ui.model.FeedbackState
import io.github.jeddchoi.ui.model.Message
import io.github.jeddchoi.ui.model.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*


class MyStatusViewModel : ViewModel() {

    private val _uiState: Flow<MyStatusUiStateData> = flow {
        delay(1000)
        emit(MyStatusUiStateData("MyStatus"))
    }


    val uiState: StateFlow<UiState<MyStatusUiStateData>> =
        _uiState.map<MyStatusUiStateData, UiState<MyStatusUiStateData>> {
            UiState.Success(it)
        }.catch {
            emit(UiState.Error(it))
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            UiState.InitialLoading
        )
}


data class MyStatusUiStateData(
    val data: String,
    override val isBusy: Boolean = false,
    override val canContinue: Boolean = true,
    override val messages: List<Message> = emptyList()
) : FeedbackState {
    override fun copy(
        isBusy: Boolean,
        canContinue: Boolean,
        messages: List<Message>
    ): MyStatusUiStateData = MyStatusUiStateData(data, isBusy, canContinue, messages)
}