package io.github.jeddchoi.designsystem.component.item

import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.jeddchoi.common.CafeIcons
import io.github.jeddchoi.common.UiText
import kotlinx.datetime.Instant

@Composable
fun DateTimeItem(
    modifier: Modifier = Modifier,
    title: UiText = UiText.PlaceHolder,
    date: Instant?,
) {
    ListItem(
        modifier = modifier,
        leadingContent = {
            Icon(imageVector = CafeIcons.Schedule, contentDescription = null)
        },
        headlineContent = {
            Text(
                text = date?.toString() ?: "-",
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

}