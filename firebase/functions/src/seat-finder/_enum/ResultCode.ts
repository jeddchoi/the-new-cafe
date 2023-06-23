export enum ResultCode {
    OK = "OK",
    REJECTED = "REJECTED",
    UNAUTHENTICATED = "UNAUTHENTICATED", // prompts client that user should be logged in
    SEAT_NOT_AVAILABLE = "SEAT_NOT_AVAILABLE", // prompts client that seat is not available
    ALREADY_IN_PROGRESS = "ALREADY_IN_PROGRESS", // prompts client that user is already in progress
    TIMER_CHANGE_NOT_AVAILABLE = "TIMER_CHANGE_NOT_AVAILABLE", // prompts client that timer change is not available
    ALREADY_TIMEOUT = "ALREADY_TIMEOUT", // prompts client that user is already timeout
    TEMPORARY_LONGER_THAN_OVERALL = "TEMPORARY_LONGER_THAN_OVERALL", // prompts client that temporary longer than overall
    PERMISSION_DENIED = "PERMISSION_DENIED", // prompts client that permission denied
    INVALID_SEAT_STATE = "INVALID_SEAT_STATE", // prompts client that invalid seat state
    INVALID_SESSION_STATE = "INVALID_SESSION_STATE", // prompts client that invalid seat state
    CORRUPTED = "CORRUPTED", // rollback failed
}
