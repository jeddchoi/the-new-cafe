package io.github.jeddchoi.actionlog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.jeddchoi.designsystem.TheNewCafeTheme
import io.github.jeddchoi.ui.model.UiState

@Composable
fun ActionLogRoute(
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,

    ) {
    val viewModel: ActionLogViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ActionLogScreen(uiState = uiState)
}


@Composable
internal fun ActionLogScreen(
    uiState: UiState<ActionLogUiStateData>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val text = when (uiState) {
            UiState.EmptyResult -> "EMPTY"
            is UiState.Error -> "ERROR : ${uiState.exception.message}"
            is UiState.InitialLoading -> "LOADING"
            is UiState.Success -> "OK 🎉 ${uiState.data.data}"
        }

        Text(
            text = text,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ActionLogScreenPreview() {
    TheNewCafeTheme {
        ActionLogScreen(
            UiState.Success(ActionLogUiStateData("Hello!")),
        )
    }
}