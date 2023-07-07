package io.github.jeddchoi.mypage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jeddchoi.data.repository.UserSessionRepository
import io.github.jeddchoi.data.service.seatfinder.SeatFinderService
import io.github.jeddchoi.data.service.seatfinder.SeatFinderUserRequestType
import io.github.jeddchoi.data.util.TickHandler
import io.github.jeddchoi.mypage.session.DisplayedUserSession
import io.github.jeddchoi.mypage.session.toDisplayedUserSession
import io.github.jeddchoi.ui.model.Message
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Instant
import javax.inject.Inject

@HiltViewModel
internal class MyPageViewModel @Inject constructor(
    private val sessionRepository: UserSessionRepository,
    private val seatFinderService: SeatFinderService,
    private val tickHandler: TickHandler,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val myPageTabArg = MyPageArgs(savedStateHandle)

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
}


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

//internal data class MyPageUiStateData(
//    val controlButtons: List<SeatFinderButtonState> = listOf(),
//    override val isBusy: Boolean = false,
//    override val canContinue: Boolean = true,
//    override val messages: List<Message> = emptyList()
//) : FeedbackState {
//    override fun copy(
//        isBusy: Boolean,
//        canContinue: Boolean,
//        messages: List<Message>
//    ): MyPageUiStateData = MyPageUiStateData(
//        controlButtons,
//        isBusy,
//        canContinue,
//        messages
//    )
//}
//
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