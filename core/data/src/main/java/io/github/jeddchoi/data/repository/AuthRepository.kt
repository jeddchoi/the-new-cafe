package io.github.jeddchoi.data.repository

import io.github.jeddchoi.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    /**
     * Attempts to log the user in with the given email and password.
     *
     * @param email The user's email.
     * @param password The user's password.
     * @return A [Result] object indicating success or failure of the login attempt.
     */
    suspend fun signInWithEmail(email: String, password: String): Result<Unit>

    /**
     * Attempts to register the user with the given email and password.
     *
     * @param email The user's email.
     * @param firstName The user's first name.
     * @param lastName The user's last name.
     * @param password The user's password.
     * @return A [Result] object indicating success or failure of the signup attempt.
     */
    suspend fun registerWithEmail(email: String, firstName: String, lastName:String, password: String): Result<Unit>

    /**
     * Attempts to sign the user in with Google Sign-In.
     *
     * @param idToken The ID token received from Google Sign-In.
     * @return A [Result] object indicating success or failure of the login attempt.
     */
    suspend fun signInWithGoogle(idToken: String): Result<Unit>


    /**
     * Logs the current user out.
     */
    suspend fun logout()

    /**
     * Returns true if a user is currently signed in, false otherwise.
     */
    fun isUserSignedIn(): Boolean

    /**
     * Returns the currently signed-in user, or null if no user is signed in.
     */
    fun getCurrentUser(): Flow<User?>

    /**
     * Sends a password reset email to the given email address.
     *
     * @param email The email address to send the password reset email to.
     * @return A [Result] object indicating success or failure of the password reset request.
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
}