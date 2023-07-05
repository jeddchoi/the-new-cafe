package io.github.jeddchoi.authentication.register

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.jeddchoi.authentication.AuthViewModel
import io.github.jeddchoi.authentication.R
import io.github.jeddchoi.designsystem.TheNewCafeTheme
import io.github.jeddchoi.designsystem.component.GeneralTextField
import io.github.jeddchoi.designsystem.component.PasswordField
import io.github.jeddchoi.designsystem.component.UserInputScreen


@Composable
internal fun RegisterScreen(
    viewModel: AuthViewModel,
    onBackClick: () -> Unit,
    navigateToMain: () -> Unit,
    navigateToSignInClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    UserInputScreen(
        title = stringResource(R.string.register),
        inputFields = { inputFieldsModifier ->

            GeneralTextField(
                value = uiState.displayNameInput ?: "",
                onValueChange = { viewModel.onDisplayNameInputChange(it) },
                placeholderMsg = stringResource(R.string.display_name),
                isError = !uiState.isDisplayNameValid,
                errorMsg = stringResource(R.string.name_invalid),
                modifier = inputFieldsModifier
            )

            GeneralTextField(
                value = uiState.emailInput ?: "",
                onValueChange = { viewModel.onEmailInputChange(it) },
                placeholderMsg = stringResource(R.string.email),
                isError = !uiState.isEmailValid,
                errorMsg = stringResource(R.string.email_invalid_msg),
                modifier = inputFieldsModifier
            )

            PasswordField(
                value = uiState.passwordInput ?: "",
                onValueChange = { viewModel.onPasswordInputChange(it, true) },
                placeholderMsg = stringResource(R.string.password),
                isError = !uiState.isPasswordValid,
                errorMsg = stringResource(R.string.password_invalid_msg),
                modifier = inputFieldsModifier
            )

            PasswordField(
                value = uiState.confirmPasswordInput ?: "",
                onValueChange = { viewModel.onConfirmPasswordInputChange(it) },
                placeholderMsg = stringResource(R.string.confirm_password),
                isError = !uiState.doPasswordsMatch,
                errorMsg = stringResource(R.string.password_isnt_same),
                modifier = inputFieldsModifier
            )
        },
        buttonText = stringResource(R.string.register),
        isLoading = uiState.isLoading,
        onPrimaryButtonClick = viewModel::onRegister,
        existBackStack = true,
        onBackClick = onBackClick,
        primaryButtonEnabled = !uiState.isLoading && uiState.registerInfoComplete && uiState.isValidInfoToRegister,
        errorMsg = uiState.userMessage?.content,
        optionalTitle = stringResource(R.string.already_have_an_account),
        optionalButtonClick = navigateToSignInClick,
        optionalButtonText = stringResource(R.string.sign_in),
    )

    LaunchedEffect(uiState.isRegisterTaskCompleted) {
        if (uiState.isRegisterTaskCompleted) {
            navigateToMain()
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun RegisterScreenPreview() {
    TheNewCafeTheme {
        RegisterScreen(viewModel = hiltViewModel(), onBackClick = {}, navigateToSignInClick = {}, navigateToMain = {})
    }
}

