package io.github.jeddchoi.data.repository

import io.github.jeddchoi.model.CurrentUser
import kotlinx.coroutines.flow.StateFlow

interface CurrentUserRepository {

    /**
     * Returns true if a user is currently signed in, false otherwise.
     */
    fun isUserSignedIn(): Boolean

    /**
     * Get current user id
     */
    fun getUserId(): String?

    /**
     * Returns the currently signed-in user, or null if no user is signed in.
     */
    val currentUser: StateFlow<CurrentUser?>

}