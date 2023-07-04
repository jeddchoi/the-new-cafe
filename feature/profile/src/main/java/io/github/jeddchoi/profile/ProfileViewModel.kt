package io.github.jeddchoi.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jeddchoi.data.repository.AuthRepository
import io.github.jeddchoi.data.repository.CurrentUserRepository
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
    private val currentUserRepository: CurrentUserRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiStateData())

    val uiState: StateFlow<UiState<ProfileUiStateData>> = _uiState.asUiState(viewModelScope)

    fun signOut() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    init {
        viewModelScope.launch {
            currentUserRepository.currentUser.collect {
                if (it != null) {
                    _uiState.value = _uiState.value.copy(
                        displayName = it.toString(),
                        uid = it.authId,
                        emailAddress = it.emailAddress,
                        isEmailVerified = it.isEmailVerified,
                        isBlocked = it.isBlocked,
                        blockEndTime = it.blockEndTime
                    )
                }
            }
        }
    }
}


internal data class ProfileUiStateData(
    val displayName: String = "Not logged in",
    val uid: String = "",
    val emailAddress: String = "",
    val isEmailVerified: Boolean = false,
    val isBlocked: Boolean = false,
    val blockEndTime: Long? = null,
    override val isBusy: Boolean = false,
    override val canContinue: Boolean = true,
    override val messages: List<Message> = emptyList()
) : FeedbackState {
    override fun copy(
        isBusy: Boolean,
        canContinue: Boolean,
        messages: List<Message>
    ): FeedbackState = ProfileUiStateData(
        displayName,
        uid,
        emailAddress,
        isEmailVerified,
        isBlocked,
        blockEndTime,
        isBusy,
        canContinue,
        messages
    )
}