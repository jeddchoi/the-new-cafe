package io.github.jeddchoi.historydetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jeddchoi.common.Message
import io.github.jeddchoi.data.repository.UserSessionHistoryDetailRepository
import io.github.jeddchoi.model.UserStateChange
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HistoryDetailViewModel @Inject constructor(
    val userSessionHistoryDetailRepository: UserSessionHistoryDetailRepository,
    val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val historyDetailArgs = HistoryDetailArgs(savedStateHandle)
    val uiState = userSessionHistoryDetailRepository.getStateChanges(historyDetailArgs.sessionId)
        .map {
            HistoryDetailUiState.Success(
                stateChanges = it
            )
        }
        .catch { HistoryDetailUiState.Error(it) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            HistoryDetailUiState.Loading
        )

}

sealed class HistoryDetailUiState {
    object Loading : HistoryDetailUiState()
    object NotFound : HistoryDetailUiState()
    data class Success(
        val stateChanges: List<UserStateChange> = emptyList(),
        val isLoading: Boolean = false,
        val userMessage: Message? = null,
    ) : HistoryDetailUiState()

    data class Error(val exception: Throwable) : HistoryDetailUiState()
}