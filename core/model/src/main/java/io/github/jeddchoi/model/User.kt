package io.github.jeddchoi.model

import java.util.*

data class User(
    val emailAddress: String,
    val firstName: String,
    val lastName: String,
    val id: String = UUID.randomUUID().toString(),
)