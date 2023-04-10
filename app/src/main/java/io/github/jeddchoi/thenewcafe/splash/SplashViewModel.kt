package io.github.jeddchoi.thenewcafe.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()


    private fun initialize() {
        viewModelScope.launch {
            _isLoading.emit(false)
        }
    }

    init {
        initialize()
    }
}