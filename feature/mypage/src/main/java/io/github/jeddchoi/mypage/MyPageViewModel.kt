package io.github.jeddchoi.mypage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jeddchoi.data.service.seatfinder.SeatFinderService
import io.github.jeddchoi.ui.model.Action
import io.github.jeddchoi.ui.model.FeedbackState
import io.github.jeddchoi.ui.model.Message
import io.github.jeddchoi.ui.model.Severity
import io.github.jeddchoi.ui.model.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class MyPageViewModel @Inject constructor(
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

    fun quit() {
        launchOneShotJob {
            val result = seatFinderService.quit()
            Log.i("MyPage", result.toString())
        }
    }

    fun occupySeat(endTime: Long? = null, durationInSeconds: Int? = null) {
        launchOneShotJob {
            val result = seatFinderService.occupySeat(endTime, durationInSeconds)
            Log.i("MyPage", result.toString())
        }
    }

    fun doBusiness(endTime: Long? = null, durationInSeconds: Int? = null) {
        launchOneShotJob {
            val result = seatFinderService.doBusiness(endTime, durationInSeconds)
            Log.i("MyPage", result.toString())
        }
    }

    fun resumeUsing() {
        launchOneShotJob {
            val result = seatFinderService.resumeUsing()
            Log.i("MyPage", result.toString())
        }
    }

    fun leaveAway(endTime: Long? = null, durationInSeconds: Int? = null) {
        launchOneShotJob {
            val result = seatFinderService.leaveAway(endTime, durationInSeconds)
            Log.i("MyPage", result.toString())
        }
    }

    fun changeReservationTimeoutTime(endTime: Long? = null, durationInSeconds: Int? = null) {
        launchOneShotJob {
            val result = seatFinderService.changeReservationTimeoutTime(endTime, durationInSeconds)
            Log.i("MyPage", result.toString())
        }
    }

    fun changeOccupyTimeoutTime(endTime: Long? = null, durationInSeconds: Int? = null) {
        launchOneShotJob {
            val result = seatFinderService.changeOccupyTimeoutTime(endTime, durationInSeconds)
            Log.i("MyPage", result.toString())
        }
    }

    fun changeBusinessTimeoutTime(endTime: Long? = null, durationInSeconds: Int? = null) {
        launchOneShotJob {
            val result = seatFinderService.changeBusinessTimeoutTime(endTime, durationInSeconds)
            Log.i("MyPage", result.toString())
        }
    }

    fun changeAwayTimeoutTime(endTime: Long? = null, durationInSeconds: Int? = null) {
        launchOneShotJob {
            val result = seatFinderService.changeAwayTimeoutTime(endTime, durationInSeconds)
            Log.i("MyPage", result.toString())
        }

    }

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
}

internal data class MyPageUiStateData(
    val isQuitEnabled: Boolean = false,
    val isOccupySeatEnabled: Boolean = false,
    val isDoBusinessEnabled: Boolean = false,
    val isResumeUsingEnabled: Boolean = false,
    val isLeaveAwayEnabled: Boolean = false,
    val isChangeReservationTimeoutTimeEnabled: Boolean = false,
    val isChangeOccupyTimeoutTimeEnabled: Boolean = false,
    val isChangeBusinessTimeoutTimeEnabled: Boolean = false,
    val isChangeAwayTimeoutTimeEnabled: Boolean = false,
    override val isBusy: Boolean = false,
    override val canContinue: Boolean = true,
    override val messages: List<Message> = emptyList()
) : FeedbackState {
    override fun copy(
        isBusy: Boolean,
        canContinue: Boolean,
        messages: List<Message>
    ): MyPageUiStateData = MyPageUiStateData(
        isQuitEnabled,
        isOccupySeatEnabled,
        isDoBusinessEnabled,
        isResumeUsingEnabled,
        isLeaveAwayEnabled,
        isChangeReservationTimeoutTimeEnabled,
        isChangeOccupyTimeoutTimeEnabled,
        isChangeBusinessTimeoutTimeEnabled,
        isChangeAwayTimeoutTimeEnabled,
        isBusy,
        canContinue,
        messages
    )
}