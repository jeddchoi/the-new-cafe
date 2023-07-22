package io.github.jeddchoi.mypage.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jeddchoi.data.repository.UserSessionHistoryRepository
import io.github.jeddchoi.data.repository.UserSessionRepository
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class HistoryViewModel @Inject constructor(
    private val userSessionHistoryRepository: UserSessionHistoryRepository,
    private val userSessionRepository: UserSessionRepository,
) : ViewModel() {

    val histories = userSessionHistoryRepository.getHistories()
        .cachedIn(viewModelScope).onEach { Timber.v("💥 $it") }
    val currentSession = userSessionRepository.userSession.onEach { Timber.v("💥 $it") }

}
