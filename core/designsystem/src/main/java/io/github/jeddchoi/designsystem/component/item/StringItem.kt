package io.github.jeddchoi.designsystem.component.item

import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.jeddchoi.common.UiText

@Composable
fun StringItem(
    modifier: Modifier = Modifier,
    title: UiText = UiText.PlaceHolder,
    content: UiText? = UiText.PlaceHolder,
) {
    ListItem(
        modifier = modifier,
        headlineContent = {
            Text(
                text = content?.asString() ?: "-",
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