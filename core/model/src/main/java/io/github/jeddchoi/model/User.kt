package io.github.jeddchoi.model

import java.util.UUID

data class User(
    val emailAddress: String = "",
    val displayName: String = "Unknown",
    val id: String = UUID.randomUUID().toString(),
)