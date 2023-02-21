package io.github.jeddchoi.store

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
import io.github.jeddchoi.designsystem.ui.theme.TheNewCafeTheme
import io.github.jeddchoi.ui.UiState

@Composable
fun StoreRoute(
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,

    ) {
    val viewModel: StoreViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    StoreScreen(uiState = uiState)
}


@Composable
fun StoreScreen(
    uiState: UiState<SeatsUiStateData>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val text = when (uiState) {
            UiState.Empty -> "EMPTY"
            is UiState.Error -> "ERROR : ${uiState.exception.message}"
            is UiState.Loading -> "LOADING ${uiState.data?.data}"
            is UiState.Success -> "SUCCESS 🎉 ${uiState.data.data}"
        }

        Text(
            text = text,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StoreScreenPreview() {
    TheNewCafeTheme {
        StoreScreen(
            UiState.Success(SeatsUiStateData("Hello!")),
        )
    }
}