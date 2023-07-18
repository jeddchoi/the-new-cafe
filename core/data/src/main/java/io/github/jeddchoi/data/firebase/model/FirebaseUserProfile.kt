package io.github.jeddchoi.data.firebase.model

import io.github.jeddchoi.model.Sex
import io.github.jeddchoi.model.UserProfile
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable


@Serializable
data class FirebasePrivateInfo(
    @field:JvmField val isAnonymous: Boolean? = null,
    val creationTime: Long? = null,
    val lastSignInTime: Long? = null,
    val emailVerified: Boolean? = null,
    val sex: Int? = null,
)

@Serializable
data class FirebaseUserProfile(
    val displayName: String? = null,
    @field:JvmField val isOnline: Boolean? = null,
    val profilePhotoUrl: String? = null,
    val emailAddress: String? = null,
    val privateInfo: FirebasePrivateInfo? = null,
)


fun FirebaseUserProfile.toUserProfile() = UserProfile(
    isAnonymous = privateInfo?.isAnonymous ?: false,
    displayName = displayName ?: "Not signed in",
    emailAddress = emailAddress ?: "not_signed_in@email.com",
    isOnline = isOnline ?: false,
    profilePhotoUrl = profilePhotoUrl,
    creationTime = privateInfo?.creationTime?.let { Instant.fromEpochMilliseconds(it) }
        ?: Instant.DISTANT_PAST,
    lastSignInTime = privateInfo?.lastSignInTime?.let { Instant.fromEpochMilliseconds(it) }
        ?: Instant.DISTANT_PAST,
    isEmailVerified = privateInfo?.emailVerified ?: false,
    sex = Sex.getByValue(privateInfo?.sex)
)