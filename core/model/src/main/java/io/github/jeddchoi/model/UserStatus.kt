package io.github.jeddchoi.model

data class UserStatus(
    val lastChange: UserStatusChange? = null,
    val history: List<UserStatusChange> = emptyList(),
)
