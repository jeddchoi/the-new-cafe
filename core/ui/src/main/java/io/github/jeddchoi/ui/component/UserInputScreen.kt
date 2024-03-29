package io.github.jeddchoi.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.jeddchoi.common.Message
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.designsystem.TheNewCafeTheme
import io.github.jeddchoi.designsystem.component.BackButton
import io.github.jeddchoi.designsystem.component.input.GeneralTextField
import io.github.jeddchoi.designsystem.component.input.PasswordField
import io.github.jeddchoi.designsystem.textColor
import io.github.jeddchoi.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInputScreen(
    title: UiText,
    inputFields: @Composable ColumnScope.(Modifier) -> Unit,
    bottomButtons: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    existBackStack: Boolean = false,
    clickBack: () -> Unit = {},
    optionalTitle: UiText? = null,
    clickOptionalButton: () -> Unit = {},
    optionalButtonText: UiText? = null,
    userMessage: Message? = null,
) {
    val isKeyboardOpen by keyboardAsState()

    val topAppBarScrollState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarScrollState)
    Scaffold (
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(text = title.asString(),)
                },
                navigationIcon = {
                    if (existBackStack) {
                        BackButton(onClick = clickBack)
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) { scaffoldPadding ->
        ComponentWithBottomButtons(
            modifier = Modifier
                .padding(scaffoldPadding)
                .imePadding()
                .fillMaxSize(),
            bottomButtons = bottomButtons,
            showGradientBackground = true,
            optionalContentOfButtonTop = {
                if (!isKeyboardOpen && optionalTitle != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = optionalTitle.asString())
                        Spacer(modifier = modifier.width(8.dp))
                        optionalButtonText?.asString()?.let {
                            TextButton(onClick = clickOptionalButton) {
                                Text(it, textDecoration = TextDecoration.Underline)
                            }
                        }
                    }
                }

                if (userMessage != null) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = userMessage.title.asString(),
                                color = userMessage.severity.textColor(),
                                textAlign = TextAlign.Start,
                                modifier = Modifier.weight(1f)
                            )

                            userMessage.action.forEach {
                                TextButton(onClick = it.action) {
                                    Text(
                                        text = it.title.asString(),
                                        textDecoration = TextDecoration.Underline
                                    )
                                }
                            }
                        }
                        userMessage.content?.asString()?.let {
                            Text(
                                text = it,
                                textAlign = TextAlign.Start,
                            )
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
            title = UiText.StringResource(R.string.sign_in),
            inputFields = {
                GeneralTextField(
                    value = email,
                    changeValue = { email = it },
                    labelText = "Email",
                    isError = !isEmailValid,
                    supportingText = UiText.DynamicString("Email is invalid")
                )

                PasswordField(
                    value = password,
                    changeValue = { password = it },
                    labelText = "Password",
                    supportingText = UiText.DynamicString("Password is invalid"),
                    isError = !isPasswordValid,
                )
            },
            bottomButtons = {},
            clickBack = { /*TODO*/ },
            userMessage = null
        )
    }
}


