package io.github.jeddchoi.order.store

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.jeddchoi.common.CafeIcons
import io.github.jeddchoi.common.UiIcon
import io.github.jeddchoi.common.getLocalDateTimeWithMyTimeZone
import io.github.jeddchoi.model.Seat

@Composable
fun SeatItem(
    seat: Seat,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = modifier
            .fillMaxWidth(),
        leadingContent = {
            Text(text = seat.minor)
        },
        headlineContent = {
            Text(text = seat.name)
        },
        supportingContent = {
            Column {
                Text(text = seat.id)
                Text(text = seat.macAddress)
                seat.userId?.let { Text(text = "UserId : $it") }
                seat.reserveEndTime?.let {
                    Text(
                        text = "Reservation End At : ${getLocalDateTimeWithMyTimeZone(it)}"
                    )
                }
                seat.occupyEndTime?.let { Text(text = "Occupation End At : ${getLocalDateTimeWithMyTimeZone(it)}") }
            }
        },
        overlineContent = {
            Text(text = seat.state.name)
        },
        trailingContent = {
            if (isSelected) {
                UiIcon.ImageVectorIcon(CafeIcons.CheckCircle).ToComposable()
            }
        },
        tonalElevation = if (isSelected) 4.dp else 0.dp,
        colors = if (seat.isAvailable) ListItemDefaults.colors()
        else {
            val disabledColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            ListItemDefaults.colors(
                headlineColor = disabledColor,
                supportingColor = disabledColor,
                trailingIconColor = disabledColor,
                leadingIconColor = disabledColor,
                overlineColor = disabledColor,
            )
        },
    )
}