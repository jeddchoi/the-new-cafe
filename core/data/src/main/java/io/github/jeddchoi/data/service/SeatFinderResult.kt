package io.github.jeddchoi.data.service

import io.github.jeddchoi.model.UserStateType
import kotlinx.datetime.Instant

data class SeatFinderResult(
    val resultCode: ResultCode = ResultCode.REJECTED,
    val afterSeatName: String? = null,
    val startSessionTime: Instant? = null,
    val previousState: UserStateType = UserStateType.None,
    val currentState: UserStateType = UserStateType.None,
    val startCurrentStateTime: Instant? = null,
    val endCurrentStateTime: Instant? = null,
    val requestTypeAfterCurrentState: SeatFinderRequestType? = null,
)