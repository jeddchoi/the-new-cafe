package io.github.jeddchoi.order.store_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.model.Store
import io.github.jeddchoi.order.R
import io.github.jeddchoi.ui.component.ScreenWithTopAppBar
import io.github.jeddchoi.ui.fullscreen.EmptyResultScreen
import io.github.jeddchoi.ui.fullscreen.ErrorScreen
import io.github.jeddchoi.ui.fullscreen.LoadingScreen

@Composable
internal fun StoreListScreen(
    uiState: StoreListUiState,
    modifier: Modifier = Modifier,
    navigateToStore: (String) -> Unit = {},
) {
    ScreenWithTopAppBar(
        title = UiText.StringResource(R.string.store_list_title),
        modifier = modifier,
    ) {scaffoldPadding ->

        val modifierWithPadding = Modifier.padding(scaffoldPadding)

        when (uiState) {
            StoreListUiState.Loading -> LoadingScreen(modifier = modifierWithPadding)
            StoreListUiState.EmptyList -> EmptyResultScreen(subject = UiText.StringResource(R.string.store), modifier = modifierWithPadding)
            is StoreListUiState.Success -> {
                LazyColumn(
                    modifier = modifierWithPadding.fillMaxSize()
                ) {
                    items(
                        uiState.stores,
                        key = { it.uuid },
                    ) { store ->
                        StoreListItem(store, onClick = navigateToStore)
                    }
                }
            }

            is StoreListUiState.Error -> ErrorScreen(exception = (uiState as StoreListUiState.Error).exception, modifier = modifierWithPadding)
        }
    }
}

@Composable
fun StoreListItem(
    store: Store,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit = {},
) {
    ListItem(
        headlineContent = {
            Text(text = store.name)
        },
        modifier = modifier.clickable(
            enabled = store.acceptsReservation,
            onClick = {
                onClick(store.id)
            }
        ),
        supportingContent = {
            Text(text = store.uuid)
        },
        trailingContent = {
            Text(text = store.seatsStat())
        }
    )
}
