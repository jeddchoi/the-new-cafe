package io.github.jeddchoi.order.store

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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
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
            combine(temp.onEach { Timber.v("💥 $it") }) { combined ->
                combined.map { (section, seats) ->
                    SectionWithSeats(section, seats)
                }
            }
        }

    private val oneShotActionState = MutableStateFlow(OneShotActionState())


    val uiState = storeDetail.onEach { Timber.v("💥 $it") }.flatMapLatest { store ->
        if (store == null) {
            flowOf(StoreUiState.NotFound)
        } else {
            combine(
                sectionWithSeats.onEach { Timber.v("💥 $it") },
                oneShotActionState.onEach { Timber.v("💥 $it") },
                userSessionRepository.userStateAndUsedSeatPosition.onEach { Timber.v("💥 $it") },
            ) { sections, oneShotActionState, userStateAndUsedSeatPosition ->
                StoreUiState.Success(
                    store = store,
                    sectionWithSeats = sections.sortedBy { it.section.major },
                    isLoading = oneShotActionState.isLoading,
                    userMessage = oneShotActionState.userMessage,
                    selectedSeat = oneShotActionState.selectedSeat,
                    userStateAndUsedSeatPosition = userStateAndUsedSeatPosition,
                )
            }
        }
    }
        .catch {
            Timber.e(it)
            emit(StoreUiState.Error(it))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StoreUiState.Loading)


    fun selectSeat(sectionId: String, seatId: String) {
        Timber.v("✅ $sectionId $seatId")
        val newSelectedSeat = SelectedSeat(sectionId, seatId)
        oneShotActionState.update {
            if (it.selectedSeat?.equals(newSelectedSeat) == true) {
                it.copy(selectedSeat = null)
            } else {
                it.copy(selectedSeat = newSelectedSeat)
            }
        }
    }

    init {
        Timber.i("StoreId = ${storeArgs.storeId} SectionId = ${storeArgs.sectionId} SeatId = ${storeArgs.seatId}")
        if (storeArgs.sectionId != null && storeArgs.seatId != null) {
            selectSeat(storeArgs.sectionId, storeArgs.seatId)
        }
    }
    fun reserve() {
        Timber.v("✅")
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
        Timber.v("✅")
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
        Timber.v("✅")
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

    fun setUserMessage(message: Message?) {
        Timber.v("✅")
        oneShotActionState.update {
            it.copy(userMessage = message)
        }
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
    data object Loading : StoreUiState()
    data object NotFound : StoreUiState()
    data class Success(
        val store: Store,
        val sectionWithSeats: List<SectionWithSeats>,
        val selectedSeat: SelectedSeat? = null,
        val isLoading: Boolean = false,
        val userMessage: Message? = null,
        val userStateAndUsedSeatPosition: UserStateAndUsedSeatPosition? = null,
    ) : StoreUiState() {

        /**
         * Selected same used seat?
         * if null, it means not selected or not signed in or not used
         */
        val selectedUsedSeat = with(userStateAndUsedSeatPosition) {
            if (this is UserStateAndUsedSeatPosition.UsingSeat && selectedSeat != null) {
                seatPosition.storeId == store.id &&
                        seatPosition.sectionId == selectedSeat.sectionId &&
                        seatPosition.seatId == selectedSeat.seatId
            } else {
                null
            }
        }
    }

    data class Error(val exception: Throwable) : StoreUiState()
}