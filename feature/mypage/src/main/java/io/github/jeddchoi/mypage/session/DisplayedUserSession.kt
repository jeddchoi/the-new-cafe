package io.github.jeddchoi.mypage.session

/**
 * Sealed interface that represents the current status of the user.
 */
sealed interface DisplayedUserSession {
    /**
     * Represents the case where the user has no status.
     */
    object None : DisplayedUserSession

    /**
     * Sealed interface that represents the cases where the user is related to using a seat.
     *
     * @property startTime The time when the user started current status.
     * @property timeoutLimit The amount of time the user is allowed to be in current status.
     * @property isExtended Whether the user has extended the time limit.
     * @property extendable Whether the user can extend the time limit.
     * @property extendedTime The amount of time by which the user has extended the time limit.
     * @property timeIncrement The amount of time by which the user can extend the time limit by a request.
     * @property endSessionTime The time when the user is expected to finish current status.
     * @property remainingTime The amount of time remaining until the user finishes current status.
     * @property elapsedTime The amount of time that has elapsed since the user started current status.
     */
    sealed interface UsingSeat : DisplayedUserSession {
        val mainTimer: SessionTimer
        val subTimer: SessionTimer?



        /**
         * Represents the case where the user has reserved a seat but has not yet started using it.
         */
        data class Reserved(
            override val mainTimer: SessionTimer,
            override val subTimer: SessionTimer?,
        ) : UsingSeat


        /**
         * Represents the case where the user is currently using a seat.
         */
        data class Occupied(
            override val mainTimer: SessionTimer,
            override val subTimer: SessionTimer?,
        ) : UsingSeat

        /**
         * Represents the case where the user is away.
         */
        data class Away(
            override val mainTimer: SessionTimer,
            override val subTimer: SessionTimer?,
        ) : UsingSeat

        /**
         * Represents the case where the user is on business away from the seat.
         */
        data class OnBusiness(
            override val mainTimer: SessionTimer,
            override val subTimer: SessionTimer?,
        ) : UsingSeat
    }

}

//        fun remainingTime(current:Instant) = etc?.minus(current)
//        fun elapsedTimeSinceStart(current: Instant) = current - startTime
//        fun elapsedTimeSinceStartSession(current: Instant) = current - startSessionTime
