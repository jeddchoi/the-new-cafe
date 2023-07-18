package io.github.jeddchoi.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jeddchoi.common.OneShotFeedbackUiState
import io.github.jeddchoi.data.repository.AppFlagsRepository
import io.github.jeddchoi.data.repository.AuthRepository
import io.github.jeddchoi.data.repository.UserProfileRepository
import io.github.jeddchoi.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userProfileRepository: UserProfileRepository,
    private val appFlagsRepository: AppFlagsRepository,
) : ViewModel() {


    private val feedbackUiState = MutableStateFlow(OneShotFeedbackUiState())
    val uiState = combine(userProfileRepository.userProfile, feedbackUiState) { profile, feedback ->
        if (profile != null) {
            ProfileUiState.Success(profile, feedback)
        } else {
            ProfileUiState.NotAuthenticated
        }
    }.catch {
        Log.e("uiState", it.stackTraceToString())
        emit(ProfileUiState.Error(it))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileUiState.InitialLoading)


    fun signOut() {
        viewModelScope.launch {
            authRepository.logout()
            appFlagsRepository.setShowMainScreenOnStart(false)
        }
    }
}

internal sealed class ProfileUiState {
    object InitialLoading : ProfileUiState()

    object NotAuthenticated : ProfileUiState()

    data class Success(
        val profile: UserProfile = UserProfile(),
        val feedback: OneShotFeedbackUiState = OneShotFeedbackUiState(),
    ) : ProfileUiState()

    data class Error(val exception: Throwable) : ProfileUiState()
}