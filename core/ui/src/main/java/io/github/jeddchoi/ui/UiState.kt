package io.github.jeddchoi.ui

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed interface UiState<out T> {
    data class Loading<T>(val data: T? = null) : UiState<T>
    object Empty : UiState<Nothing>
    data class Error<T>(val exception: Throwable, val data: T? = null) : UiState<T>
    data class Success<T>(val data: T) : UiState<T>
}


fun <T> Flow<T>.asUiState(): Flow<UiState<T>> {
    return this
        .map<T, UiState<T>> {
            if (it == null || it is Collection<*> && it.isEmpty()) UiState.Empty
            else UiState.Success(it)
        }
        .onStart { emit(UiState.Loading()) }
        .catch { emit(UiState.Error(it)) }
}


