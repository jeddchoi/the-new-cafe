package io.github.jeddchoi.mypage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.jeddchoi.data.service.seatfinder.SeatFinderRequestType
import kotlinx.coroutines.CoroutineScope
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColumnScope.ControlPanel(
    scaffoldState: BottomSheetScaffoldState,
    scope: CoroutineScope,
    quit: () -> Unit = {},
    occupySeat: (Instant) -> Unit = {},
    doBusiness: (Instant) -> Unit = {},
    resumeUsing: () -> Unit = {},
    leaveAway: (Instant) -> Unit = {},
    changeMainStateEndTime: (Instant) -> Unit = {},
    changeSubStateEndTime: (Instant) -> Unit = {},
) {
    val current = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    var offsetSeconds by remember { mutableStateOf(50f) }

    var endTime by remember {
        mutableStateOf(Clock.System.now())
    }

    Box(
        Modifier
            .fillMaxWidth()
            .height(128.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Text("Swipe up to expand sheet")
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Text(text = offsetSeconds.toInt().toString())
        Slider(
            modifier = Modifier.semantics { contentDescription = "Localized Description" },
            value = offsetSeconds,
            onValueChange = {
                offsetSeconds = it
                endTime = Clock.System.now() + offsetSeconds.toInt().seconds
            },
            valueRange = 0f..100f,
            steps = 19
        )
        Text(text = endTime.toLocalDateTime(TimeZone.currentSystemDefault()).toString())
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
    ) {
        item(span = { GridItemSpan(2) }) {
            Button(
                onClick = quit,
                modifier = Modifier
                    .height(72.dp)
                    .padding(8.dp)
            ) {
                Text(SeatFinderRequestType.Quit.name)
            }
        }
        item {
            Button(
                onClick = { occupySeat(endTime) },
                modifier = Modifier
                    .height(72.dp)
                    .padding(8.dp)
            ) {
                Text(SeatFinderRequestType.OccupySeat.name)
            }
        }
        item {
            Button(
                onClick = { doBusiness(endTime) },
                modifier = Modifier
                    .height(72.dp)
                    .padding(8.dp)
            ) {
                Text(SeatFinderRequestType.DoBusiness.name)
            }
        }
        item {
            Button(
                onClick = resumeUsing,
                modifier = Modifier
                    .height(72.dp)
                    .padding(8.dp)
            ) {
                Text(SeatFinderRequestType.ResumeUsing.name)
            }
        }
        item {
            Button(
                onClick = { leaveAway(endTime) },
                modifier = Modifier
                    .height(72.dp)
                    .padding(8.dp)
            ) {
                Text(SeatFinderRequestType.LeaveAway.name)
            }
        }
        item {
            Button(
                onClick = { changeMainStateEndTime(endTime) },
                modifier = Modifier
                    .height(72.dp)
                    .padding(8.dp)
            ) {
                Text(SeatFinderRequestType.ChangeMainStateEndTime.name)
            }
        }
        item {
            Button(
                onClick = { changeSubStateEndTime(endTime) },
                modifier = Modifier
                    .height(72.dp)
                    .padding(8.dp)
            ) {
                Text(SeatFinderRequestType.ChangeSubStateEndTime.name)
            }
        }
    }
//    Column(
//        Modifier
//            .fillMaxWidth()
//            .padding(64.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text("Sheet content")
//        Spacer(Modifier.height(20.dp))
//        Button(
//            onClick = {
//                scope.launch { scaffoldState.bottomSheetState.partialExpand() }
//            }
//        ) {
//            Text("Click to collapse sheet")
//        }
//    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ControlPanelPreview() {
    Column {
        ControlPanel(
            scaffoldState = rememberBottomSheetScaffoldState(),
            scope = rememberCoroutineScope()
        )
    }
}
