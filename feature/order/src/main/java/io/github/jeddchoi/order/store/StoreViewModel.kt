package io.github.jeddchoi.order.store

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jeddchoi.common.Action
import io.github.jeddchoi.common.Message
import io.github.jeddchoi.common.OneShotFeedbackUiState
import io.github.jeddchoi.common.toErrorMessage
import io.github.jeddchoi.data.repository.StoreRepository
import io.github.jeddchoi.data.repository.UserSessionRepository
import io.github.jeddchoi.data.service.seatfinder.SeatFinderService
import io.github.jeddchoi.model.SeatPosition
import io.github.jeddchoi.model.Store
import io.github.jeddchoi.model.UserStateAndUsedSeatPosition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
internal class StoreViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
    private val seatFinderService: SeatFinderService,
    private val userSessionRepository: UserSessionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val storeArgs = StoreArgs(savedStateHandle)

    private val storeDetail = storeRepository.getStoreDetail(storeArgs.storeId)
    private val sectionWithSeats =
        storeRepository.sections(storeArgs.storeId).flatMapLatest { sections ->
            val temp = sections.map { section ->
                storeRepository.seats(storeArgs.storeId, section.id)
                    .map { seats -> section to seats }
            }
            combine(temp) { combined ->
                combined.map { (section, seats) ->
                    SectionWithSeats(section, seats)
                }
            }
        }

    private val oneShotActionState = MutableStateFlow(OneShotActionState())

    val uiState = combine(
        storeDetail.onEach { Log.d("uiState", "storeDetail : $it") },
        sectionWithSeats.onEach { Log.d("uiState", "sectionWithSeats : $it") },
        oneShotActionState.onEach { Log.d("uiState", "oneShotActionState : $it") },
        userSessionRepository.userStateAndUsedSeatPosition.onEach {
            Log.d(
                "uiState",
                "userStateAndUsedSeatPosition : $it"
            )
        },
    ) { store, sections, oneShotActionState, userStateAndUsedSeatPosition ->
        Log.d("uiState", "$store\n$sections\n$oneShotActionState\n$userStateAndUsedSeatPosition")
        if (store == null) {
            StoreUiState.NotFound
        } else {
            StoreUiState.Success(
                store = store,
                sectionWithSeats = sections,
                isLoading = oneShotActionState.isLoading,
                userMessage = oneShotActionState.userMessage,
                selectedSeat = oneShotActionState.selectedSeat,
                userStateAndUsedSeatPosition = userStateAndUsedSeatPosition,
            )
        }
    }
        .catch {
            Log.e("uiState", "Error : $it")
            emit(StoreUiState.Error(it))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StoreUiState.Loading)

    fun onSelect(sectionId: String, seatId: String) {
        val newSelectedSeat = SelectedSeat(sectionId, seatId)
        oneShotActionState.update {
            if (it.selectedSeat?.equals(newSelectedSeat) == true) {
                it.copy(selectedSeat = null)
            } else {
                it.copy(selectedSeat = newSelectedSeat)
            }
        }
    }

    fun reserve() {
        launchOneShotJob(
            job = {
                val selectedSeat = oneShotActionState.value.selectedSeat ?: return@launchOneShotJob
                seatFinderService.reserveSeat(
                    SeatPosition(
                        storeId = storeArgs.storeId,
                        sectionId = selectedSeat.sectionId,
                        seatId = selectedSeat.seatId,
                    )
                )
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

    fun quit() {
        launchOneShotJob(
            job = {
                seatFinderService.quit()
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

    fun quitAndReserve() {
        launchOneShotJob(
            job = {
                val selectedSeat = oneShotActionState.value.selectedSeat ?: return@launchOneShotJob
                seatFinderService.quit()
                seatFinderService.reserveSeat(
                    SeatPosition(
                        storeId = storeArgs.storeId,
                        sectionId = selectedSeat.sectionId,
                        seatId = selectedSeat.seatId,
                    )
                )
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


data class SelectedSeat(
    val sectionId: String,
    val seatId: String,
)

data class OneShotActionState(
    val selectedSeat: SelectedSeat? = null,
    override val isLoading: Boolean = false,
    override val userMessage: Message? = null,
) : OneShotFeedbackUiState()


internal sealed class StoreUiState {
    object Loading : StoreUiState()
    object NotFound : StoreUiState()
    data class Success(
        val store: Store,
        val sectionWithSeats: List<SectionWithSeats>,
        val selectedSeat: SelectedSeat? = null,
        val isLoading: Boolean = false,
        val userMessage: Message? = null,
        val userStateAndUsedSeatPosition: UserStateAndUsedSeatPosition = UserStateAndUsedSeatPosition(
            null,
            null
        ),
    ) : StoreUiState() {

        /**
         * Selected same used seat?
         * if null, it means not selected or not signed in or not used
         */
        val selectedUsedSeat = with(userStateAndUsedSeatPosition.seatPosition) {
            if (this == null || selectedSeat == null) {
                null
            } else {
                this.storeId == store.id && this.sectionId == selectedSeat.sectionId && this.seatId == selectedSeat.seatId
            }
        }
    }

    data class Error(val exception: Throwable) : StoreUiState()
}