package io.github.jeddchoi.store_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jeddchoi.data.firebase.model.FirebaseSeatPosition
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
internal class StoreListViewModel @Inject constructor(
    private val seatFinderService: SeatFinderService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(StoreListUiStateData(""))


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

    fun reserveSeat() {
        launchOneShotJob {
            val result = seatFinderService.reserveSeat(
                FirebaseSeatPosition(
                    storeId = "i9sAij5mVBijR85hgraE",
                    sectionId = "FMLYWLzKmiou1PTcrFR8",
                    seatId = "ZlblGsMYd7IlO1DEho4H",
                )
            )
            Log.i("StoreList", result.toString())
        }
    }

    private fun launchOneShotJob(
        job: suspend () -> Unit
    ) {
        _uiState.value = _uiState.value.copy(isBusy = true)
        viewModelScope.launch {
            try {
                job()
            } catch (e: Exception) {
                Log.e("StoreList", e.stackTraceToString())
                Log.e("StoreList", e.message ?: "")
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


internal data class StoreListUiStateData(
    val data: String,
    override val isBusy: Boolean = false,
    override val canContinue: Boolean = true,
    override val messages: List<Message> = emptyList()
) : FeedbackState {
    override fun copy(
        isBusy: Boolean,
        canContinue: Boolean,
        messages: List<Message>
    ): StoreListUiStateData = StoreListUiStateData(data, isBusy, canContinue, messages)
}