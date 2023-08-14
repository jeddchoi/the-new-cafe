package io.github.jeddchoi.order.store

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.designsystem.component.card.ProgressCard
import io.github.jeddchoi.designsystem.component.card.TextCard
import io.github.jeddchoi.model.Section
import io.github.jeddchoi.order.R

@Composable
fun SectionInfoCard(section: Section) {
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
    ) {

        Text(
            modifier = Modifier.padding(8.dp),
            text = section.name,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )

        Row(
            modifier = Modifier.height(intrinsicSize = IntrinsicSize.Max)
        ) {
            TextCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .fillMaxHeight(),
                title = UiText.StringResource(R.string.major),
                content = UiText.DynamicString(section.major),
            )
            ProgressCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .fillMaxHeight(),
                title = UiText.StringResource(R.string.total_available_seats),
                content = UiText.DynamicString(section.seatsStat()),
                progress = section.totalAvailableSeats.toFloat().div(section.totalSeats)
            )
        }
    }

}