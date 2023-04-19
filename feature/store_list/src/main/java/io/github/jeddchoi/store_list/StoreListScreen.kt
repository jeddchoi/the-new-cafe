package io.github.jeddchoi.store_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import io.github.jeddchoi.designsystem.TheNewCafeTheme
import io.github.jeddchoi.ui.model.UiState


@Composable
internal fun StoreListScreen(
    uiState: UiState<StoreListUiStateData>,
    reserveSeat: () -> Unit = {},
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
            is UiState.Success -> "SUCCESS 🎉 ${uiState.data.data}"
        }

        Text(
            text = text,
            textAlign = TextAlign.Center
        )

        Button(onClick = reserveSeat) {
            Text("Reserve Seat")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StoreListScreenPreview() {
    TheNewCafeTheme {
        StoreListScreen(
            UiState.Success(StoreListUiStateData("Hello!")),
        )
    }
}