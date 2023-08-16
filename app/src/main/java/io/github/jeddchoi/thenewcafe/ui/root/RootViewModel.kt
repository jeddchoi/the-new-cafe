package io.github.jeddchoi.thenewcafe.ui.root

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jeddchoi.data.repository.AppFlagsRepository
import io.github.jeddchoi.data.repository.UserSessionRepository
import io.github.jeddchoi.data.service.seatfinder.SeatFinderService
import io.github.jeddchoi.model.UserSession
import io.github.jeddchoi.model.UserStateType
import io.github.jeddchoi.order.store.SEAT_ID_ARG
import io.github.jeddchoi.order.store.SECTION_ID_ARG
import io.github.jeddchoi.order.store.STORE_ID_ARG
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    private val userSessionRepository: UserSessionRepository,
    private val appFlagsRepository: AppFlagsRepository,
    private val seatFinderService: SeatFinderService,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()


    fun initialize() {
        Timber.v("✅")
        _isLoading.update { false }
    }

    val redirectToAuth: StateFlow<Boolean> = appFlagsRepository.getShowMainScreenOnStart
        .map { it.not() }
        .onEach { Timber.v("💥 $it") }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    private val currentUserSession = userSessionRepository.userSession.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        null
    )
    val shouldRunService =
        currentUserSession.map {
            when (it) {
                null,
                UserSession.None -> false

                is UserSession.UsingSeat -> {
                    true
                }
            }
        }.distinctUntilChanged()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                false
            )

    private var _nfcReadUri =
        MutableSharedFlow<Uri?>(replay = 0)

    fun taggedNfc(uri: Uri) {
        Timber.i("✅")
        viewModelScope.launch {
            _nfcReadUri.emit(uri)
        }
    }

    fun handledNfcReadUri() {
        viewModelScope.launch {
            _nfcReadUri.emit(null)
        }
    }
    val navigateToStoreDetail = _nfcReadUri.onEach { Timber.v("💥 $it") }.map { readUri ->
        if (readUri == null) return@map null

        val needToNavigate = when (val cus = currentUserSession.value) {
            null,
            UserSession.None -> true

            is UserSession.UsingSeat -> {
                when (cus.currentState) {
                    UserStateType.None -> throw IllegalStateException()
                    UserStateType.Occupied -> true
                    UserStateType.Reserved,
                    UserStateType.Away,
                    UserStateType.OnBusiness -> {
                        val storeId = readUri.getQueryParameter(STORE_ID_ARG)
                        val sectionId = readUri.getQueryParameter(SECTION_ID_ARG)
                        val seatId = readUri.getQueryParameter(SEAT_ID_ARG)
                        !(cus.seatPosition.storeId == storeId && cus.seatPosition.sectionId == sectionId && cus.seatPosition.seatId == seatId)
                    }
                }
            }
        }

        Timber.i("needToNavigate : $needToNavigate, readUri : $readUri")
        if (needToNavigate) readUri else null
    }.onEach {
        Timber.i("💥 $it")
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        null
    )


    val arriveOnSeat = _nfcReadUri.onEach {readUri ->
        Timber.i("💥 $readUri")
        if (readUri == null) return@onEach
        val storeId = readUri.getQueryParameter(STORE_ID_ARG)
        val sectionId = readUri.getQueryParameter(SECTION_ID_ARG)
        val seatId = readUri.getQueryParameter(SEAT_ID_ARG)

        when (val cus = currentUserSession.value) {
            null,
            UserSession.None -> return@onEach

            is UserSession.UsingSeat -> {
                if (cus.seatPosition.storeId == storeId && cus.seatPosition.sectionId == sectionId && cus.seatPosition.seatId == seatId) {
                    when (cus.currentState) {
                        UserStateType.None -> throw IllegalStateException()
                        UserStateType.Occupied -> return@onEach
                        UserStateType.Reserved -> seatFinderService.occupySeat()
                        UserStateType.Away,
                        UserStateType.OnBusiness -> seatFinderService.resumeUsing()
                    }
                } else return@onEach
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        null
    )
}
