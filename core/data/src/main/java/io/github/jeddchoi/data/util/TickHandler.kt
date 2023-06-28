package io.github.jeddchoi.data.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant


class TickHandler(
    private val externalScope: CoroutineScope,
    private val tickIntervalMs: Long = 1000
) {
    // Backing property to avoid flow emissions from other classes
    private val _tickFlow = MutableSharedFlow<Instant>(replay = 0)
    val tickFlow: SharedFlow<Instant> = _tickFlow

    init {
        externalScope.launch {
            while (true) {
                _tickFlow.emit(Clock.System.now())
                delay(tickIntervalMs)
            }
        }
    }
}