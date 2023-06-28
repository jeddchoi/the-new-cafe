package io.github.jeddchoi.mystatus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jeddchoi.data.repository.UserSessionRepository
import io.github.jeddchoi.ui.model.FeedbackState
import io.github.jeddchoi.ui.model.Message
import io.github.jeddchoi.ui.model.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
internal class MyStatusViewModel @Inject constructor(
    private val userSessionRepository: UserSessionRepository
): ViewModel() {

    private val _uiState: Flow<MyStatusUiStateData> = userSessionRepository.userSession.map {
        MyStatusUiStateData(data = it.toString())
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


internal data class MyStatusUiStateData(
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