package io.github.jeddchoi.designsystem.component.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.jeddchoi.common.UiText

@Composable
fun TextCard(
    title: UiText,
    content: UiText,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = Modifier.padding(8.dp).then(modifier),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title.asString(),
                style = MaterialTheme.typography.labelMedium,
            )
            Spacer(
                modifier = Modifier.weight(1f)
            )
            Text(text = content.asString(), style = MaterialTheme.typography.titleMedium, overflow = TextOverflow.Ellipsis, maxLines = 2)
        }
    }
}