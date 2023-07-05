package io.github.jeddchoi.data.util

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun getCurrentDateTime() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()
fun getCurrentTime() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time.toString()