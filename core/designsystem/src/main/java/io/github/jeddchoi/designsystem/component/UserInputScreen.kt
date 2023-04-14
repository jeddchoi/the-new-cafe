package io.github.jeddchoi.designsystem.component

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.jeddchoi.data.util.AuthInputValidator
import io.github.jeddchoi.designsystem.TheNewCafeTheme

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun UserInputScreen(
    title: String,
    inputFields: @Composable ColumnScope.(Modifier) -> Unit,
    buttonText: String,
    isBusy: Boolean,
    onPrimaryButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    existBackStack: Boolean = false,
    onBackClick: () -> Unit = {},
    canContinue: Boolean = true,
    errorMsg: String? = null,
    userInfoComplete: Boolean = false,
    optionalTitle: String = "",
    optionalButtonClick: () -> Unit = {},
    optionalButtonText: String = "",
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize(),
    ) { scaffoldPadding ->

        Column(modifier = Modifier.padding(scaffoldPadding)) {
            LargeTopAppBar(
                title = {
                    Text(title)
                },
                navigationIcon = {
                    if (existBackStack) {
                        BackButton(onClick = {
                            onBackClick()
                        })
                    }
                },
            )

            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .imePadding()
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
//                    val bringIntoViewRequester = remember { BringIntoViewRequester() }
//                    val coroutineScope = rememberCoroutineScope()

                    inputFields(
                        Modifier
//                        .bringIntoViewRequester(bringIntoViewRequester)
//                        .onFocusChanged {
//                            if (it.isFocused) {
//                                coroutineScope.launch {
//                                    // This sends a request to all parents that asks them to scroll so
//                                    // that this item is brought into view.
//                                    bringIntoViewRequester.bringIntoView()
//                                }
//                            }
//                        }
//                        .focusTarget()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = optionalTitle)
                        Spacer(modifier = modifier.width(8.dp))
                        TextButton(onClick = optionalButtonClick) {
                            Text(optionalButtonText, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    if (errorMsg != null && userInfoComplete) {
                        Text(
                            text = errorMsg,
                            style = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.error)
                        )
                    }


                    PrimaryButton(
                        enabled = canContinue,
                        buttonText = buttonText,
                        onClick = {
                            onPrimaryButtonClick()
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        isBusy = isBusy
                    )
                }
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
            derivedStateOf { AuthInputValidator.EmailValidator.isValidEmail(email) }
        }
        val isPasswordValid by remember(password) {
            derivedStateOf { AuthInputValidator.PasswordValidator.isSecure(password) }
        }

        UserInputScreen(
            title = "Sign In",
            onBackClick = { /*TODO*/ },
            inputFields = {
                GeneralTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholderMsg = "Email",
                    isError = !isEmailValid,
                    errorMsg = "Email is invalid"
                )

                PasswordField(
                    value = password,
                    onValueChange = { password = it },
                    placeholderMsg = "Password",
                    isError = !isPasswordValid,
                    errorMsg = "Password is invalid",
                )
            },
            buttonText = "Done",
            onPrimaryButtonClick = {
                Log.i("SignInScreen", "email : $email, password : $password")
            },
            canContinue = !isEmailValid || !isPasswordValid,
            errorMsg = "Some input is invalid",
            userInfoComplete = email.isNotEmpty() && password.isNotEmpty(),
            isBusy = true
        )
    }
}