package io.github.jeddchoi.data.service

import androidx.annotation.StringRes
import io.github.jeddchoi.data.R

/**
 * Result code
 * This includes success, business error and technical error
 *
 * @property value
 * @property description
 * @constructor Create empty Result code
 */
enum class ResultCode(val value: Int, @StringRes val description: Int){
    // SUCCESS
    OK(0, R.string.result_ok),

    UNAUTHENTICATED(100, R.string.result_unauthenticated), // prompts client that user should be logged in
    SEAT_NOT_AVAILABLE(101, R.string.result_seat_not_available), // prompts client that seat is not available
    ALREADY_IN_PROGRESS(102, R.string.result_already_in_progress), // prompts client that user is already in progress
    TIMER_CHANGE_NOT_AVAILABLE(103, R.string.result_timer_change_not_available), // prompts client that timer change is not available
    ALREADY_TIMEOUT(104, R.string.result_already_timeout), // prompts client that user is already timeout
    TEMPORARY_LONGER_THAN_OVERALL(105, R.string.result_temporary_longer_than_overall), // prompts client that temporary longer than overall
    PERMISSION_DENIED(106, R.string.result_permission_denied), // prompts client that permission denied
    INVALID_STATE(107, R.string.result_invalid_state), // prompts client that invalid state

    CANCELLED(201, R.string.result_cancelled),
    UNKNOWN(202, R.string.result_unknown),
    NETWORK_FAILURE(203, R.string.result_network_failure),
    INVALID_ARGUMENT(204, R.string.result_invalid_argument),
    FAILED_PRECONDITION(205, R.string.result_failed_precondition),
    DEADLINE_EXCEEDED(206, R.string.result_deadline_exceeded),

    NOT_FOUND(301, R.string.result_not_found),
    ALREADY_EXISTS(302, R.string.result_already_exists),
    RESOURCE_EXHAUSTED(303, R.string.result_resource_exhausted),
    ABORTED(304, R.string.result_aborted),
    OUT_OF_RANGE(305, R.string.result_out_of_range),
    UNIMPLEMENTED(306, R.string.result_unimplemented),
    INTERNAL(307, R.string.result_internal),
    UNAVAILABLE(308, R.string.result_unavailable),
    DATA_LOSS(309, R.string.result_data_loss)
    ;

    companion object {
        private val VALUES = values()
        fun getByValue(value: Int) = VALUES.firstOrNull { it.value == value } ?: UNKNOWN
    }
}