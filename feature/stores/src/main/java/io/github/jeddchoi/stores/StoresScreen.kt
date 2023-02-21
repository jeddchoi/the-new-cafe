package io.github.jeddchoi.stores

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
fun StoresRoute(
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,

    ) {
    val viewModel: StoresViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    StoresScreen(uiState = uiState)
}


@Composable
fun StoresScreen(
    uiState: UiState<StoresUiStateData>,
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
fun StoresScreenPreview() {
    TheNewCafeTheme {
        StoresScreen(
            UiState.Success(StoresUiStateData("Hello!")),
        )
    }
}