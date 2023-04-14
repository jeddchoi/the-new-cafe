package io.github.jeddchoi.ui.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*


sealed class UiState<out T : FeedbackState> {
    object InitialLoading : UiState<Nothing>()
    object EmptyResult : UiState<Nothing>()
    data class Success<T : FeedbackState>(val data: T) : UiState<T>()
    data class Error(val exception: Throwable) : UiState<Nothing>()
}


fun <T : FeedbackState> Flow<T>.asUiState(
    scope: CoroutineScope
): StateFlow<UiState<T>> =
    this.map {
        if (it is Collection<*> && it.isEmpty()) UiState.EmptyResult
        else UiState.Success(it)
    }.catch {
        emit(UiState.Error(it))
    }.stateIn(scope, SharingStarted.WhileSubscribed(5_000), UiState.InitialLoading)

