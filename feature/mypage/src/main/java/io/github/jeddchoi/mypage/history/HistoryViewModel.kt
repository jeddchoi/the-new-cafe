package io.github.jeddchoi.mypage.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jeddchoi.common.Message
import io.github.jeddchoi.common.OneShotFeedbackUiState
import io.github.jeddchoi.data.repository.UserSessionHistoryRepository
import io.github.jeddchoi.data.repository.UserSessionRepository
import io.github.jeddchoi.model.UserSessionHistory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class HistoryViewModel @Inject constructor(
    val userSessionHistoryRepository: UserSessionHistoryRepository,
    val userSessionRepository: UserSessionRepository,
) : ViewModel() {

    val histories = userSessionHistoryRepository.getHistories().cachedIn(viewModelScope).onEach { Timber.v("💥 $it") }
    val currentSession = userSessionRepository.userSession.onEach { Timber.v("💥 $it") }
    val uiState: StateFlow<HistoryUiState> = histories.map {
        HistoryUiState.Success(
            history = it
        )
    }.catch {
        HistoryUiState.Error(it)
    }.onEach {
        Timber.v("💥 $it")
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        HistoryUiState.InitialLoading
    )
}

internal data class OneShotActionState(
    override val isLoading: Boolean = false,
    override val userMessage: Message? = null,
) : OneShotFeedbackUiState()

internal sealed class HistoryUiState {
    object InitialLoading : HistoryUiState()

    object NotAuthenticated : HistoryUiState()

    data class Success(
        val history: PagingData<UserSessionHistory>,
        val isLoading: Boolean = false,
        val userMessage: Message? = null
    ) : HistoryUiState()

    data class Error(val exception: Throwable) : HistoryUiState()
}