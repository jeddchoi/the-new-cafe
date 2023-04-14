package io.github.jeddchoi.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jeddchoi.data.repository.AuthRepository
import io.github.jeddchoi.ui.model.FeedbackState
import io.github.jeddchoi.ui.model.Message
import io.github.jeddchoi.ui.model.UiState
import io.github.jeddchoi.ui.model.asUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiStateData(""))

    val uiState: StateFlow<UiState<ProfileUiStateData>> = _uiState.asUiState(viewModelScope)

    fun signOut() {

    }

    init {
        Log.i("Auth", System.identityHashCode(authRepository).toString() )
        viewModelScope.launch {
            authRepository.getCurrentUser().collect() {
                _uiState.value = _uiState.value.copy(data = it?.toString() ?: "Not logged in")
            }
        }
    }
}


internal data class ProfileUiStateData(
    val data: String,
    override val isBusy: Boolean = false,
    override val canContinue: Boolean = true,
    override val messages: List<Message> = emptyList()
) : FeedbackState {
    override fun copy(
        isBusy: Boolean,
        canContinue: Boolean,
        messages: List<Message>
    ): FeedbackState = ProfileUiStateData(data, isBusy, canContinue, messages)
}