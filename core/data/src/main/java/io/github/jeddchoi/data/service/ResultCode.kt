package io.github.jeddchoi.data.service

import androidx.annotation.StringRes
import com.google.firebase.functions.FirebaseFunctionsException
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


    NETWORK_FAILURE(100, R.string.result_network_failure),
    CANCELLED(1, R.string.result_cancelled),
    UNKNOWN(2, R.string.result_unknown),
    INVALID_ARGUMENT(3, R.string.result_invalid_argument),
    PERMISSION_DENIED(7, R.string.result_permission_denied),
    FAILED_PRECONDITION(9, R.string.result_failed_precondition),
    UNAUTHENTICATED(16, R.string.result_unauthenticated),
    DEADLINE_EXCEEDED(4, R.string.result_deadline_exceeded),

    NOT_FOUND(5, R.string.result_not_found),
    ALREADY_EXISTS(6, R.string.result_already_exists),
    RESOURCE_EXHAUSTED(8, R.string.result_resource_exhausted),
    ABORTED(10, R.string.result_aborted),
    OUT_OF_RANGE(11, R.string.result_out_of_range),
    UNIMPLEMENTED(12, R.string.result_unimplemented),
    INTERNAL(13, R.string.result_internal),
    UNAVAILABLE(14, R.string.result_unavailable),
    DATA_LOSS(15, R.string.result_data_loss)
    ;

    companion object {
        private val VALUES = values()
        fun getByValue(value: Int) = VALUES.firstOrNull { it.value == value } ?: UNKNOWN
    }
}



/**
 * Client specified an invalid argument. Note that this differs from FAILED_PRECONDITION.
 * INVALID_ARGUMENT indicates arguments that are problematic regardless of the state of the
 * system (e.g., an invalid field name).
 */
var INVALID_ARGUMENT: FirebaseFunctionsException.Code? = null,

/**
 * Deadline expired before operation could complete. For operations that change the state of the
 * system, this error may be returned even if the operation has completed successfully. For
 * example, a successful response from a server could have been delayed long enough for the
 * deadline to expire.
 */
var DEADLINE_EXCEEDED: FirebaseFunctionsException.Code? = null,

/** Some requested document was not found.  */
var NOT_FOUND: FirebaseFunctionsException.Code? = null,

/** Some document that we attempted to create already exists.  */
var ALREADY_EXISTS: FirebaseFunctionsException.Code? = null,

/** The caller does not have permission to execute the specified operation.  */
var PERMISSION_DENIED: FirebaseFunctionsException.Code? = null,

/**
 * Some resource has been exhausted, perhaps a per-user quota, or perhaps the entire file system
 * is out of space.
 */
var RESOURCE_EXHAUSTED: FirebaseFunctionsException.Code? = null,

/**
 * Operation was rejected because the system is not in a state required for the operation's
 * execution.
 */
var FAILED_PRECONDITION: FirebaseFunctionsException.Code? = null,

/**
 * The operation was aborted, typically due to a concurrency issue like transaction aborts, etc.
 */
var ABORTED: FirebaseFunctionsException.Code? = null,

/** Operation was attempted past the valid range.  */
var OUT_OF_RANGE: FirebaseFunctionsException.Code? = null,

/** Operation is not implemented or not supported/enabled.  */
var UNIMPLEMENTED: FirebaseFunctionsException.Code? = null,

/**
 * Internal errors. Means some invariants expected by underlying system has been broken. If you
 * see one of these errors, something is very broken.
 */
var INTERNAL: FirebaseFunctionsException.Code? = null,

/**
 * The service is currently unavailable. This is a most likely a transient condition and may be
 * corrected by retrying with a backoff.
 */
var UNAVAILABLE: FirebaseFunctionsException.Code? = null,

/** Unrecoverable data loss or corruption.  */
var DATA_LOSS: FirebaseFunctionsException.Code? = null,

