package io.github.jeddchoi.mypage

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
import io.github.jeddchoi.data.service.seatfinder.toMessage
import io.github.jeddchoi.model.DisplayedUserSession
import io.github.jeddchoi.model.SeatFinderUserRequestType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class MyPageViewModel @Inject constructor(
    private val sessionRepository: UserSessionRepository,
    private val seatFinderService: SeatFinderService,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {


    private val oneShotActionState = MutableStateFlow(OneShotActionState())
    val uiState: StateFlow<MyPageUiState> =
        sessionRepository.userSessionWithTimer.map {
            if (it != null) {
                MyPageUiState.Success(displayedUserSession = it)
            } else {
                MyPageUiState.NotAuthenticated
            }
        }.catch {
            emit(MyPageUiState.Error(it))
        }.distinctUntilChanged().onEach {
            Timber.v("💥 $it")
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
        Timber.v("✅ $seatFinderUserRequestType $duration $endTime")
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
            onError = { e, job ->
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
        Timber.v("✅")
        oneShotActionState.update {
            it.copy(isLoading = true, userMessage = null)
        }
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    job()
                }
            } catch (e: Throwable) {
                Timber.e(e)
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