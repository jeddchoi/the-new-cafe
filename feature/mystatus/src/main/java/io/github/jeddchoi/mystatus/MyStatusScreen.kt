package io.github.jeddchoi.mystatus

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
import io.github.jeddchoi.ui.feature.UiState

@Composable
fun MyStatusRoute(
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,

    ) {
    val viewModel: MyStatusViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MyStatusScreen(uiState = uiState)
}


@Composable
fun MyStatusScreen(
    uiState: UiState<MyStatusUiStateData>,
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
fun MyStatusScreenPreview() {
    TheNewCafeTheme {
        MyStatusScreen(
            UiState.Success(MyStatusUiStateData("Hello!")),
        )
    }
}