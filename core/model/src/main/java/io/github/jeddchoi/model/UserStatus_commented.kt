package io.github.jeddchoi.model
//
///**
// * Sealed interface that represents the current status of the user.
// */
//sealed interface UserStatus {
//
//    /**
//     * Represents the case where the user has no status.
//     */
//    object None : UserStatus
//
//    /**
//     * Represents the case where the user is blocked by the system.
//     *
//     * @property blockedTime The time when the user was blocked.
//     * @property timeElapsedSinceBlocked The amount of time that has elapsed since the user was blocked.
//     */
//    data class Blocked(
//        val blockedTime: Long,
//        val timeElapsedSinceBlocked: Long
//    ) : UserStatus
//
//
//    /**
//     * Sealed interface that represents the cases where the user is related to using a seat.
//     *
//     * @property startTime The time when the user started current status.
//     * @property timeoutLimit The amount of time the user is allowed to be in current status.
//     * @property isExtended Whether the user has extended the time limit.
//     * @property extendable Whether the user can extend the time limit.
//     * @property extendedTime The amount of time by which the user has extended the time limit.
//     * @property timeIncrement The amount of time by which the user can extend the time limit by a request.
//     * @property endTime The time when the user is expected to finish current status.
//     * @property remainingTime The amount of time remaining until the user finishes current status.
//     * @property elapsedTimeSinceStart The amount of time that has elapsed since the user started current status.
//     */
//    sealed interface UsingSeat : UserStatus {
//        val startTime: Long
//        val timeoutLimit: Long
//        val totalUsedTime: Long
//
//        val isExtended: Boolean
//        val extendable: Boolean
//        val extendedTime: Long
//        val timeIncrement: Long
//
//        val endTime: Long
//            get() = startTime + timeoutLimit + extendedTime
//
//        val remainingTime: Long
//            get() = endTime - System.currentTimeMillis()
//
//        val elapsedTimeSinceStart: Long
//            get() = System.currentTimeMillis() - startTime
//
//
//        /**
//         * Represents the case where the user has reserved a seat but has not yet started using it.
//         */
//        data class Reserved(
//            override val startTime: Long,
//            override val timeoutLimit: Long,
//            override val isExtended: Boolean,
//            override val extendable: Boolean,
//            override val extendedTime: Long,
//            override val timeIncrement: Long
//        ) : UsingSeat {
//            override val totalUsedTime: Long = 0L
//        }
//
//
//        /**
//         * Represents the case where the user is currently using a seat.
//         */
//        data class Occupied(
//            override val startTime: Long,
//            override val timeoutLimit: Long,
//            override val totalUsedTime: Long,
//            override val isExtended: Boolean,
//            override val extendable: Boolean,
//            override val extendedTime: Long,
//            override val timeIncrement: Long
//
//        ) : UsingSeat
//
//        /**
//         * Represents the case where the user is vacant.
//         */
//        data class Vacant(
//            override val startTime: Long,
//            override val timeoutLimit: Long,
//            override val totalUsedTime: Long,
//            override val isExtended: Boolean,
//            override val extendable: Boolean,
//            override val extendedTime: Long,
//            override val timeIncrement: Long
//
//        ) : UsingSeat
//
//        /**
//         * Represents the case where the user is on a task away from the seat.
//         */
//        data class OnTask(
//            override val startTime: Long,
//            override val timeoutLimit: Long,
//            override val totalUsedTime: Long,
//            override val isExtended: Boolean,
//            override val extendable: Boolean,
//            override val extendedTime: Long,
//            override val timeIncrement: Long
//
//        ) : UsingSeat
//    }
//
//}
