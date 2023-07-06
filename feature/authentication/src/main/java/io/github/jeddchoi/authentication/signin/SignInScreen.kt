package io.github.jeddchoi.authentication.signin

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.jeddchoi.authentication.AuthViewModel
import io.github.jeddchoi.authentication.R
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.designsystem.TheNewCafeTheme
import io.github.jeddchoi.designsystem.component.BottomButton
import io.github.jeddchoi.designsystem.component.BottomButtonType
import io.github.jeddchoi.designsystem.component.input.GeneralTextField
import io.github.jeddchoi.designsystem.component.input.PasswordField
import io.github.jeddchoi.ui.component.UserInputScreen

@Composable
internal fun SignInScreen(
    viewModel: AuthViewModel,
    onBackClick: () -> Unit,
    navigateToMain: () -> Unit,
    navigateToRegister: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()


    UserInputScreen(
        title = UiText.StringResource(R.string.sign_in),
        inputFields = { inputFieldsModifier ->
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
                onValueChange = { viewModel.checkPasswordInput(it) },
                labelText = stringResource(R.string.password),
                supportingText = uiState.passwordSupportingText,
                isError = uiState.passwordInputError,
                modifier = inputFieldsModifier.fillMaxWidth(),
                isLastButton = true,
                onKeyboardDoneAction = viewModel::onSignIn
            )

            TextButton(
                onClick = viewModel::onPasswordForgotClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(stringResource(R.string.forgot_password))
            }
        },
        bottomButtons = {
            val maxWidthModifier = Modifier.fillMaxWidth().weight(1f)
            BottomButton(
                enabled = !uiState.isLoading,
                text = UiText.StringResource(R.string.sign_in_later),
                onClick = navigateToMain,
                isLoading = false,
                type = BottomButtonType.FilledTonal,
                modifier = maxWidthModifier,
            )
            BottomButton(
                enabled = !uiState.isLoading && uiState.signInInfoComplete && uiState.isValidInfoToSignIn,
                text = UiText.StringResource(R.string.sign_in),
                onClick = viewModel::onSignIn,
                isLoading = uiState.isLoading,
                type = BottomButtonType.Filled,
                modifier = maxWidthModifier,
            )
        },
        modifier = modifier,
        onBackClick = onBackClick,
        optionalTitle = UiText.StringResource(R.string.new_user),
        optionalButtonClick = {
            navigateToRegister()
            viewModel.onUserMessageDismissed()
        },
        optionalButtonText = UiText.StringResource(R.string.register),
        userMessage = uiState.userMessage,
    )

    LaunchedEffect(uiState.isSignInTaskCompleted) {
        if (uiState.isSignInTaskCompleted) {
            navigateToMain()
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun SignInScreenPreview() {
    TheNewCafeTheme {
        SignInScreen(
            viewModel = hiltViewModel(),
            onBackClick = { },
            navigateToRegister = {

            },
            navigateToMain = {}
        )
    }

}

