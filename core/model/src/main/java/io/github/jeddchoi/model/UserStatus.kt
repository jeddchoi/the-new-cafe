package io.github.jeddchoi.model

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
data class UserStatus(
    val lastStatus: UserStatusType = UserStatusType.None,
    val status: UserStatusType = UserStatusType.None,
    val cause: UserStatusChangeCause = UserStatusChangeCause.UserAction,
    val updateTimestamp: Long = Clock.System.now().epochSeconds,
    val seatPos: SeatPosition? = null,
    val occupyExpirationInfo: TimerTaskInfo? = null,
    val currentStatusExpirationInfo: TimerTaskInfo? = null,
)

@Serializable
data class TimerTaskInfo(
    val endTimestamp: Long = Clock.System.now().epochSeconds,
    val startTimestamp: Long = Clock.System.now().epochSeconds,
    val timerTaskName: String = "",
)

