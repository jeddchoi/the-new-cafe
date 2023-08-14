package io.github.jeddchoi.order.store_list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
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
    ) { scaffoldPadding ->

        val modifierWithPadding = Modifier.padding(scaffoldPadding)

        when (uiState) {
            StoreListUiState.Loading -> LoadingScreen(modifier = modifierWithPadding)
            StoreListUiState.EmptyList -> EmptyResultScreen(
                subject = UiText.StringResource(R.string.store),
                modifier = modifierWithPadding
            )

            is StoreListUiState.Success -> {
                LazyColumn(
                    contentPadding = PaddingValues(8.dp),
                    modifier = modifierWithPadding.fillMaxSize(),
                ) {
                    items(
                        uiState.stores,
                        key = { it.uuid },
                    ) { store ->
                        StoreListItem(
                            store = store,
                            onClick = navigateToStore,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            is StoreListUiState.Error -> ErrorScreen(
                exception = uiState.exception,
                modifier = modifierWithPadding
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreListItem(
    store: Store,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit = {},
) {
    Card(
        modifier = modifier,
        onClick = {
            onClick(store.id)
        },
        enabled = store.acceptsReservation,
    ) {
        AsyncImage(
            model = store.photoUrl,
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier.height(200.dp)
        )
        Text(text = store.name)
        Text(text = store.uuid)
        Text(text = store.seatsStat())
    }
}
