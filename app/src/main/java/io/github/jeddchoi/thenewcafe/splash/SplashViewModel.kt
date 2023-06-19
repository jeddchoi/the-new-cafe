package io.github.jeddchoi.thenewcafe.splash

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jeddchoi.authentication.SignInNavigation
import io.github.jeddchoi.data.repository.AuthRepository
import io.github.jeddchoi.data.repository.CurrentUserRepository
import io.github.jeddchoi.thenewcafe.navigation.main.MainNavigation
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


    private val _startDestination: MutableState<String> = mutableStateOf(SignInNavigation.routeGraph)
    val startDestination: MutableState<String> = _startDestination


    private fun initialize() {
        viewModelScope.launch {
            if (currentUserRepository.isUserSignedIn()) {
                _startDestination.value = MainNavigation.route()
            } else {
                _startDestination.value = SignInNavigation.routeGraph
            }
            _isLoading.emit(false)
        }
    }

    init {
        initialize()
    }
}