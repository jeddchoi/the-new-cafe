package io.github.jeddchoi.data.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout


class TickHandler(
    private val externalScope: CoroutineScope,
    private val tickIntervalMs: Long = 5000
) {
    // Backing property to avoid flow emissions from other classes
    private val _tickFlow = MutableSharedFlow<Unit>(replay = 0)
    val tickFlow: SharedFlow<Unit> = _tickFlow

    init {
        val now = System.currentTimeMillis()
        externalScope.launch {

            withTimeout(tickIntervalMs) {
                _tickFlow.emit(Unit)
            }
        }
    }
}

fun main() {
    GlobalScope.launch {
        while(true) {
            val now = System.currentTimeMillis()
            println(now)
            delay(1000)
        }
    }
}