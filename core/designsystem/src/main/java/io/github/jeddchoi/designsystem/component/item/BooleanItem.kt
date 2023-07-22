package io.github.jeddchoi.designsystem.component.item

import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.jeddchoi.common.UiText

@Composable
fun BooleanItem(
    modifier: Modifier = Modifier,
    title: UiText = UiText.PlaceHolder,
    switchOn: Boolean = false,
) {
    ListItem(
        modifier = modifier,
        headlineContent = {
            Text(
                text = title.asString(),
                maxLines = 1,
            )
        },
        trailingContent = {
            Switch(
                checked = switchOn,
                onCheckedChange = {},
                enabled = false,
            )

        }
    )
}