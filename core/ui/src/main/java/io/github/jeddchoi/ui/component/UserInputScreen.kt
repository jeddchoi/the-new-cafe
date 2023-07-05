package io.github.jeddchoi.ui.component

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.jeddchoi.designsystem.R
import io.github.jeddchoi.designsystem.TheNewCafeTheme
import io.github.jeddchoi.designsystem.component.input.GeneralTextField
import io.github.jeddchoi.designsystem.component.input.PasswordField
import kotlinx.coroutines.CoroutineScope

@Composable
fun UserInputScreen(
    title: String,
    inputFields: @Composable ColumnScope.(Modifier) -> Unit,
    buttonText: String,
    isLoading: Boolean,
    onPrimaryButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    existBackStack: Boolean = false,
    onBackClick: () -> Unit = {},
    primaryButtonEnabled: Boolean = true,
    errorMsg: String? = null,
    onDismissErrorMsg: () -> Unit = {},
    optionalTitle: String = "",
    optionalButtonClick: () -> Unit = {},
    optionalButtonText: String = "",
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) {
    val isKeyboardOpen by keyboardAsState()

    ScreenWithTopAppBar(
        title = title,
        showNavigateUp = existBackStack,
        onBackClick = onBackClick,
        modifier = modifier,
    ) { scaffoldPadding ->
        ComponentWithBottomPrimaryButton(
            buttonEnabled = primaryButtonEnabled,
            buttonText = buttonText,
            onButtonClick = onPrimaryButtonClick,
            isLoading = isLoading,
            showGradientBackground = true,
            modifier = Modifier
                .padding(scaffoldPadding)
                .imePadding()
                .fillMaxSize(),
            optionalContentOfButtonTop = {
                if (!isKeyboardOpen) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = optionalTitle)
                        Spacer(modifier = modifier.width(8.dp))
                        TextButton(onClick = optionalButtonClick) {
                            Text(optionalButtonText, textDecoration = TextDecoration.Underline)
                        }
                    }
                }

                if (errorMsg != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = errorMsg,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .weight(1f)
                        )

                        TextButton(onClick = onDismissErrorMsg) {
                            Text(stringResource(R.string.dismiss), textDecoration = TextDecoration.Underline)
                        }
                    }
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .align(Alignment.TopCenter)
                    .padding(start = 16.dp, end = 16.dp, bottom = 200.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                inputFields(Modifier)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UserInputOneByOneScreenPreview() {
    TheNewCafeTheme {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        val isEmailValid by remember(email) {
            mutableStateOf(true)
        }
        val isPasswordValid by remember(password) {
            mutableStateOf(true)
        }

        UserInputScreen(
            title = "Sign In",
            inputFields = {
                GeneralTextField(
                    value = email,
                    onValueChange = { email = it },
                    labelText = "Email",
                    isError = !isEmailValid,
                    supportingText = "Email is invalid"
                )

                PasswordField(
                    value = password,
                    onValueChange = { password = it },
                    labelText = "Password",
                    supportingText = "Password is invalid",
                    isError = !isPasswordValid,
                )
            },
            buttonText = "Done",
            isLoading = true,
            onPrimaryButtonClick = {
                Log.i("SignInScreen", "email : $email, password : $password")
            },
            onBackClick = { /*TODO*/ },
            primaryButtonEnabled = !isEmailValid || !isPasswordValid,
            errorMsg = "Some input is invalid"
        )
    }
}


