package io.github.jeddchoi.order.store

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.order.R
import io.github.jeddchoi.order.store_list.StoreListUiState
import io.github.jeddchoi.ui.component.ScreenWithTopAppBar
import io.github.jeddchoi.ui.feature.EmptyResultScreen
import io.github.jeddchoi.ui.feature.ErrorScreen
import io.github.jeddchoi.ui.feature.LoadingScreen
import io.github.jeddchoi.ui.feature.PlaceholderScreen

@Composable
internal fun StoreScreen(
    viewModel: StoreViewModel,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val result = uiState) {
        StoreUiState.Loading -> LoadingScreen()
        StoreUiState.NotFound -> EmptyResultScreen(subject = UiText.StringResource(R.string.store)) // TODO: Not Found
        is StoreUiState.Success -> ScreenWithTopAppBar(
            title = UiText.DynamicString(result.store.name),
            showNavigateUp = true,
            onBackClick =  onBackClick
        ) {
            PlaceholderScreen(title = UiText.DynamicString(result.store.toString()), modifier = Modifier.padding(it))
        }

        is StoreUiState.Error -> ErrorScreen(exception = (uiState as StoreListUiState.Error).exception)
    }
}