package io.github.jeddchoi.thenewcafe.splash

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jeddchoi.data.repository.CurrentUserRepository
import io.github.jeddchoi.thenewcafe.ui.root.RootNav
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val currentUserRepository: CurrentUserRepository,
) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()


    private val _startDestination = MutableStateFlow(RootNav.Auth)
    val startDestination: StateFlow<RootNav> = _startDestination


    private var initializeCalled = false

    // This function is idempotent provided it is only called from the UI thread.
    @MainThread
    fun initialize() {
        if (initializeCalled) return
        initializeCalled = true

        if (currentUserRepository.isUserSignedIn()) {
            _startDestination.value = RootNav.Main
        } else {
            _startDestination.value = RootNav.Auth
        }
        _isLoading.update { false }
    }
}