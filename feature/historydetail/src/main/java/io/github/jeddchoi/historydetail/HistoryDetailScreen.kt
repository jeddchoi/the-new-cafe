package io.github.jeddchoi.historydetail

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.ui.fullscreen.PlaceholderScreen

@Composable
fun HistoryDetailScreen(
    sessionId: String,
    modifier: Modifier = Modifier,
    clickBack: () -> Unit = {},
) {
    PlaceholderScreen(title = UiText.DynamicString("History Detail : $sessionId"))
}