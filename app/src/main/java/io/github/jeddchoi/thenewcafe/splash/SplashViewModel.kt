package io.github.jeddchoi.thenewcafe.splash

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jeddchoi.authentication.SigninNavigation
import io.github.jeddchoi.thenewcafe.navigation.main.MainNavigation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()


    private val _startDestination: MutableState<String> = mutableStateOf(SigninNavigation.routeGraph)
    val startDestination: MutableState<String> = _startDestination


    private fun initialize() {
        viewModelScope.launch {
            delay(1000L)
            _startDestination.value = MainNavigation.route()
            _isLoading.emit(false)
        }
    }

    init {
        initialize()
    }
}