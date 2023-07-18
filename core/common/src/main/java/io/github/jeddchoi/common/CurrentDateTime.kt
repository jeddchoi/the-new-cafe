package io.github.jeddchoi.common

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun currentDateTimeStr() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()
fun currentTimeStr() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time.toString()