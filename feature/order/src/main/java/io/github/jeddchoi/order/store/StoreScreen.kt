package io.github.jeddchoi.order.store

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.order.R
import io.github.jeddchoi.ui.component.ScreenWithTopAppBar
import io.github.jeddchoi.ui.feature.EmptyResultScreen
import io.github.jeddchoi.ui.feature.ErrorScreen
import io.github.jeddchoi.ui.feature.LoadingScreen

@Composable
internal fun StoreScreen(
    viewModel: StoreViewModel,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val result = uiState) {
        StoreUiState.Loading -> LoadingScreen(modifier = modifier)
        StoreUiState.NotFound -> EmptyResultScreen(subject = UiText.StringResource(R.string.store), modifier = modifier) // TODO: Not Found
        is StoreUiState.Success -> ScreenWithTopAppBar(
            title = UiText.DynamicString(result.store.name),
            showNavigateUp = true,
            onBackClick =  onBackClick,
            modifier = modifier
        ) {
            ScreenWithTopAppBar(title = UiText.DynamicString(result.store.name), modifier = modifier) {scaffoldPadding ->
                Column(modifier = Modifier.padding(scaffoldPadding)) {
                    Text(text = result.store.toString())
                    Text(text = result.sectionWithSeats.joinToString("\n"))
                }
            }
        }
        is StoreUiState.Error -> ErrorScreen(exception = result.exception, modifier = modifier)
    }
}