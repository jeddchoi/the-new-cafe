package io.github.jeddchoi.authentication

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.jeddchoi.designsystem.TheNewCafeTheme
import io.github.jeddchoi.designsystem.component.GeneralTextField
import io.github.jeddchoi.designsystem.component.PasswordField
import io.github.jeddchoi.designsystem.component.UserInputScreen
import io.github.jeddchoi.ui.feature.LoadingScreen
import io.github.jeddchoi.ui.model.UiState


@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onBackClick: () -> Unit,
    navigateToMain: () -> Unit,
    navigateToSignInClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState) {
        UiState.EmptyResult -> {

        }
        is UiState.Error -> {

        }
        is UiState.InitialLoading -> {
            LoadingScreen()
        }
        is UiState.Success -> {
            val data = (uiState as UiState.Success<AuthScreenData>).data
            UserInputScreen(
                title = stringResource(R.string.register),
                existBackStack = true,
                onBackClick = onBackClick,
                inputFields = { inputFieldsModifier ->

                    GeneralTextField(
                        value = data.firstName,
                        onValueChange = { viewModel.onFirstNameChange(it) },
                        placeholderMsg = stringResource(R.string.first_name),
                        isError = !data.isFirstNameValid,
                        errorMsg = stringResource(R.string.name_invalid),
                        modifier = inputFieldsModifier
                    )

                    GeneralTextField(
                        value = data.lastName,
                        onValueChange = { viewModel.onLastNameChange(it) },
                        placeholderMsg = stringResource(R.string.last_name),
                        isError = !data.isLastNameValid,
                        errorMsg = stringResource(R.string.name_invalid),
                        modifier = inputFieldsModifier
                    )

                    GeneralTextField(
                        value = data.email,
                        onValueChange = { viewModel.onEmailChange(it) },
                        placeholderMsg = stringResource(R.string.email),
                        isError = !data.isEmailValid,
                        errorMsg = stringResource(R.string.email_invalid_msg),
                        modifier = inputFieldsModifier
                    )

                    PasswordField(
                        value = data.password,
                        onValueChange = { viewModel.onPasswordChange(it, true) },
                        placeholderMsg = stringResource(R.string.password),
                        isError = !data.isPasswordValid,
                        errorMsg = stringResource(R.string.password_invalid_msg),
                        modifier = inputFieldsModifier
                    )

                    PasswordField(
                        value = data.confirmPassword,
                        onValueChange = { viewModel.onConfirmPasswordChange(it) },
                        placeholderMsg = stringResource(R.string.confirm_password),
                        isError = !data.doPasswordsMatch,
                        errorMsg = stringResource(R.string.password_isnt_same),
                        modifier = inputFieldsModifier
                    )
                },
                buttonText = stringResource(R.string.register),
                onPrimaryButtonClick = {
                    Log.i("SignInScreen", "email : ${data.email}, password : ${data.password}")
                    viewModel.onRegister()
                },
                canContinue = data.isValidInfoToRegister && data.canContinue && !data.isBusy,
                isBusy = data.isBusy,
                errorMsg = if (data.isValidInfoToRegister) null else stringResource(R.string.some_inputs_invalid_msg),
                userInfoComplete = data.registerInfoComplete,
                optionalTitle = stringResource(R.string.already_have_an_account),
                optionalButtonClick = navigateToSignInClick,
                optionalButtonText = stringResource(R.string.sign_in),
            )
            LaunchedEffect(data.isRegisterSuccessful) {
                if (data.isRegisterSuccessful) {
                    navigateToMain()
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    TheNewCafeTheme {
        RegisterScreen(viewModel = hiltViewModel(), onBackClick = {}, navigateToSignInClick = {}, navigateToMain = {})
    }
}