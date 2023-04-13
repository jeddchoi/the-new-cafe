package io.github.jeddchoi.ui.feature

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

interface CanHaveData<T> {
    val data: T?
}

sealed interface UiState<out T> {

    data class Loading<T>(override val data: T? = null) : UiState<T>, CanHaveData<T>

    data class Error<T>(val exception: Throwable, override val data: T? = null) : UiState<T>,
        CanHaveData<T>

    object EmptyResult : UiState<Nothing>
    data class Success<T>(override val data: T) : UiState<T>, CanHaveData<T>
}


fun <T> Flow<T>.asUiState(): Flow<UiState<T>> {
    return this
        .map<T, UiState<T>> {
            if (it == null || it is Collection<*> && it.isEmpty()) UiState.EmptyResult
            else UiState.Success(it)
        }
        .onStart { emit(UiState.Loading()) }
        .catch { emit(UiState.Error(it)) }
}


