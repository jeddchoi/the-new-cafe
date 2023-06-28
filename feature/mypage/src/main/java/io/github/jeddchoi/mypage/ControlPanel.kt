package io.github.jeddchoi.mypage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.commandiron.wheel_picker_compose.WheelDateTimePicker
import com.commandiron.wheel_picker_compose.core.TimeFormat
import io.github.jeddchoi.ui.model.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toLocalDateTime
import java.time.ZoneId
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ColumnScope.ControlPanel(
    scaffoldState: BottomSheetScaffoldState,
    scope: CoroutineScope,
    uiState: UiState<MyPageUiStateData>,
) {
    var endTime by remember { mutableStateOf(Instant.DISTANT_FUTURE) }

    when (uiState) {
        UiState.EmptyResult -> {
            Text("No result")
        }

        is UiState.Error -> {
            Text("Error")
        }

        UiState.InitialLoading -> {
            CircularProgressIndicator()
        }

        is UiState.Success -> {
            Text(text = uiState.data.messages.lastOrNull()?.contentId?.let { stringResource(it) } ?: "No message")
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(value = endTime.toString(), onValueChange = {}, readOnly = true)
                Button(onClick = {
                    endTime = Instant.DISTANT_FUTURE
                }) {
                    Text("Reset")
                }
            }

            EndTimeInput {
                endTime = it
            }

            ControlPanelButtons(uiState.data, endTime, scope, scaffoldState)
        }
    }
}

@Composable
private fun ColumnScope.EndTimeInput(onEndTimeChange: (Instant) -> Unit) {
    var offsetSeconds by remember { mutableStateOf(50f) }

    val current = Clock.System.now()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {


        Text(text = "${offsetSeconds.toInt()} seconds")
        Slider(
            modifier = Modifier.semantics { contentDescription = "Localized Description" },
            value = offsetSeconds,
            onValueChange = {
                offsetSeconds = it
                onEndTimeChange(current.plus(it.toInt().seconds))
            },
            valueRange = 0f..100f,
            steps = 19,
        )


        WheelDateTimePicker(
            startDateTime = current.toLocalDateTime(TimeZone.currentSystemDefault())
                .toJavaLocalDateTime(),
            timeFormat = TimeFormat.HOUR_24,
            rowCount = 5,
        ) { snappedDateTime ->
            onEndTimeChange(
                snappedDateTime.atZone(ZoneId.systemDefault()).toInstant().toKotlinInstant()
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ControlPanelButtons(
    uiState: MyPageUiStateData,
    endTime: Instant,
    scope: CoroutineScope,
    scaffoldState: BottomSheetScaffoldState
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
    ) {
        items(uiState.controlButtons, span = {
            if (it is SeatFinderButtonState.PrimaryButton)
                GridItemSpan(2)
            else
                GridItemSpan(1)
        }) {
            Button(
                onClick = {
                    it.onClick(endTime)
                    scope.launch { scaffoldState.bottomSheetState.partialExpand() }
                },
                modifier = Modifier
                    .height(72.dp)
                    .padding(8.dp),
                enabled = it.isEnabled
            ) {
                Text(it.name)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ControlPanelPreview() {
}
