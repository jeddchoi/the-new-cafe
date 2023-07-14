package io.github.jeddchoi.mypage.session

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun SessionScreen(
    displayedUserSession: DisplayedUserSession,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var innerProgress = 1f
        var outerProgress = 1f
        when (displayedUserSession) {
            DisplayedUserSession.None -> {
                // do nothing
            }

            is DisplayedUserSession.UsingSeat -> {
                innerProgress = displayedUserSession.currentStateTimer.progress(true) ?: 1f
                outerProgress = displayedUserSession.sessionTimer.progress(true) ?: 1f
            }
        }
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = innerProgress,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.then(Modifier.size(80.dp))
            )
            CircularProgressIndicator(
                progress = outerProgress,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.then(Modifier.size(100.dp))
            )
        }

        Text(text = displayedUserSession.toString())
    }
//    PlaceholderScreen(
//        title = UiText.DynamicString(displayedUserSession.toString()),
//        modifier = modifier
//    )
}