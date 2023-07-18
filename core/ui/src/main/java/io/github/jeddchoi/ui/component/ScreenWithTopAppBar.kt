package io.github.jeddchoi.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.designsystem.TheNewCafeTheme
import io.github.jeddchoi.designsystem.component.BackButton
import io.github.jeddchoi.designsystem.component.input.GeneralTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenWithTopAppBar(
    title: UiText,
    modifier: Modifier = Modifier,
    showNavigateUp: Boolean = false,
    clickBack: () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit = {}
) {
    val topAppBarScrollState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarScrollState)
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(title.asString()) },
                navigationIcon = {
                    if (showNavigateUp) {
                        BackButton(onClick = clickBack)
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) { scaffoldPadding ->
        content(scaffoldPadding)
    }
}


@Preview
@Composable
private fun ScreenWithTopAppBarPreview() {
    TheNewCafeTheme {
        ScreenWithTopAppBar(
            title = UiText.DynamicString("Title"),
            modifier = Modifier.fillMaxSize(),
            showNavigateUp = true,
            clickBack = { /*TODO*/ }) {
            repeat(20) {
                GeneralTextField(
                    value = "Hello $it",
                    changeValue = {},
                    labelText = "Placeholder",
                    isError = false,
                    supportingText = null,
                )
            }
        }
    }
}