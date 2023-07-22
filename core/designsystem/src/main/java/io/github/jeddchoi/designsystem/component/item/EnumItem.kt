package io.github.jeddchoi.designsystem.component.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.jeddchoi.common.CafeIcons
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.designsystem.TheNewCafeTheme

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EnumItem(
    modifier: Modifier = Modifier,
    currentIdx: Int? = 0,
    values: List<String> = emptyList(),
    title: UiText = UiText.PlaceHolder,
) {

    ListItem(
        modifier = modifier,
        headlineContent = {
            if (currentIdx == null) {
                Text(
                    text = "-",
                )
            } else {
                FlowRow(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    values.forEachIndexed { index, item ->
                        FilterChip(
                            onClick = {},
                            selected = currentIdx == index,
                            leadingIcon = {
                                if (currentIdx == index) {
                                    Icon(
                                        imageVector = CafeIcons.Check,
                                        contentDescription = null
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = item,
                                )

                            }
                        )
                    }
                }
            }
        },
        supportingContent = {
            Text(
                text = title.asString()
            )
        },
    )
}


@Preview
@Composable
fun EnumItemPreview() {
    TheNewCafeTheme {
        EnumItem(
            modifier = Modifier.fillMaxWidth(),
            currentIdx = 0,
            values = listOf(
                "Item 1",
                "Item 2",
                "Item 3",
                "Item 4",
                "Item 5",
                "Item 6",
            ),
            title = UiText.PlaceHolder
        )
    }
}