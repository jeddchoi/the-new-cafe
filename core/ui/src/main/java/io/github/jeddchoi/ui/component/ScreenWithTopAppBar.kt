package io.github.jeddchoi.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import io.github.jeddchoi.designsystem.TheNewCafeTheme
import io.github.jeddchoi.designsystem.component.BackButton
import io.github.jeddchoi.designsystem.component.input.GeneralTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenWithTopAppBar(
    title: String,
    showNavigateUp: Boolean,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit = {}
) {
    val topAppBarScrollState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarScrollState)
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (showNavigateUp) {
                        BackButton(onClick = {
                            onBackClick()
                        })
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { scaffoldPadding ->
        content(scaffoldPadding)
    }
}

@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}


@Preview
@Composable
fun ScreenWithTopAppBarPreview() {
    TheNewCafeTheme {
        ScreenWithTopAppBar(
            modifier = Modifier.fillMaxSize(),
            title = "Title",
            showNavigateUp = true,
            onBackClick = { /*TODO*/ }) {
            repeat(20) {
                GeneralTextField(
                    value = "Hello $it",
                    onValueChange = {},
                    labelText = "Placeholder",
                    isError = false,
                    supportingText = "",
                )
            }
        }
    }
}