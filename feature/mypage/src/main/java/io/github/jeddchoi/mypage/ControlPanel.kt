package io.github.jeddchoi.mypage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.commandiron.wheel_picker_compose.WheelDateTimePicker
import com.commandiron.wheel_picker_compose.core.TimeFormat
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.data.service.seatfinder.SeatFinderUserRequestType
import io.github.jeddchoi.designsystem.component.BottomButton
import io.github.jeddchoi.designsystem.component.SegmentedControl
import io.github.jeddchoi.mypage.session.DisplayedUserSession
import io.github.jeddchoi.ui.component.ComponentWithBottomButtons
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Composable
internal fun ColumnScope.ControlPanel(
    displayedUserSession: DisplayedUserSession,
    modifier: Modifier = Modifier,
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
        modifier = modifier,
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

//    ControlPanelButtons(
//        endTime = endTime,
//        expandPartially = expandPartially,
//    )
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
                    startDateTime = endTime?.let { Instant.ofEpochSecond(it).atZone(ZoneId.systemDefault()).toLocalDateTime() }
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
    modifier: Modifier = Modifier,
    duration: Int? = null,
    endTime: Long? = null,
    clickButton: (SeatFinderUserRequestType) -> Unit = {},
) {

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SeatFinderUserRequestType.VALUES.forEach {

        }

    }


//    LazyVerticalGrid(
//        columns = GridCells.Fixed(2),
//        contentPadding = PaddingValues(8.dp),
//    ) {
//        items(controlButtons, span = {
//            if (it is SeatFinderButtonState.PrimaryButton)
//                GridItemSpan(2)
//            else
//                GridItemSpan(1)
//        }) {
//            Button(
//                onClick = {
//                    it.onClick(endTime)
//                    expandPartially()
//                },
//                modifier = Modifier
//                    .height(72.dp)
//                    .padding(8.dp),
//                enabled = it.isEnabled
//            ) {
//                Text(it.name)
//            }
//        }
//    }
}


@Composable
fun ControlPanelButton(
    requestType: SeatFinderUserRequestType,
    clickButton: (SeatFinderUserRequestType) -> Unit = {},
) {
    BottomButton(
        text = requestType.toUiText(),
        click = {
            clickButton(requestType)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun ControlPanelPreview() {
}
