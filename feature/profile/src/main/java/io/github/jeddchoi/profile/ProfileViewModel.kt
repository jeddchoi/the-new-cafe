package io.github.jeddchoi.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jeddchoi.ui.model.FeedbackState
import io.github.jeddchoi.ui.model.Message
import io.github.jeddchoi.ui.model.UiState
import kotlinx.coroutines.flow.*

internal class ProfileViewModel(

) : ViewModel() {

    private val _uiState = flow {
        emit(ProfileUiStateData("Profile"))
    }

    val uiState: StateFlow<UiState<ProfileUiStateData>> =
        _uiState.map {
            if (it.data.isBlank()) UiState.EmptyResult
            else UiState.Success(it)
        }.catch {
            emit(UiState.Error(it))
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            UiState.InitialLoading
        )

    fun signOut() {

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