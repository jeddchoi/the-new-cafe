package io.github.jeddchoi.mypage.session

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.ui.feature.PlaceholderScreen

@Composable
internal fun SessionScreen(
    displayedUserSession: DisplayedUserSession,
    modifier: Modifier = Modifier
) {
    PlaceholderScreen(
        title = UiText.DynamicString(displayedUserSession.toString()),
        modifier = modifier.background(Color.Blue)
    )
}