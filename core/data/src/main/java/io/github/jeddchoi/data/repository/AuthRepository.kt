package io.github.jeddchoi.data.repository

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
     * @param displayName The user's first name.
     * @param password The user's password.
     * @return A [Result] object indicating success or failure of the signup attempt.
     */
    suspend fun registerWithEmail(email: String, displayName: String, password: String): Result<Unit>

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
     * Sends a password reset email to the given email address.
     *
     * @param email The email address to send the password reset email to.
     * @return A [Result] object indicating success or failure of the password reset request.
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
}