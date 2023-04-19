package io.github.jeddchoi.model

import java.util.*

data class User(
    val emailAddress: String,
    val displayName: String,
    val id: String = UUID.randomUUID().toString(),
)