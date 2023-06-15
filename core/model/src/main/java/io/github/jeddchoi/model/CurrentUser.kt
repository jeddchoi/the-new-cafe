package io.github.jeddchoi.model

data class CurrentUser(
    val authId: String = "",
    val displayName: String = "Unknown",
    val emailAddress: String = "",
    val isEmailVerified: Boolean = false,
    val isBlocked: Boolean = false,
    val blockEndTime: Long? = null,
)