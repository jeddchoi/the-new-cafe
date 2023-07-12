package io.github.jeddchoi.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import io.github.jeddchoi.common.CafeIcons
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.designsystem.R
import io.github.jeddchoi.designsystem.TheNewCafeTheme

@Composable
fun ExpandableCard(
    title: UiText,
    modifier: Modifier = Modifier,
    expandedContent: @Composable () -> Unit = {}
) {

    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column {
            Row(
                modifier = Modifier
                    .clickable {
                        expanded = !expanded
                    }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.asString(),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) CafeIcons.ExpandLess else CafeIcons.ExpandMore,
                    contentDescription = stringResource(
                        if (expanded) R.string.collapse_desciption
                        else R.string.expand_description
                    )
                )
            }

            if (expanded) {
                expandedContent()
            }
        }
    }
}

@Preview
@Composable
fun ExpandableCardPreview() {
    TheNewCafeTheme {
        ExpandableCard(
            title = UiText.DynamicString("Expandable Card"),
            modifier = Modifier.fillMaxWidth()
        ) {
            SelectionContainer {
                Text(
                    text = LoremIpsum(40).values.joinToString(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}