package io.github.jeddchoi.model

import kotlinx.datetime.Instant

/**
 * Sealed interface that represents the current status of the user.
 */
sealed interface DisplayedUserSession {
    val state: UserStateType
    fun canDo(action: SeatFinderUserRequestType): Boolean

    /**
     * Represents the case where the user has no status.
     */
    object None : DisplayedUserSession {
        override val state: UserStateType = UserStateType.None

        override fun canDo(action: SeatFinderUserRequestType): Boolean =
            when (action) {
                SeatFinderUserRequestType.Reserve -> true

                SeatFinderUserRequestType.Occupy,
                SeatFinderUserRequestType.Quit,
                SeatFinderUserRequestType.DoBusiness,
                SeatFinderUserRequestType.LeaveAway,
                SeatFinderUserRequestType.ResumeUsing,
                SeatFinderUserRequestType.ChangeReservationEndTime,
                SeatFinderUserRequestType.ChangeOccupyEndTime,
                SeatFinderUserRequestType.ChangeBusinessEndTime,
                SeatFinderUserRequestType.ChangeAwayEndTime -> false
            }
    }

    /**
     * Sealed interface that represents the cases where the user is related to using a seat.
     *
     */
    data class UsingSeat(
        val sessionTimer: SessionTimer,
        val currentStateTimer: SessionTimer,
        val hasFailure: Boolean,
        val seatPosition: SeatPosition,
        val resultStateAfterCurrentState: UserStateType?,
        override val state: UserStateType,
    ) : DisplayedUserSession {
        override fun canDo(action: SeatFinderUserRequestType): Boolean {
            return action.availableState.contains(state)
        }
    }
}

fun UserSession.toDisplayedUserSession(current: Instant): DisplayedUserSession {
    when (this) {
        UserSession.None -> {
            return DisplayedUserSession.None
        }

        is UserSession.UsingSeat -> {
            this.currentState
            val sessionTimer = SessionTimer(
                startTime = startSessionTime,
                endTime = endSessionTime,
                elapsedTime = current - startSessionTime,
                remainingTime = endSessionTime?.minus(current),
                totalTime = endSessionTime?.minus(startSessionTime),
            )
            val currentStateTimer = SessionTimer(
                startTime = startTime,
                endTime = endTime,
                elapsedTime = current - startTime,
                remainingTime = endTime?.minus(current),
                totalTime = endTime?.minus(startTime),
            )
            return when (currentState) {
                UserStateType.None -> DisplayedUserSession.None
                UserStateType.Reserved,
                UserStateType.Occupied,
                UserStateType.Away,
                UserStateType.OnBusiness -> DisplayedUserSession.UsingSeat(
                    sessionTimer = sessionTimer,
                    currentStateTimer = currentStateTimer,
                    hasFailure = hasFailure,
                    seatPosition = seatPosition,
                    resultStateAfterCurrentState = requestTypeAfterCurrentState?.resultState,
                    state = currentState
                )
            }
        }
    }
}
