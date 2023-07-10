package io.github.jeddchoi.model

import kotlinx.datetime.Instant

data class UserProfile(
    val isAnonymous: Boolean = false,
    val displayName: String = "",
    val emailAddress: String = "",
    val isOnline: Boolean = false,
    val profilePhotoUrl: String? = null,
    val creationTime: Instant = Instant.DISTANT_PAST,
    val lastSignInTime: Instant = Instant.DISTANT_PAST,
    val isEmailVerified: Boolean = false,
    val sex: Sex? = null,
)
