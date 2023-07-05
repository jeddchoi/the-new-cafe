package io.github.jeddchoi.authentication

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jeddchoi.data.repository.AuthRepository
import io.github.jeddchoi.data.util.AuthInputValidator
import io.github.jeddchoi.data.util.getCurrentTime
import io.github.jeddchoi.ui.model.Action
import io.github.jeddchoi.ui.model.Message
import io.github.jeddchoi.ui.model.Severity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
internal class AuthViewModel @Inject constructor(
    private val authInputValidator: AuthInputValidator,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<AuthUiState> = MutableStateFlow(AuthUiState())

    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()


    fun onEmailInputChange(email: String) {
        val isEmailValid = authInputValidator.isValidEmail(email)
        _uiState.update {
            it.copy(emailInput = email, isEmailValid = isEmailValid, userMessage = null)
        }
    }

    fun onDisplayNameInputChange(displayName: String) {
        val isNameValid = authInputValidator.isNameValid(displayName)
        _uiState.update {
            it.copy(
                displayNameInput = displayName,
                isDisplayNameValid = isNameValid,
                userMessage = null
            )
        }
    }


    fun onPasswordInputChange(password: String, isRegister: Boolean = false) {
        val isPasswordValid =
            if (isRegister) authInputValidator.isPasswordValid(password) else password.isNotBlank()
        _uiState.update {
            it.copy(passwordInput = password, isPasswordValid = isPasswordValid, userMessage = null)
        }
    }

    fun onConfirmPasswordInputChange(confirmPassword: String) {
        val doMatch =
            authInputValidator.doPasswordsMatch(_uiState.value.passwordInput, confirmPassword)

        _uiState.update {
            it.copy(
                confirmPasswordInput = confirmPassword,
                doPasswordsMatch = doMatch,
                userMessage = null
            )
        }
    }

    fun onPasswordForgotClick() {
        // TODO: Implement this
    }

    // TODO: check pre conditions
    fun onSignIn() {
        launchOneShotJob(job = {
            val email = uiState.value.emailInput ?: throw IllegalArgumentException("Email is empty")

            val password =
                uiState.value.passwordInput ?: throw IllegalArgumentException("Password is empty")
            authRepository.signInWithEmail(email, password)
                .onSuccess {
                    _uiState.update {
                        it.copy(isSignInTaskCompleted = true)
                    }
                }
        }, onError = { e, job ->
            _uiState.update {
                it.copy(
                    isSignInTaskCompleted = false,
                    userMessage = getErrorMessage(e, job)
                )
            }
        })
    }

    // TODO: check pre conditions
    fun onRegister() {
        launchOneShotJob(job = {
            val email = uiState.value.emailInput ?: throw IllegalArgumentException("Email is empty")
            val displayName =
                uiState.value.displayNameInput ?: throw IllegalArgumentException("Name is empty")
            val password =
                uiState.value.passwordInput ?: throw IllegalArgumentException("Password is empty")

            authRepository.registerWithEmail(email, displayName, password)
                .onSuccess {
                    _uiState.update {
                        it.copy(isRegisterTaskCompleted = true)
                    }
                }
        }, onError = { e, job ->
            _uiState.update {
                it.copy(
                    isRegisterTaskCompleted = false,
                    userMessage = getErrorMessage(e, job)
                )
            }
        })
    }

    fun onUserMessageDismissed() {
        _uiState.update {
            it.copy(userMessage = null)
        }
    }

    private fun launchOneShotJob(
        job: suspend () -> Unit,
        onError: (Exception, suspend () -> Unit) -> Unit
    ) {
        _uiState.update {
            it.copy(isLoading = true, userMessage = null)
        }
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    job()
                }
            } catch (e: Exception) {
                Log.e("Auth", e.stackTraceToString())
                onError(e, job)
            } finally {
                _uiState.update {
                    it.copy(isLoading = false)
                }
            }
        }

    }

    private fun getErrorMessage(exception: Throwable, job: suspend () -> Unit): Message {
        return Message(
            titleId = R.string.error,
            severity = Severity.ERROR,
            content = "[${getCurrentTime()}] ${exception.message ?: exception.stackTraceToString()}",
            action = listOf(
                Action(R.string.retry) {
                    launchOneShotJob(job) { e, job ->
                        _uiState.update {
                            it.copy(
                                userMessage = getErrorMessage(e, job)
                            )
                        }
                    }
                }
            )
        )
    }
}


data class AuthUiState(
    val emailInput: String? = null,
    val isEmailValid: Boolean = false,
    val displayNameInput: String? = null,
    val isDisplayNameValid: Boolean = false,
    val passwordInput: String? = null,
    val isPasswordValid: Boolean = false,
    val confirmPasswordInput: String? = null,
    val doPasswordsMatch: Boolean = false,
    val isSignInTaskCompleted: Boolean = false,
    val isRegisterTaskCompleted: Boolean = false,
    val isLoading: Boolean = false,
    val userMessage: Message? = null
) {
    val signInInfoComplete = !emailInput.isNullOrBlank() && !passwordInput.isNullOrBlank()
    val isValidInfoToSignIn = isEmailValid && isPasswordValid

    val registerInfoComplete =
        !emailInput.isNullOrBlank() && !passwordInput.isNullOrBlank() && !confirmPasswordInput.isNullOrBlank() && !displayNameInput.isNullOrBlank()
    val isValidInfoToRegister =
        isEmailValid && isDisplayNameValid && isPasswordValid && doPasswordsMatch

    val emailInputError: Boolean = !emailInput.isNullOrEmpty() && !isEmailValid
    val displayNameInputError: Boolean = !displayNameInput.isNullOrEmpty() && !isDisplayNameValid
    val passwordInputError: Boolean = !passwordInput.isNullOrEmpty() && !isPasswordValid
    val confirmPasswordInputError: Boolean = !confirmPasswordInput.isNullOrEmpty() && !doPasswordsMatch
}