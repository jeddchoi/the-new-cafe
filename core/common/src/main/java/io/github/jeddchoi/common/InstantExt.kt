package io.github.jeddchoi.common

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun getLocalDateTimeWithMyTimeZone(epochMilli: Long) =
    Instant.fromEpochMilliseconds(epochMilli).toLocalDateTime(TimeZone.currentSystemDefault())