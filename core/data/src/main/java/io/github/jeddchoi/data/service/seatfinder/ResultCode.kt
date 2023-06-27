package io.github.jeddchoi.data.service.seatfinder

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
enum class ResultCode(@StringRes val description: Int) {
    // SUCCESS
    OK(R.string.result_ok),
    REJECTED(R.string.result_rejected),
    INVALID_SEAT_STATE(R.string.result_invalid_seat_state),
    INVALID_SESSION_STATE(R.string.result_invalid_session_state),
    CORRUPTED(R.string.result_corrupted),
    UNAUTHENTICATED(R.string.result_unauthenticated), // prompts client that user should be logged in
    SEAT_NOT_AVAILABLE(R.string.result_seat_not_available), // prompts client that seat is not available
    ALREADY_IN_PROGRESS(R.string.result_already_in_progress), // prompts client that user is already in progress
    TIMER_CHANGE_NOT_AVAILABLE(R.string.result_timer_change_not_available), // prompts client that timer change is not available
    ALREADY_TIMEOUT(R.string.result_already_timeout), // prompts client that user is already timeout
    TEMPORARY_LONGER_THAN_OVERALL(R.string.result_temporary_longer_than_overall), // prompts client that temporary longer than overall
    PERMISSION_DENIED(R.string.result_permission_denied), // prompts client that permission denied
    INVALID_STATE(R.string.result_invalid_state), // prompts client that invalid state

    CANCELLED(R.string.result_cancelled),
    UNKNOWN(R.string.result_unknown),
    NETWORK_FAILURE(R.string.result_network_failure),
    INVALID_ARGUMENT(R.string.result_invalid_argument),
    FAILED_PRECONDITION(R.string.result_failed_precondition),
    DEADLINE_EXCEEDED(R.string.result_deadline_exceeded),

    NOT_FOUND(R.string.result_not_found),
    ALREADY_EXISTS(R.string.result_already_exists),
    RESOURCE_EXHAUSTED(R.string.result_resource_exhausted),
    ABORTED(R.string.result_aborted),
    OUT_OF_RANGE(R.string.result_out_of_range),
    UNIMPLEMENTED(R.string.result_unimplemented),
    INTERNAL(R.string.result_internal),
    UNAVAILABLE(R.string.result_unavailable),
    DATA_LOSS(R.string.result_data_loss)
    ;

    companion object {
        private val VALUES = values()
        fun getByValue(input: String?) =
            input?.let { VALUES.firstOrNull { code -> code.name == input } } ?: UNKNOWN
    }
}