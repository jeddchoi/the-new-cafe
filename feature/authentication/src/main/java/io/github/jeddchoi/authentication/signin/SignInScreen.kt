package io.github.jeddchoi.authentication.signin

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.jeddchoi.authentication.AuthScreenData
import io.github.jeddchoi.authentication.AuthViewModel
import io.github.jeddchoi.authentication.R
import io.github.jeddchoi.designsystem.TheNewCafeTheme
import io.github.jeddchoi.designsystem.component.*
import io.github.jeddchoi.ui.LogCompositions
import io.github.jeddchoi.ui.feature.LoadingScreen
import io.github.jeddchoi.ui.model.UiState
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SignInScreen(
    viewModel: AuthViewModel,
    onBackClick: () -> Unit,
    navigateToMain: () -> Unit,
    navigateToRegisterClick: () -> Unit,
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

            LogCompositions(tag = "SignIn", msg = data.toString())
            UserInputScreen(
                title = stringResource(R.string.sign_in),
                onBackClick = onBackClick,
                inputFields = { inputFieldsModifier ->
                    GeneralTextField(
                        value = data.email,
                        onValueChange = { viewModel.onEmailChange(it) },
                        placeholderMsg = stringResource(R.string.email),
                        isError = !data.isEmailValid,
                        errorMsg = stringResource(R.string.email_invalid_msg),
                        modifier = inputFieldsModifier
                    )

                    Column {
                        PasswordField(
                            value = data.password,
                            onValueChange = { viewModel.onPasswordChange(it) },
                            placeholderMsg = stringResource(R.string.password),
                            isError = !data.isPasswordValid,
                            errorMsg = stringResource(R.string.password_invalid_msg),
                            modifier = inputFieldsModifier
                        )

                        TextButton(
                            onClick = viewModel::onPasswordForgotClick,
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(stringResource(R.string.forgot_password))
                        }
                    }
                },
                buttonText = stringResource(R.string.sign_in),
                onPrimaryButtonClick = {
                    Log.i("SignInScreen", "email : ${data.email}, password : ${data.password}")
                    viewModel.onSignIn()
                },
                canContinue = data.isValidInfoToSignIn && data.canContinue && !data.isBusy,
                isBusy = data.isBusy,
                errorMsg = if (data.isValidInfoToSignIn) null else stringResource(R.string.some_inputs_invalid_msg),
                userInfoComplete = data.signInInfoComplete,
                optionalTitle = stringResource(R.string.new_user),
                optionalButtonClick = navigateToRegisterClick,
                optionalButtonText = stringResource(R.string.register),
            )

            LaunchedEffect(data.isSignInSuccessful) {
                if (data.isSignInSuccessful) {
                    navigateToMain()
                }
            }
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
            navigateToRegisterClick = {

            },
            navigateToMain = {}
        )
    }

}

