package io.github.jeddchoi.order.store

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class StoreViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel(){
    private val storeArgs = StoreArgs(savedStateHandle)
}