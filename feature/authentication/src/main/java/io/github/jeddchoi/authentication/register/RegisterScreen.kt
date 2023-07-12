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
import io.github.jeddchoi.authentication.AuthUiState
import io.github.jeddchoi.authentication.AuthViewModel
import io.github.jeddchoi.authentication.R
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.designsystem.TheNewCafeTheme
import io.github.jeddchoi.designsystem.component.BottomButton
import io.github.jeddchoi.designsystem.component.input.GeneralTextField
import io.github.jeddchoi.designsystem.component.input.PasswordField
import io.github.jeddchoi.ui.component.UserInputScreen


@Composable
internal fun RegisterScreen(
    uiState: AuthUiState,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    navigateToMain: () -> Unit = {},
    navigateToSignInClick: () -> Unit = {},
    changeDisplayNameInput: (String) -> Unit = {},
    changeEmailInput: (String) -> Unit = {},
    changePasswordInput: (String) -> Unit = {},
    changeConfirmPasswordInput: (String) -> Unit = {},
    register: () -> Unit = {},
    dismissUserMessage: () -> Unit = {},
) {


    UserInputScreen(
        title = UiText.StringResource(R.string.register),
        inputFields = { inputFieldsModifier ->

            GeneralTextField(
                value = uiState.displayNameInput,
                changeValue = changeDisplayNameInput,
                labelText = stringResource(R.string.display_name),
                isError = uiState.displayNameInputError,
                supportingText = uiState.displayNameSupportingText,
                modifier = inputFieldsModifier.fillMaxWidth()
            )

            GeneralTextField(
                value = uiState.emailInput,
                changeValue = changeEmailInput,
                labelText = stringResource(R.string.email),
                isError = uiState.emailInputError,
                supportingText = uiState.emailSupportingText,
                modifier = inputFieldsModifier.fillMaxWidth(),
                keyboardType = KeyboardType.Email,
            )

            PasswordField(
                value = uiState.passwordInput,
                changeValue = changePasswordInput,
                modifier = inputFieldsModifier.fillMaxWidth(),
                labelText = stringResource(R.string.password),
                supportingText = uiState.passwordSupportingText,
                isError = uiState.passwordInputError
            )

            PasswordField(
                value = uiState.confirmPasswordInput,
                changeValue = changeConfirmPasswordInput,
                modifier = inputFieldsModifier.fillMaxWidth(),
                labelText = stringResource(R.string.confirm_password),
                supportingText = uiState.confirmPasswordSupportingText,
                isError = uiState.confirmPasswordInputError,
                isLastButton = true,
                pressKeyboardDone = register
            )
        },
        bottomButtons = {
            val maxWidthModifier = Modifier.fillMaxWidth()
            BottomButton(
                text = UiText.StringResource(R.string.register),
                isLoading = uiState.isLoading,
                click = register,
                enabled = !uiState.isLoading && uiState.registerInfoComplete && uiState.isValidInfoToRegister,
                modifier = maxWidthModifier
            )
        },
        modifier = modifier,
        existBackStack = true,
        clickBack = onBackClick,
        optionalTitle = UiText.StringResource(R.string.already_have_an_account),
        clickOptionalButton = {
            navigateToSignInClick()
            dismissUserMessage()
        },
        optionalButtonText = UiText.StringResource(R.string.sign_in),
        userMessage = uiState.userMessage,
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
        val viewModel: AuthViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        RegisterScreen(
            uiState = uiState,
        )
    }
}

