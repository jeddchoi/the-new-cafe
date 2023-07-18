package io.github.jeddchoi.mypage

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jeddchoi.common.Action
import io.github.jeddchoi.common.Message
import io.github.jeddchoi.common.OneShotFeedbackUiState
import io.github.jeddchoi.common.toErrorMessage
import io.github.jeddchoi.data.repository.UserSessionRepository
import io.github.jeddchoi.data.service.seatfinder.SeatFinderService
import io.github.jeddchoi.data.service.seatfinder.SeatFinderUserRequestType
import io.github.jeddchoi.data.service.seatfinder.toMessage
import io.github.jeddchoi.data.util.TickHandler
import io.github.jeddchoi.mypage.session.DisplayedUserSession
import io.github.jeddchoi.mypage.session.toDisplayedUserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
internal class MyPageViewModel @Inject constructor(
    private val sessionRepository: UserSessionRepository,
    private val seatFinderService: SeatFinderService,
    private val tickHandler: TickHandler,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {


    private val oneShotActionState = MutableStateFlow(OneShotActionState())
    val uiState: StateFlow<MyPageUiState> =
        tickHandler.tickFlow.combine(sessionRepository.userSession) { current, userSession ->
            if (userSession != null) {
                MyPageUiState.Success(
                    displayedUserSession = userSession.toDisplayedUserSession(
                        current
                    )
                )
            } else {
                MyPageUiState.NotAuthenticated
            }
        }.catch {
            emit(MyPageUiState.Error(it))
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            MyPageUiState.InitialLoading,
        )


    fun sendRequest(
        seatFinderUserRequestType: SeatFinderUserRequestType,
        duration: Int? = null,
        endTime: Long? = null,
    ) {
        launchOneShotJob(
            job = {
                val result = seatFinderService.requestInSession(
                    seatFinderRequestType = seatFinderUserRequestType,
                    endTime = endTime,
                    durationInSeconds = duration,
                )

                oneShotActionState.update {
                    it.copy(
                        userMessage = result.resultCode.toMessage()
                    )
                }
            },
            onError = {e, job ->
                oneShotActionState.update {
                    it.copy(
                        userMessage = e.toErrorMessage(Action.Retry { launchOneShotJob(job) })
                    )
                }
            }
        )
    }

    private fun launchOneShotJob(
        job: suspend () -> Unit,
        onError: (Throwable, suspend () -> Unit) -> Unit = { _, _ -> }
    ) {
        oneShotActionState.update {
            it.copy(isLoading = true, userMessage = null)
        }
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    job()
                }
            } catch (e: Throwable) {
                Log.e("launchOneShotJob", e.stackTraceToString())
                onError(e, job)
            } finally {
                oneShotActionState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }
}

data class OneShotActionState(
    override val isLoading: Boolean = false,
    override val userMessage: Message? = null,
) : OneShotFeedbackUiState()

internal sealed class MyPageUiState {
    object InitialLoading : MyPageUiState()

    object NotAuthenticated : MyPageUiState()

    data class Success(
        val displayedUserSession: DisplayedUserSession = DisplayedUserSession.None,
        val isLoading: Boolean = false,
        val userMessage: Message? = null
    ) : MyPageUiState()

    data class Error(val exception: Throwable) : MyPageUiState()
}