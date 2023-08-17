package io.github.jeddchoi.designsystem.component.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.jeddchoi.common.CafeIcons
import io.github.jeddchoi.common.UiIcon
import io.github.jeddchoi.common.UiText

@Composable
fun EnumCard(
    modifier: Modifier = Modifier,
    currentIdx: Int? = 0,
    values: List<String> = emptyList(),
    title: UiText = UiText.PlaceHolder,
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .then(modifier),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title.asString(),
            )
            Spacer(
                modifier = Modifier.weight(1f)
            )
            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val disabledColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                values.forEachIndexed { index, text ->
                    if (currentIdx == index) {
                        Divider()
                        Row(
                            horizontalArrangement = Arrangement.Center
                        ) {
                            UiIcon.ImageVectorIcon(
                                imageVector = CafeIcons.ArrowRight
                            ).ToComposable()
                            Text(
                                text = text,
                            )
                            UiIcon.ImageVectorIcon(
                                imageVector = CafeIcons.ArrowLeft
                            ).ToComposable()
                        }
                        Divider()
                    } else {
                        Text(
                            text = text,
                            color = disabledColor,
                        )
                    }
                }
            }
        }
    }
}