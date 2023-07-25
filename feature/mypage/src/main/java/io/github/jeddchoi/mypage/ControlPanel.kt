package io.github.jeddchoi.mypage

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.commandiron.wheel_picker_compose.WheelDateTimePicker
import com.commandiron.wheel_picker_compose.core.TimeFormat
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.designsystem.TheNewCafeTheme
import io.github.jeddchoi.designsystem.component.BottomButton
import io.github.jeddchoi.designsystem.component.SegmentedControl
import io.github.jeddchoi.model.SeatFinderUserRequestType
import io.github.jeddchoi.model.SeatPosition
import io.github.jeddchoi.model.UserStateType
import io.github.jeddchoi.model.DisplayedUserSession
import io.github.jeddchoi.model.SessionTimer
import io.github.jeddchoi.ui.component.ComponentWithBottomButtons
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.time.Duration.Companion.seconds

@Composable
internal fun ColumnScope.ControlPanel(
    displayedUserSession: DisplayedUserSession,
    modifier: Modifier = Modifier,
    sendRequest: (SeatFinderUserRequestType, Int?, Long?) -> Unit = { _, _, _ -> },
    expandPartially: () -> Unit = {},
) {
    var selectedItemIdx by remember {
        mutableIntStateOf(0)
    }
    var duration: Int? by remember {
        mutableStateOf(null)
    }
    var endTime: Long? by remember {
        mutableStateOf(null)
    }

    EndTimeInput(
        modifier = Modifier.heightIn(min = 250.dp),
        selectedItemIdx = selectedItemIdx,
        selectItem = {
            selectedItemIdx = it
        },
        duration = duration,
        changeDuration = {
            duration = it
        },
        endTime = endTime,
        changeEndTime = {
            endTime = it
        },
    )

    if (selectedItemIdx == 0) {
        Text(
            text = "Duration : ${duration?.seconds}",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
    } else {
        Text(
            text = "EndTime : ${
                endTime?.let {
                    Instant.ofEpochSecond(it).atZone(ZoneId.systemDefault()).toLocalDateTime()
                }
            }",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
    }

    ControlPanelButtons(
        displayedUserSession = displayedUserSession,
        modifier = Modifier.fillMaxWidth(),
        clickButton = {
            sendRequest(
                it,
                if (selectedItemIdx == 0) duration else null,
                if (selectedItemIdx == 1) endTime?.times(1_000) else null
            )
            expandPartially()
        }
    )
}

@Composable
private fun EndTimeInput(
    modifier: Modifier = Modifier,
    selectedItemIdx: Int = 0,
    selectItem: (Int) -> Unit = {},
    duration: Int? = null,
    changeDuration: (Int?) -> Unit = {},
    endTime: Long? = null,
    changeEndTime: (Long?) -> Unit = {},
) {

    ComponentWithBottomButtons(
        bottomButtons = {
            BottomButton(
                modifier = Modifier.fillMaxWidth(),
                text = UiText.StringResource(R.string.reset),
                click = {
                    changeDuration(null)
                    changeEndTime(null)
                }
            )
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.padding(horizontal = 8.dp)
        ) {

            SegmentedControl(
                items = listOf(
                    UiText.StringResource(R.string.duration_in_seconds),
                    UiText.StringResource(R.string.end_time)
                ),
                modifier = Modifier.fillMaxWidth(),
                selectedItemIndex = selectedItemIdx,
                onItemSelection = selectItem
            )

            if (selectedItemIdx == 0) {

                Slider(
                    modifier = Modifier.semantics { contentDescription = "Localized Description" },
                    value = duration?.toFloat() ?: 50f,
                    onValueChange = {
                        changeDuration(it.toInt())
                    },
                    valueRange = 0f..100f,
                    steps = 19,
                )
            } else {

                WheelDateTimePicker(
                    startDateTime = endTime?.let {
                        Instant.ofEpochSecond(it).atZone(ZoneId.systemDefault()).toLocalDateTime()
                    }
                        ?: LocalDateTime.now(),
                    timeFormat = TimeFormat.HOUR_24,
                    rowCount = 5,
                ) { snappedDateTime ->
                    changeEndTime(
                        snappedDateTime.atZone(ZoneId.systemDefault()).toEpochSecond()
                    )
                }
            }
        }
    }
}


@Composable
private fun ControlPanelButtons(
    displayedUserSession: DisplayedUserSession,
    modifier: Modifier = Modifier,
    clickButton: (SeatFinderUserRequestType) -> Unit = {},
) {

    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(8.dp),
    ) {
        items(
            items = SeatFinderUserRequestType.RequestTypesInSession,
        ) {
            Button(
                modifier = Modifier.padding(8.dp),
                onClick = {
                    clickButton(it)
                },
                enabled = displayedUserSession.canDo(it)
            ) {
                Text(
                    text = it.title(),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            }
        }

    }
}

@Composable
private fun SeatFinderUserRequestType.title(): String {
    return stringResource(
        id = when (this) {
            SeatFinderUserRequestType.Reserve -> io.github.jeddchoi.data.R.string.reserve
            SeatFinderUserRequestType.Occupy -> io.github.jeddchoi.data.R.string.occupy
            SeatFinderUserRequestType.Quit -> io.github.jeddchoi.data.R.string.quit
            SeatFinderUserRequestType.DoBusiness -> io.github.jeddchoi.data.R.string.do_business
            SeatFinderUserRequestType.LeaveAway -> io.github.jeddchoi.data.R.string.leave_away
            SeatFinderUserRequestType.ResumeUsing -> io.github.jeddchoi.data.R.string.resume_using
            SeatFinderUserRequestType.ChangeReservationEndTime -> io.github.jeddchoi.data.R.string.change_reservation_end_time
            SeatFinderUserRequestType.ChangeOccupyEndTime -> io.github.jeddchoi.data.R.string.change_occupy_end_time
            SeatFinderUserRequestType.ChangeBusinessEndTime -> io.github.jeddchoi.data.R.string.change_business_end_time
            SeatFinderUserRequestType.ChangeAwayEndTime -> io.github.jeddchoi.data.R.string.change_away_end_time
        }
    )
}


@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun ControlPanelPreview() {

    TheNewCafeTheme {
        Surface() {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                ControlPanel(
                    DisplayedUserSession.UsingSeat(
                        SessionTimer(),
                        SessionTimer(),
                        hasFailure = false,
                        seatPosition = SeatPosition(),
                        null,
                        state = UserStateType.Reserved
                    )
                )
            }
        }
    }
}
