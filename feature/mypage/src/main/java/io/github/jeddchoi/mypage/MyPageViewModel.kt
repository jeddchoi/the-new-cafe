package io.github.jeddchoi.mypage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jeddchoi.data.repository.UserSessionRepository
import io.github.jeddchoi.data.service.seatfinder.SeatFinderService
import io.github.jeddchoi.data.service.seatfinder.SeatFinderUserRequestType
import io.github.jeddchoi.model.SeatFinderRequestType
import io.github.jeddchoi.ui.model.Action
import io.github.jeddchoi.ui.model.FeedbackState
import io.github.jeddchoi.ui.model.Message
import io.github.jeddchoi.ui.model.Severity
import io.github.jeddchoi.ui.model.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.isDistantFuture
import javax.inject.Inject

@HiltViewModel
internal class MyPageViewModel @Inject constructor(
    private val sessionRepository: UserSessionRepository,
    private val seatFinderService: SeatFinderService
) : ViewModel() {


    private val _uiState = MutableStateFlow(MyPageUiStateData())
    val uiState: StateFlow<UiState<MyPageUiStateData>> =
        _uiState.map<MyPageUiStateData, UiState<MyPageUiStateData>> {
            UiState.Success(it)
        }.catch {
            emit(UiState.Error(it))
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            UiState.InitialLoading
        )

    private fun launchOneShotJob(
        job: suspend () -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (!_uiState.value.canContinue) {
                    return@launch
                }
                _uiState.value = _uiState.value.copy(isBusy = true)
                job()
            } catch (e: Exception) {
                Log.e("MyPageViewModel", e.stackTraceToString())
                Log.e("MyPageViewModel", e.message ?: "")
                _uiState.value = _uiState.value.copy(
                    canContinue = false,
                    messages = _uiState.value.messages.plus(
                        Message(
                            titleId = R.string.error,
                            content = e.message ?: e.stackTraceToString(),
                            severity = Severity.ERROR,
                            action = listOf(Action(R.string.retry) {
                                launchOneShotJob(job)
                            }),
                        )
                    )
                )
            } finally {
                _uiState.value = _uiState.value.copy(isBusy = false)
            }
        }
    }


    private val controlSubActions = listOf(
        SeatFinderUserRequestType.Occupy,
        SeatFinderUserRequestType.DoBusiness,
        SeatFinderUserRequestType.ResumeUsing,
        SeatFinderUserRequestType.LeaveAway,
        SeatFinderUserRequestType.ChangeReservationEndTime,
        SeatFinderUserRequestType.ChangeOccupyEndTime,
        SeatFinderUserRequestType.ChangeBusinessEndTime,
        SeatFinderUserRequestType.ChangeAwayEndTime,
    )

    private fun onClick(
        seatFinderRequestType: SeatFinderUserRequestType,
        endTime: Instant = Instant.DISTANT_FUTURE
    ) {
        launchOneShotJob {

            val result = seatFinderService.requestInSession(
                seatFinderRequestType,
                endTime = if (endTime.isDistantFuture) null else endTime.toEpochMilliseconds()
            )
            _uiState.value = _uiState.value.copy(
                messages = _uiState.value.messages.plus(
                    Message(
                        titleId = R.string.info,
                        contentId = result.resultCode.description,
                        severity = Severity.ERROR,
                    ),
                )
            )
        }
    }

    init {
        viewModelScope.launch {
            sessionRepository.userSession.collectLatest { userSession ->
                val controlButtons = mutableListOf<SeatFinderButtonState>()
                controlButtons.add(SeatFinderButtonState.PrimaryButton(
                    isEnabled = userSession != null,
                    name = SeatFinderRequestType.Quit.name,
                    onClick = { onClick(SeatFinderUserRequestType.Quit) }
                ))

                controlSubActions.forEach {
                    controlButtons.add(SeatFinderButtonState.SecondaryButton(
                        isEnabled = userSession != null && it.availableState.contains(
                            userSession.currentState
                        ),
                        name = it.name,
                        onClick = { endTime -> onClick(it, endTime) }
                    ))
                }
                _uiState.value = _uiState.value.copy(controlButtons = controlButtons)
            }
        }
    }

}

internal data class MyPageUiStateData(
    val controlButtons: List<SeatFinderButtonState> = listOf(),
    override val isBusy: Boolean = false,
    override val canContinue: Boolean = true,
    override val messages: List<Message> = emptyList()
) : FeedbackState {
    override fun copy(
        isBusy: Boolean,
        canContinue: Boolean,
        messages: List<Message>
    ): MyPageUiStateData = MyPageUiStateData(
        controlButtons,
        isBusy,
        canContinue,
        messages
    )
}

sealed interface SeatFinderButtonState {
    val name: String
    val isEnabled: Boolean
    val onClick: (Instant) -> Unit

    data class PrimaryButton(
        override val isEnabled: Boolean,
        override val name: String,
        override val onClick: (Instant) -> Unit
    ) : SeatFinderButtonState

    data class SecondaryButton(
        override val isEnabled: Boolean,
        override val name: String,
        override val onClick: (Instant) -> Unit
    ) : SeatFinderButtonState
}