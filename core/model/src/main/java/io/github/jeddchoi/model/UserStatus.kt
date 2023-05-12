package io.github.jeddchoi.model

import kotlinx.datetime.Instant

data class UserStatus(
    val lastStatus: UserStatusType,
    val status: UserStatusType,
    val cause: UserStatusChangeCause,
    val updatedAt: Instant,
    val seatPos: SeatPosition?,
    val occupyExpirationInfo: TimerTaskInfo?,
    val currentStatusExpirationInfo: TimerTaskInfo?,
)

data class TimerTaskInfo(
    val endTime: Instant,
    val startTime: Instant,
    val timerTaskName: String,
)

