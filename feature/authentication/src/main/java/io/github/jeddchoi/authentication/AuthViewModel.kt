package io.github.jeddchoi.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jeddchoi.common.Action
import io.github.jeddchoi.common.Message
import io.github.jeddchoi.common.OneShotFeedbackUiState
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.common.toErrorMessage
import io.github.jeddchoi.data.repository.AppFlagsRepository
import io.github.jeddchoi.data.repository.AuthRepository
import io.github.jeddchoi.data.util.AuthInputValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class AuthViewModel @Inject constructor(
    private val authInputValidator: AuthInputValidator,
    private val authRepository: AuthRepository,
    private val appFlagsRepository: AppFlagsRepository,
) : ViewModel() {

    private val _uiState: MutableStateFlow<AuthUiState> = MutableStateFlow(AuthUiState())

    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()


    fun checkEmailInput(email: String): Boolean {
        Timber.v("✅ $email")
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
        Timber.v("✅ $displayName")
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
        Timber.v("✅ $password, $isRegister")
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
        Timber.v("✅ $confirmPassword")
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

    fun forgotPassword() {
        Timber.v("✅")
        launchOneShotJob(job = {
            if (!checkEmailInput(uiState.value.emailInput)) return@launchOneShotJob
            authRepository.sendPasswordResetEmail(uiState.value.emailInput)
        }, onError = { e, job ->
            _uiState.update {
                it.copy(
                    userMessage = e.toErrorMessage(Action.Dismiss { dismissUserMessage() })
                )
            }
        })
    }

    fun signIn() {
        Timber.v("✅")
        launchOneShotJob(job = {
            if (!checkEmailInput(uiState.value.emailInput)) return@launchOneShotJob
            if (!checkPasswordInput(uiState.value.passwordInput)) return@launchOneShotJob

            authRepository.signInWithEmail(uiState.value.emailInput, uiState.value.passwordInput)
                .onSuccess {
                    _uiState.update {
                        it.copy(isSignInTaskCompleted = true)
                    }
                    appFlagsRepository.setShowMainScreenOnStart(true)
                }
        }, onError = { e, job ->
            _uiState.update {
                it.copy(
                    isSignInTaskCompleted = false,
                    userMessage = e.toErrorMessage(Action.Dismiss { dismissUserMessage() })
                )
            }
        })
    }

    fun signInLater() {
        Timber.v("✅")
        viewModelScope.launch {
            appFlagsRepository.setShowMainScreenOnStart(true)
        }
    }

    fun register() {
        Timber.v("✅")
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
                    appFlagsRepository.setShowMainScreenOnStart(true)
                }
        }, onError = { e, job ->
            _uiState.update {
                it.copy(
                    isRegisterTaskCompleted = false,
                    userMessage = e.toErrorMessage(Action.Dismiss { dismissUserMessage() })
                )
            }
        })
    }

    fun dismissUserMessage() {
        Timber.v("✅")
        _uiState.update {
            it.copy(userMessage = null)
        }
    }

    private fun launchOneShotJob(
        job: suspend () -> Unit,
        onError: (Throwable, suspend () -> Unit) -> Unit
    ) {
        Timber.v("✅")
        _uiState.update {
            it.copy(isLoading = true, userMessage = null)
        }
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    job()
                }
            } catch (e: Throwable) {
                Timber.e(e)
                onError(e, job)
            } finally {
                _uiState.update {
                    it.copy(isLoading = false)
                }
            }
        }

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

    val openPasswordForgotDialog: Boolean = false,
    override val isLoading: Boolean = false,
    override val userMessage: Message? = null
) : OneShotFeedbackUiState() {
    val signInInfoComplete = emailInput.isNotBlank() && passwordInput.isNotBlank()
    val isValidInfoToSignIn = isEmailValid && isPasswordValid

    val registerInfoComplete =
        emailInput.isNotBlank() && passwordInput.isNotBlank() && confirmPasswordInput.isNotBlank() && displayNameInput.isNotBlank()
    val isValidInfoToRegister =
        isEmailValid && isDisplayNameValid && isPasswordValid && doPasswordsMatch

    val emailInputError: Boolean = emailInput.isNotEmpty() && !isEmailValid
    val displayNameInputError: Boolean = displayNameInput.isNotEmpty() && !isDisplayNameValid
    val passwordInputError: Boolean = passwordInput.isNotEmpty() && !isPasswordValid
    val confirmPasswordInputError: Boolean =
        confirmPasswordInput.isNotEmpty() && !doPasswordsMatch
}