package io.github.jeddchoi.thenewcafe.ui.root

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jeddchoi.data.repository.AppFlagsRepository
import io.github.jeddchoi.data.repository.UserSessionRepository
import io.github.jeddchoi.data.service.seatfinder.SeatFinderService
import io.github.jeddchoi.model.SeatPosition
import io.github.jeddchoi.model.UserSession
import io.github.jeddchoi.model.UserStateType
import io.github.jeddchoi.order.store.SEAT_ID_ARG
import io.github.jeddchoi.order.store.SECTION_ID_ARG
import io.github.jeddchoi.order.store.STORE_ID_ARG
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    private val userSessionRepository: UserSessionRepository,
    private val appFlagsRepository: AppFlagsRepository,
    private val seatFinderService: SeatFinderService,
) : ViewModel() {

    // it should be sharedflow because it will be notified with same value
    // And replay should be 1 because of when launched by NFC Read
    private var _nfcReadSeatPosition =
        MutableSharedFlow<SeatPosition?>(replay = 1)


    fun readNfcSeatPosition(uri: Uri) {
        Timber.i("✅ $uri")
        val storeId = uri.getQueryParameter(STORE_ID_ARG)
        val sectionId = uri.getQueryParameter(SECTION_ID_ARG)
        val seatId = uri.getQueryParameter(SEAT_ID_ARG)

        if (storeId != null && sectionId != null && seatId != null) {
            viewModelScope.launch {
                _nfcReadSeatPosition.emit(SeatPosition(storeId, sectionId, seatId))
            }
        }
    }


    val startSessionEvent = userSessionRepository.userSession.map { it is UserSession.UsingSeat }
        .distinctUntilChanged()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5_000))


    val userSessionStateFlow = userSessionRepository.userSession.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        null
    )

    val redirectionToAuth = appFlagsRepository.getShowMainScreenOnStart.map {
        it.not() && userSessionStateFlow.value == null
    }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(5_000))


    val redirectionEventByNfcRead =
        _nfcReadSeatPosition.map { nfcReadSeatPosition ->
            var redirectTo: Redirection? = null
            userSessionStateFlow.value.let { userSession ->
                Timber.i("redirectionEventByNfcRead\nnfcReadSeatPosition : $nfcReadSeatPosition / userSession : $userSession")
                when (userSession) {
                    null -> { // not signed in
                        if (nfcReadSeatPosition != null) {
                            redirectTo = Redirection.StoreScreen(nfcReadSeatPosition)
                        }
                    }

                    UserSession.None -> {
                        if (nfcReadSeatPosition != null) {
                            redirectTo = Redirection.StoreScreen(nfcReadSeatPosition)
                        }
                    }

                    is UserSession.UsingSeat -> {
                        when (userSession.currentState) {
                            UserStateType.None -> throw IllegalStateException()
                            UserStateType.Reserved -> {
                                if (nfcReadSeatPosition != null) {
                                    redirectTo =
                                        if (userSession.seatPosition == nfcReadSeatPosition) {
                                            Redirection.SessionScreen
                                        } else {
                                            Redirection.StoreScreen(nfcReadSeatPosition)
                                        }
                                }

                            }

                            UserStateType.Occupied -> {
                                if (nfcReadSeatPosition != null) {
                                    redirectTo =
                                        Redirection.StoreScreen(nfcReadSeatPosition)
                                }
                            }

                            UserStateType.Away -> {
                                if (nfcReadSeatPosition != null) {
                                    redirectTo =
                                        if (userSession.seatPosition == nfcReadSeatPosition) {
                                            Redirection.SessionScreen
                                        } else {
                                            Redirection.StoreScreen(nfcReadSeatPosition)
                                        }
                                }
                            }

                            UserStateType.OnBusiness -> {
                                if (nfcReadSeatPosition != null) {
                                    redirectTo =
                                        if (userSession.seatPosition == nfcReadSeatPosition) {
                                            Redirection.SessionScreen
                                        } else {
                                            Redirection.StoreScreen(nfcReadSeatPosition)
                                        }
                                }

                            }
                        }
                    }
                }
            }
            redirectTo
        }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(5_000))


    val arriveOnSeatByNfcReadEvent =
        _nfcReadSeatPosition.map { nfcReadSeatPosition ->
            var arrivedOnSeatWithNfc: (() -> Unit)? = null
            userSessionStateFlow.value.let { userSession ->
                Timber.i("arriveOnSeatByNfcReadEvent\nnfcReadSeatPosition : $nfcReadSeatPosition / userSession : $userSession")
                when (userSession) {
                    null -> {} // not signed in
                    UserSession.None -> {}

                    is UserSession.UsingSeat -> {
                        val sameWithOccupiedSeat = nfcReadSeatPosition == userSession.seatPosition
                        when (userSession.currentState) {
                            UserStateType.None -> throw IllegalStateException()
                            UserStateType.Reserved -> {
                                if (sameWithOccupiedSeat) {
                                    arrivedOnSeatWithNfc =
                                        { viewModelScope.launch { seatFinderService.occupySeat() } }
                                }
                            }

                            UserStateType.Occupied -> {
                            }

                            UserStateType.Away -> {
                                if (sameWithOccupiedSeat) {
                                    arrivedOnSeatWithNfc =
                                        { viewModelScope.launch { seatFinderService.resumeUsing() } }
                                }
                            }

                            UserStateType.OnBusiness -> {
                                if (sameWithOccupiedSeat) {
                                    arrivedOnSeatWithNfc =
                                        { viewModelScope.launch { seatFinderService.occupySeat() } }
                                }
                            }
                        }
                    }
                }
            }
            arrivedOnSeatWithNfc
        }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(5_000))

}

sealed class Redirection {
    data class StoreScreen(val seatPosition: SeatPosition) : Redirection()
    data object SessionScreen : Redirection()
}