package io.github.jeddchoi.authentication

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jeddchoi.data.repository.AuthRepository
import io.github.jeddchoi.data.util.AuthInputValidator
import io.github.jeddchoi.ui.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class AuthViewModel @Inject constructor(
    private val authInputValidator: AuthInputValidator,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<AuthScreenData> = MutableStateFlow(AuthScreenData())

    val uiState: StateFlow<UiState<AuthScreenData>> =
        _uiState.onStart { delay(4_000) }
            .asUiState(viewModelScope)


    fun onEmailChange(email: String) {

        val isEmailValid = authInputValidator.isValidEmail(email)
        _uiState.value = _uiState.value.copy(email = email, isEmailValid = isEmailValid)
    }

    fun onFirstNameChange(firstName: String) {
        val isNameValid = authInputValidator.isNameValid(firstName)
        _uiState.value = _uiState.value.copy(firstName = firstName, isFirstNameValid = isNameValid)
    }

    fun onLastNameChange(lastName: String) {
        val isNameValid = authInputValidator.isNameValid(lastName)
        _uiState.value = _uiState.value.copy(lastName = lastName, isLastNameValid = isNameValid)
    }

    fun onPasswordChange(password: String, isRegister: Boolean = false) {
        val isPasswordValid =
            if (isRegister) authInputValidator.isPasswordValid(password) else password.isNotBlank()
        _uiState.value = _uiState.value.copy(password = password, isPasswordValid = isPasswordValid)
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        val password = _uiState.value.password
        val doMatch = authInputValidator.doPasswordsMatch(password, confirmPassword)
        _uiState.value =
            _uiState.value.copy(confirmPassword = confirmPassword, doPasswordsMatch = doMatch)
    }

    fun onPasswordForgotClick() {

    }

    fun onSignIn() {
        val email = _uiState.value.email
        val password = _uiState.value.password

        launchOneShotJob {
            val result = authRepository.signInWithEmail(email, password)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(isSignInSuccessful = true)
            } else {
                _uiState.value =
                    _uiState.value.copy(isSignInSuccessful = false, canContinue = false)
            }
        }
    }

    fun onRegister() {
        val email = _uiState.value.email
        val firstName = _uiState.value.firstName
        val lastName = _uiState.value.lastName
        val password = _uiState.value.password

        launchOneShotJob {
            val result = authRepository.registerWithEmail(email, firstName, lastName, password)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(isRegisterSuccessful = true)
            } else {
                _uiState.value =
                    _uiState.value.copy(isRegisterSuccessful = false, canContinue = false)
            }
        }
    }


    private fun launchOneShotJob(
        job: suspend () -> Unit
    ) {
        _uiState.value = _uiState.value.copy(isBusy = true)
        viewModelScope.launch {
            try {
                job()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    canContinue = false,
                    messages = _uiState.value.messages.plus(
                        Message(
                            titleId = R.string.error,
                            content = e.message ?: e.stackTraceToString(),
                            severity = Severity.ERROR,
                            action = listOf(Action(R.string.retry) {
                                launchOneShotJob(job)
                            }),
                        )
                    )
                )
            } finally {
                _uiState.value = _uiState.value.copy(isBusy = false)
            }
        }
    }


    init {
        Log.i("Auth", System.identityHashCode(authRepository).toString() )
    }
}


internal data class AuthScreenData(
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isEmailValid: Boolean = false,
    val isFirstNameValid: Boolean = false,
    val isLastNameValid: Boolean = false,
    val isPasswordValid: Boolean = false,
    val doPasswordsMatch: Boolean = false,
    val isSignInSuccessful: Boolean = false,
    val isRegisterSuccessful: Boolean = false,
    override val isBusy: Boolean = false,
    override val canContinue: Boolean = true,
    override val messages: List<Message> = emptyList()
) : FeedbackState {

    val signInInfoComplete = email.isNotEmpty() && password.isNotEmpty()
    val isValidInfoToSignIn = isEmailValid && isPasswordValid

    val registerInfoComplete =
        email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && firstName.isNotEmpty() && lastName.isNotEmpty()
    val isValidInfoToRegister =
        isEmailValid && isFirstNameValid && isLastNameValid && isPasswordValid && doPasswordsMatch

    override fun copy(
        isBusy: Boolean,
        canContinue: Boolean,
        messages: List<Message>
    ): AuthScreenData = AuthScreenData(
        email,
        firstName,
        lastName,
        password,
        confirmPassword,
        isEmailValid,
        isFirstNameValid,
        isLastNameValid,
        isPasswordValid,
        doPasswordsMatch,
        isSignInSuccessful,
        isRegisterSuccessful,
        isBusy,
        canContinue,
        messages
    )
}