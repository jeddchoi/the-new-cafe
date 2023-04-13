package io.github.jeddchoi.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jeddchoi.data.repository.AuthRepository
import io.github.jeddchoi.data.util.AuthInputValidator
import io.github.jeddchoi.ui.feature.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authInputValidator: AuthInputValidator,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthScreenData())

    val uiState: StateFlow<UiState<AuthScreenData>> =
        _uiState
            .onStart { delay(4_000) }
            .map<AuthScreenData, UiState<AuthScreenData>> {
                UiState.Success(it)
            }.catch {
                emit(UiState.Error(it))
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                UiState.Loading()
            )


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
        viewModelScope.launch {
            val result = authRepository.signInWithEmail(_uiState.value.email, _uiState.value.password)
            if (result.isSuccess) {

            } else {

            }
        }

    }

    fun onRegister() {
        viewModelScope.launch {
            val result = authRepository.registerWithEmail(_uiState.value.email, _uiState.value.firstName, _uiState.value.lastName, _uiState.value.password)
            if (result.isSuccess) {

            } else {

            }
        }
    }
}


data class AuthScreenData(
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
    val isRegisterSuccessful: Boolean = false
) {
    val canSignIn = isEmailValid && isPasswordValid
    val canRegister =
        isEmailValid && isFirstNameValid && isLastNameValid && isPasswordValid && doPasswordsMatch

    val signInInfoComplete = email.isNotEmpty() && password.isNotEmpty()
    val registerInfoComplete = email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && firstName.isNotEmpty() && lastName.isNotEmpty()
}