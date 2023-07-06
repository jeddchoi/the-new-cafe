package io.github.jeddchoi.authentication.register

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.jeddchoi.authentication.AuthViewModel
import io.github.jeddchoi.authentication.R
import io.github.jeddchoi.designsystem.TheNewCafeTheme
import io.github.jeddchoi.designsystem.component.input.GeneralTextField
import io.github.jeddchoi.designsystem.component.input.PasswordField
import io.github.jeddchoi.ui.component.UserInputScreen


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
        modifier = modifier,
        title = io.github.jeddchoi.common.UiText.StringResource(R.string.register),
        inputFields = { inputFieldsModifier ->

            GeneralTextField(
                value = uiState.displayNameInput,
                onValueChange = { viewModel.checkDisplayNameInput(it) },
                labelText = stringResource(R.string.display_name),
                isError = uiState.displayNameInputError,
                supportingText = uiState.displayNameSupportingText,
                modifier = inputFieldsModifier.fillMaxWidth()
            )

            GeneralTextField(
                value = uiState.emailInput,
                onValueChange = { viewModel.checkEmailInput(it) },
                labelText = stringResource(R.string.email),
                isError = uiState.emailInputError,
                supportingText = uiState.emailSupportingText,
                modifier = inputFieldsModifier.fillMaxWidth(),
                keyboardType = KeyboardType.Email,
            )

            PasswordField(
                value = uiState.passwordInput,
                onValueChange = { viewModel.checkPasswordInput(it, true) },
                labelText = stringResource(R.string.password),
                supportingText = uiState.passwordSupportingText,
                isError = uiState.passwordInputError,
                modifier = inputFieldsModifier.fillMaxWidth()
            )

            PasswordField(
                value = uiState.confirmPasswordInput,
                onValueChange = { viewModel.checkConfirmPasswordInput(it) },
                labelText = stringResource(R.string.confirm_password),
                supportingText = uiState.confirmPasswordSupportingText,
                isError = uiState.confirmPasswordInputError,
                modifier = inputFieldsModifier.fillMaxWidth(),
                isLastButton = true,
                onKeyboardDoneAction = viewModel::onRegister
            )
        },
        buttonText = stringResource(R.string.register),
        isLoading = uiState.isLoading,
        onPrimaryButtonClick = viewModel::onRegister,
        existBackStack = true,
        onBackClick = onBackClick,
        primaryButtonEnabled = !uiState.isLoading && uiState.registerInfoComplete && uiState.isValidInfoToRegister,
        errorMsg = uiState.userMessage?.content?.asString(),
        onDismissErrorMsg = viewModel::onUserMessageDismissed,
        optionalTitle = stringResource(R.string.already_have_an_account),
        optionalButtonClick = {
            navigateToSignInClick()
            viewModel.onUserMessageDismissed()
        },
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
        RegisterScreen(
            viewModel = hiltViewModel(),
            onBackClick = {},
            navigateToSignInClick = {},
            navigateToMain = {})
    }
}

