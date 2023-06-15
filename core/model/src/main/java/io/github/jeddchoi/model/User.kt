package io.github.jeddchoi.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

data class User(
    val emailAddress: String = "",
    val displayName: String = "Unknown",
    val id: String = UUID.randomUUID().toString(),
)