package io.github.jeddchoi.order.store

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.designsystem.TheNewCafeTheme
import io.github.jeddchoi.designsystem.component.card.ProgressCard
import io.github.jeddchoi.designsystem.component.card.TextCard
import io.github.jeddchoi.model.Store
import io.github.jeddchoi.order.R

@Composable
fun StoreInfoCard(
    store: Store,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
    ) {

        TextCard(
            modifier = Modifier.fillMaxWidth(),
            title = UiText.StringResource(R.string.uuid),
            content = UiText.DynamicString(store.uuid),
        )

        Row(
            modifier = Modifier.height(intrinsicSize = IntrinsicSize.Max)
        ) {
            TextCard(
                modifier = Modifier.fillMaxWidth().weight(1f).fillMaxHeight(),
                title = UiText.StringResource(R.string.total_sections),
                content = UiText.DynamicString(store.totalSections.toString()),
            )
            ProgressCard(
                modifier = Modifier.fillMaxWidth().weight(1f).fillMaxHeight(),
                title = UiText.StringResource(R.string.total_available_seats),
                content = UiText.DynamicString(store.seatsStat()),
                progress = store.totalAvailableSeats.toFloat().div(store.totalSeats)
            )
        }
    }
}

@Preview
@Composable
fun StoreInfoCardPreview() {
    TheNewCafeTheme {
        Surface {
            StoreInfoCard(
                store = Store(
                    id = "id",
                    acceptsReservation = true,
                    name = "Starbucks",
                    totalAvailableSeats = 10,
                    totalSeats = 24,
                    totalSections = 3,
                    uuid = LoremIpsum(10).values.joinToString(" ")
                )
            )
        }
    }
}