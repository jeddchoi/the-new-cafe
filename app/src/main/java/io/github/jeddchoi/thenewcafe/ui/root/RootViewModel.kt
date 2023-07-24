package io.github.jeddchoi.thenewcafe.ui.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jeddchoi.data.repository.AppFlagsRepository
import io.github.jeddchoi.data.repository.UserSessionRepository
import io.github.jeddchoi.model.UserStateType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    private val userSessionRepository: UserSessionRepository,
    private val appFlagsRepository: AppFlagsRepository
) : ViewModel() {

    val redirectToAuth: StateFlow<Boolean> = appFlagsRepository.getShowMainScreenOnStart
        .map { it.not() }
        .onEach { Timber.v("💥 $it") }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val startMyService =
        userSessionRepository.userSession.map { it?.currentState == UserStateType.Reserved }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                false
            )
}