package io.github.jeddchoi.thenewcafe.splash

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jeddchoi.data.repository.CurrentUserRepository
import io.github.jeddchoi.thenewcafe.navigation.root.RootNavScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val currentUserRepository: CurrentUserRepository,
) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()


    private val _startDestination: MutableState<RootNavScreen> = mutableStateOf(RootNavScreen.Auth)
    val startDestination: MutableState<RootNavScreen> = _startDestination


    private fun initialize() {
        viewModelScope.launch {
            if (currentUserRepository.isUserSignedIn()) {
                _startDestination.value = RootNavScreen.Main
            } else {
                _startDestination.value = RootNavScreen.Auth
            }
            _isLoading.emit(false)
        }
    }

    init {
        initialize()
    }
}