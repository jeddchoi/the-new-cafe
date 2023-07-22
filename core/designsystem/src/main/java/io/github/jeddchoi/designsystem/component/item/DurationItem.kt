package io.github.jeddchoi.designsystem.component.item

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.jeddchoi.common.UiText
import kotlin.time.Duration

@Composable
fun DurationItem(
    modifier: Modifier = Modifier,
    title: UiText = UiText.PlaceHolder,
    duration: Duration?,
    progress: Float? = null,
) {
    Column {
        ListItem(
            modifier = modifier,
            headlineContent = {
                Text(
                    text = duration?.toString() ?: "-",
                    maxLines = 2,
                )
            },
            supportingContent = {
                Text(
                    text = title.asString(),
                    maxLines = 1,
                )
            }
        )
        if (progress != null) {
            LinearProgressIndicator(progress = progress, modifier = modifier)
        }
    }
}