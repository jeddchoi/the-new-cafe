export const enum ResultCode {
    OK = 0,
    REJECTED = 99,
    UNAUTHENTICATED = 100, // prompts client that user should be logged in
    SEAT_NOT_AVAILABLE = 101, // prompts client that seat is not available
    ALREADY_IN_PROGRESS = 102, // prompts client that user is already in progress
    TIMER_CHANGE_NOT_AVAILABLE = 103, // prompts client that timer change is not available
    ALREADY_TIMEOUT = 104, // prompts client that user is already timeout
    TEMPORARY_LONGER_THAN_OVERALL = 105, // prompts client that temporary longer than overall
    PERMISSION_DENIED = 106, // prompts client that permission denied
    INVALID_SEAT_STATE = 107, // prompts client that invalid seat state
    INVALID_SESSION_STATE = 108, // prompts client that invalid seat state

    CORRUPTED = 200, // rollback failed
}
