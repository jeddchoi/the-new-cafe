package io.github.jeddchoi.mypage.session

import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.data.service.seatfinder.SeatFinderUserRequestType
import io.github.jeddchoi.model.SeatPosition
import io.github.jeddchoi.model.UserSession
import io.github.jeddchoi.model.UserStateType
import io.github.jeddchoi.mypage.R
import kotlinx.datetime.Instant

/**
 * Sealed interface that represents the current status of the user.
 */
sealed interface DisplayedUserSession {
    val stateName: UiText
    fun canDo(action: SeatFinderUserRequestType): Boolean

    /**
     * Represents the case where the user has no status.
     */
    object None : DisplayedUserSession {
        override val stateName: UiText = UiText.StringResource(R.string.user_session_none)

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
    sealed interface UsingSeat : DisplayedUserSession {
        val sessionTimer: SessionTimer
        val currentStateTimer: SessionTimer
        val hasFailure: Boolean
        val seatPosition: SeatPosition
        val resultStateAfterCurrentState: UserStateType?

        /**
         * Represents the case where the user has reserved a seat but has not yet started using it.
         */
        data class Reserved(
            override val sessionTimer: SessionTimer,
            override val currentStateTimer: SessionTimer,
            override val hasFailure: Boolean,
            override val seatPosition: SeatPosition,
            override val resultStateAfterCurrentState: UserStateType?,
        ) : UsingSeat {
            override val stateName: UiText = UiText.StringResource(R.string.user_session_reserved)

            override fun canDo(action: SeatFinderUserRequestType): Boolean =
                when (action) {
                    SeatFinderUserRequestType.Quit,
                    SeatFinderUserRequestType.Occupy,
                    SeatFinderUserRequestType.ChangeReservationEndTime -> true

                    SeatFinderUserRequestType.Reserve,
                    SeatFinderUserRequestType.DoBusiness,
                    SeatFinderUserRequestType.LeaveAway,
                    SeatFinderUserRequestType.ResumeUsing,
                    SeatFinderUserRequestType.ChangeOccupyEndTime,
                    SeatFinderUserRequestType.ChangeBusinessEndTime,
                    SeatFinderUserRequestType.ChangeAwayEndTime -> false
                }
        }


        /**
         * Represents the case where the user is currently using a seat.
         */
        data class Occupied(
            override val sessionTimer: SessionTimer,
            override val currentStateTimer: SessionTimer,
            override val hasFailure: Boolean,
            override val seatPosition: SeatPosition,
            override val resultStateAfterCurrentState: UserStateType?,
        ) : UsingSeat {
            override val stateName: UiText = UiText.StringResource(R.string.user_session_occupied)
            override fun canDo(action: SeatFinderUserRequestType): Boolean =
                when (action) {
                    SeatFinderUserRequestType.Quit,
                    SeatFinderUserRequestType.DoBusiness,
                    SeatFinderUserRequestType.LeaveAway,
                    SeatFinderUserRequestType.ChangeOccupyEndTime -> true

                    SeatFinderUserRequestType.Reserve,
                    SeatFinderUserRequestType.Occupy,
                    SeatFinderUserRequestType.ResumeUsing,
                    SeatFinderUserRequestType.ChangeReservationEndTime,
                    SeatFinderUserRequestType.ChangeBusinessEndTime,
                    SeatFinderUserRequestType.ChangeAwayEndTime -> false
                }
        }

        /**
         * Represents the case where the user is away.
         */
        data class Away(
            override val sessionTimer: SessionTimer,
            override val currentStateTimer: SessionTimer,
            override val hasFailure: Boolean,
            override val seatPosition: SeatPosition,
            override val resultStateAfterCurrentState: UserStateType?,
        ) : UsingSeat {
            override val stateName: UiText = UiText.StringResource(R.string.user_session_away)
            override fun canDo(action: SeatFinderUserRequestType): Boolean =
                when (action) {
                    SeatFinderUserRequestType.Quit,
                    SeatFinderUserRequestType.DoBusiness,
                    SeatFinderUserRequestType.ResumeUsing,
                    SeatFinderUserRequestType.ChangeOccupyEndTime,
                    SeatFinderUserRequestType.ChangeAwayEndTime -> true

                    SeatFinderUserRequestType.Reserve,
                    SeatFinderUserRequestType.Occupy,
                    SeatFinderUserRequestType.LeaveAway,
                    SeatFinderUserRequestType.ChangeReservationEndTime,
                    SeatFinderUserRequestType.ChangeBusinessEndTime -> false
                }
        }

        /**
         * Represents the case where the user is on business away from the seat.
         */
        data class OnBusiness(
            override val sessionTimer: SessionTimer,
            override val currentStateTimer: SessionTimer,
            override val hasFailure: Boolean,
            override val seatPosition: SeatPosition,
            override val resultStateAfterCurrentState: UserStateType?,
        ) : UsingSeat {
            override val stateName: UiText = UiText.StringResource(R.string.user_session_on_business)
            override fun canDo(action: SeatFinderUserRequestType): Boolean =
                when (action) {
                    SeatFinderUserRequestType.Quit,
                    SeatFinderUserRequestType.ResumeUsing,
                    SeatFinderUserRequestType.ChangeOccupyEndTime,
                    SeatFinderUserRequestType.ChangeBusinessEndTime -> true

                    SeatFinderUserRequestType.Reserve,
                    SeatFinderUserRequestType.Occupy,
                    SeatFinderUserRequestType.DoBusiness,
                    SeatFinderUserRequestType.LeaveAway,
                    SeatFinderUserRequestType.ChangeReservationEndTime,
                    SeatFinderUserRequestType.ChangeAwayEndTime -> false
                }
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
                UserStateType.Reserved -> DisplayedUserSession.UsingSeat.Reserved(
                    sessionTimer = sessionTimer,
                    currentStateTimer = currentStateTimer,
                    hasFailure = hasFailure,
                    seatPosition = seatPosition,
                    resultStateAfterCurrentState = requestTypeAfterCurrentState?.resultState,
                )

                UserStateType.Occupied -> DisplayedUserSession.UsingSeat.Occupied(
                    sessionTimer = sessionTimer,
                    currentStateTimer = currentStateTimer,
                    hasFailure = hasFailure,
                    seatPosition = seatPosition,
                    resultStateAfterCurrentState = requestTypeAfterCurrentState?.resultState,
                )

                UserStateType.Away -> DisplayedUserSession.UsingSeat.Away(
                    sessionTimer = sessionTimer,
                    currentStateTimer = currentStateTimer,
                    hasFailure = hasFailure,
                    seatPosition = seatPosition,
                    resultStateAfterCurrentState = requestTypeAfterCurrentState?.resultState,
                )

                UserStateType.OnBusiness -> DisplayedUserSession.UsingSeat.OnBusiness(
                    sessionTimer = sessionTimer,
                    currentStateTimer = currentStateTimer,
                    hasFailure = hasFailure,
                    seatPosition = seatPosition,
                    resultStateAfterCurrentState = requestTypeAfterCurrentState?.resultState,
                )
            }
        }
    }
}
