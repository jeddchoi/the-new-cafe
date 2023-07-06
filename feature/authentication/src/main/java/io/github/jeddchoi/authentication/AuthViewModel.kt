package io.github.jeddchoi.authentication

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.data.repository.AuthRepository
import io.github.jeddchoi.data.util.AuthInputValidator
import io.github.jeddchoi.data.util.getCurrentTime
import io.github.jeddchoi.ui.model.Action
import io.github.jeddchoi.ui.model.Message
import io.github.jeddchoi.ui.model.MessageSeverity
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


    fun checkEmailInput(email: String): Boolean {
        val (isEmailValid, supportingText) = authInputValidator.isValidEmail(email)
        _uiState.update {
            it.copy(
                emailInput = email,
                isEmailValid = isEmailValid,
                userMessage = null,
                emailSupportingText = supportingText
            )
        }
        return isEmailValid
    }

    fun checkDisplayNameInput(displayName: String): Boolean {
        val (isNameValid, supportingText) = authInputValidator.isNameValid(displayName)
        _uiState.update {
            it.copy(
                displayNameInput = displayName,
                isDisplayNameValid = isNameValid,
                userMessage = null,
                displayNameSupportingText = supportingText
            )
        }
        return isNameValid
    }


    fun checkPasswordInput(password: String, isRegister: Boolean = false): Boolean {
        val (isPasswordValid, supportingText) = authInputValidator.isPasswordValid(
            password,
            isRegister
        )

        _uiState.update {
            it.copy(
                passwordInput = password,
                isPasswordValid = isPasswordValid,
                userMessage = null,
                passwordSupportingText = supportingText,
            )
        }
        return isPasswordValid
    }

    fun checkConfirmPasswordInput(confirmPassword: String): Boolean {
        val (doMatch, supportingText) = authInputValidator.doPasswordsMatch(
            _uiState.value.passwordInput,
            confirmPassword
        )

        _uiState.update {
            it.copy(
                confirmPasswordInput = confirmPassword,
                doPasswordsMatch = doMatch,
                userMessage = null,
                confirmPasswordSupportingText = supportingText,
            )
        }
        return doMatch
    }

    fun onPasswordForgotClick() {
        // TODO: Implement this
    }

    fun onSignIn() {
        launchOneShotJob(job = {
            if (!checkEmailInput(uiState.value.emailInput)) return@launchOneShotJob
            if (!checkPasswordInput(uiState.value.passwordInput)) return@launchOneShotJob

            authRepository.signInWithEmail(uiState.value.emailInput, uiState.value.passwordInput)
                .onSuccess {
                    _uiState.update {
                        it.copy(isSignInTaskCompleted = true)
                    }
                }
        }, onError = { e, job ->
            _uiState.update {
                it.copy(
                    isSignInTaskCompleted = false,
                    userMessage = getErrorMessage(exceptionToUiText(e), job)
                )
            }
        })
    }

    fun onRegister() {
        launchOneShotJob(job = {
            if (!checkDisplayNameInput(uiState.value.displayNameInput)) return@launchOneShotJob
            if (!checkEmailInput(uiState.value.emailInput)) return@launchOneShotJob
            if (!checkPasswordInput(uiState.value.passwordInput, true)) return@launchOneShotJob
            if (!checkConfirmPasswordInput(uiState.value.confirmPasswordInput)) return@launchOneShotJob

            authRepository.registerWithEmail(
                uiState.value.emailInput,
                uiState.value.displayNameInput,
                uiState.value.passwordInput
            )
                .onSuccess {
                    _uiState.update {
                        it.copy(isRegisterTaskCompleted = true)
                    }
                }
        }, onError = { e, job ->
            _uiState.update {
                it.copy(
                    isRegisterTaskCompleted = false,
                    userMessage = getErrorMessage(exceptionToUiText(e), job)
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
        onError: (Throwable, suspend () -> Unit) -> Unit
    ) {
        _uiState.update {
            it.copy(isLoading = true, userMessage = null)
        }
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    job()
                }
            } catch (e: Throwable) {
                Log.e("Auth", e.stackTraceToString())
                onError(e, job)
            } finally {
                _uiState.update {
                    it.copy(isLoading = false)
                }
            }
        }

    }


    private fun exceptionToUiText(exception: Throwable) =
        UiText.DynamicString("[${getCurrentTime()}] ${exception.message ?: exception.stackTraceToString()}")

    private fun getErrorMessage(content: UiText, job: suspend () -> Unit): Message {
        return Message(
            title = UiText.StringResource(R.string.error),
            severity = MessageSeverity.ERROR,
            action = listOf(
                Action(UiText.StringResource(io.github.jeddchoi.ui.R.string.dismiss)) {
                    onUserMessageDismissed()
                }
            ),
            content = content
        )
    }
}


data class AuthUiState(
    val emailInput: String = "",
    val isEmailValid: Boolean = false,
    val emailSupportingText: UiText? = null,

    val displayNameInput: String = "",
    val isDisplayNameValid: Boolean = false,
    val displayNameSupportingText: UiText? = null,

    val passwordInput: String = "",
    val isPasswordValid: Boolean = false,
    val passwordSupportingText: UiText? = null,

    val confirmPasswordInput: String = "",
    val doPasswordsMatch: Boolean = false,
    val confirmPasswordSupportingText: UiText? = null,

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
    val confirmPasswordInputError: Boolean =
        !confirmPasswordInput.isNullOrEmpty() && !doPasswordsMatch
}