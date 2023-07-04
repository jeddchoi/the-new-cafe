package io.github.jeddchoi.thenewcafe.ui.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jeddchoi.data.repository.CurrentUserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    private val currentUserRepository: CurrentUserRepository,
) : ViewModel() {

    val startDestination: StateFlow<RootNav?> = currentUserRepository.currentUserId.map {
        if (it != null)
            RootNav.Main
        else
            RootNav.Auth
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}