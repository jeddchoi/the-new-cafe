package io.github.jeddchoi.model


enum class UserStatusType {
    None,
    Reserved,
    Occupied,
    Vacant,
    OnTask,
    Blocked,
}

enum class UserStatusChangeCause {
    UserAction,
    Timeout,
    Admin,
}