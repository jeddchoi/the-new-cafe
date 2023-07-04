package io.github.jeddchoi.thenewcafe.ui.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jeddchoi.data.repository.AppFlagsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    private val appFlagsRepository: AppFlagsRepository
) : ViewModel() {

    val shouldRedirectToAuth: StateFlow<Boolean> = appFlagsRepository.getShowMainScreenOnStart
        .map { !it }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

}