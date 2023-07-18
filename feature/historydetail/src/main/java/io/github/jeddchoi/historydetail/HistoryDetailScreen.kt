package io.github.jeddchoi.historydetail

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.model.UserStateChange
import io.github.jeddchoi.ui.component.ScreenWithTopAppBar
import io.github.jeddchoi.ui.fullscreen.EmptyResultScreen
import io.github.jeddchoi.ui.fullscreen.ErrorScreen
import io.github.jeddchoi.ui.fullscreen.LoadingScreen

@Composable
fun HistoryDetailScreen(
    uiState: HistoryDetailUiState,
    modifier: Modifier = Modifier,
    clickBack: () -> Unit = {},
) {
    when (uiState) {
        HistoryDetailUiState.Loading -> {
            LoadingScreen()
        }
        HistoryDetailUiState.NotFound -> {
            EmptyResultScreen(subject = UiText.StringResource(R.string.history_detail))
        }
        is HistoryDetailUiState.Success -> {
            ScreenWithTopAppBar(
                title = UiText.StringResource(R.string.history_detail),
                modifier = modifier,
                showNavigateUp = true,
                clickBack = clickBack,
            ) {
                LazyColumn(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize(),
                ) {
                    items(uiState.stateChanges) { stateChange ->
                        StateChangeCard(stateChange)
                    }
                    item {
                        Spacer(
                            modifier = Modifier.height(200.dp)
                        )
                    }
                }
            }
        }

        is HistoryDetailUiState.Error -> {
            ErrorScreen(exception = uiState.exception)
        }
    }

}

@Composable
private fun StateChangeCard(stateChange: UserStateChange) {

    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = if (stateChange.success) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.errorContainer
        ),
        headlineContent = {
            Text(
                text = stateChange.requestType.name
            )
        },
        supportingContent = {
            Row {
                Text(
                    text = stateChange.reason.name
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stateChange.timestamp.toString()
                )
            }
        },

        trailingContent = {
            Text(
                text = stateChange.resultUserState.name
            )
        }
    )
}