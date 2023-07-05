package io.github.jeddchoi.designsystem.component

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.pinnedScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
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
    isLoading: Boolean,
    onPrimaryButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    existBackStack: Boolean = false,
    onBackClick: () -> Unit = {},
    primaryButtonEnabled: Boolean = true,
    errorMsg: String? = null,
    userInfoComplete: Boolean = false,
    optionalTitle: String = "",
    optionalButtonClick: () -> Unit = {},
    optionalButtonText: String = "",
) {
    val isKeyboardOpen by keyboardAsState()
    Scaffold(
        modifier = modifier
            .fillMaxSize(),
    ) { scaffoldPadding ->

        Column(modifier = Modifier.padding(scaffoldPadding)) {
            if (!isKeyboardOpen) {
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
            } else {
                TopAppBar(
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
                    scrollBehavior = pinnedScrollBehavior()
                )
            }


            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .imePadding()
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 112.dp),
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

                PrimaryButton(
                    enabled = primaryButtonEnabled,
                    buttonText = buttonText,
                    onClick = {
                        onPrimaryButtonClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    isLoading = isLoading,
                    message = errorMsg
                )
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
            primaryButtonEnabled = !isEmailValid || !isPasswordValid,
            errorMsg = "Some input is invalid",
            userInfoComplete = email.isNotEmpty() && password.isNotEmpty(),
            isLoading = true
        )
    }
}


@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}