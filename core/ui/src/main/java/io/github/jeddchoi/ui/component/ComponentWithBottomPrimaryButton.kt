package io.github.jeddchoi.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.jeddchoi.designsystem.TheNewCafeTheme
import io.github.jeddchoi.designsystem.component.PrimaryButton
import io.github.jeddchoi.designsystem.component.fadingEdge
import io.github.jeddchoi.designsystem.component.input.GeneralTextField


@Composable
fun ComponentWithBottomPrimaryButton(
    buttonEnabled: Boolean,
    buttonText: String,
    onButtonClick: () -> Unit,
    isLoading: Boolean,
    showGradientBackground: Boolean,
    modifier: Modifier = Modifier,
    optionalContentOfButtonTop: (@Composable ColumnScope.() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit = {},
) {

    Box(
        modifier = modifier
    ) {
        content()

        val maxWidthModifier = Modifier.fillMaxWidth()

        // primary button with gradient background
        Box(
            modifier = maxWidthModifier.align(Alignment.BottomCenter),
            contentAlignment = Alignment.BottomCenter
        ) {


            if (showGradientBackground) {
                Spacer(
                    modifier = Modifier
                        .matchParentSize()
                        .fadingEdge(
                            Brush.verticalGradient(
                                0f to Color.Transparent,
                                0.3f to MaterialTheme.colorScheme.background
                            )
                        )
                        .background(MaterialTheme.colorScheme.background)
                )
            }

            // primary button with optional content on top
            Column(
                modifier = maxWidthModifier.padding(bottom = 8.dp, start = 16.dp, end = 16.dp).heightIn(min = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
            ) {
                if (optionalContentOfButtonTop != null) {
                    optionalContentOfButtonTop()
                }

                PrimaryButton(
                    enabled = buttonEnabled,
                    buttonText = buttonText,
                    onClick = onButtonClick,
                    isLoading = isLoading,
                    modifier = maxWidthModifier
                        .padding(top = 8.dp),
                )
            }
        }
    }
}


@Preview(device = Devices.PHONE, showBackground = true, showSystemUi = true)
@Composable
fun ComponentWithPrimaryButtonPreview() {
    TheNewCafeTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { scaffoldPadding ->
            ComponentWithBottomPrimaryButton(
                buttonEnabled = true,
                buttonText = "Hello",
                onButtonClick = { /*TODO*/ },
                isLoading = false,
                showGradientBackground = true,
                modifier = Modifier
                    .padding(scaffoldPadding)
                    .imePadding()
                    .fillMaxSize(),
                optionalContentOfButtonTop = null,
//                {
//                    Text(
//                        text = LoremIpsum(5).values.joinToString(),
//                        style = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.error)
//                    )
//                    TextButton(onClick = {}) {
//                        Text("Do another option", textDecoration = TextDecoration.Underline)
//                    }
//                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 200.dp),
                ) {
                    repeat(20) {
                        GeneralTextField(
                            value = "Hello $it",
                            onValueChange = {},
                            labelText = "Placeholder",
                            isError = false,
                            supportingText = null,
                        )
                    }
                }
            }

        }
    }
}

