package io.github.jeddchoi.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jeddchoi.designsystem.TheNewCafeTheme
import io.github.jeddchoi.designsystem.pixelsToDp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Picker(
    items: List<String>,
    modifier: Modifier = Modifier,
    state: PickerState = rememberPickerState(),
    startIndex: Int = 0,
    visibleItemsCount: Int = 3,
    textModifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    dividerColor: Color = LocalContentColor.current,
) {
    val visibleItemsMiddle = visibleItemsCount / 2
    val itemsWithBuffer = buildList {
        repeat(visibleItemsMiddle) {
            add("")
        }
        addAll(items)
        repeat(visibleItemsMiddle) {
            add("")
        }
    }

    fun getItem(index: Int) = itemsWithBuffer[index % itemsWithBuffer.size]

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = startIndex)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    var itemHeightPixels by remember { mutableIntStateOf(0) }
    val itemHeightDp = pixelsToDp(itemHeightPixels)

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .map { it + visibleItemsMiddle }
            .distinctUntilChanged()
            .collect { selectedIndex ->
                val item = getItem(selectedIndex)
                state.selectedItem = item
            }
    }

    val height = itemHeightDp * visibleItemsCount
    val halfHeight = itemHeightPixels / 2
    Box(modifier = modifier) {
        Divider(
            color = dividerColor,
            modifier = Modifier.offset(y = itemHeightDp * visibleItemsMiddle)
        )

        Divider(
            color = dividerColor,
            modifier = Modifier.offset(y = itemHeightDp * (visibleItemsMiddle + 1))
        )

        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .fadingEdge(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.5f to MaterialTheme.colorScheme.onBackground,
                        1f to Color.Transparent
                    )
                ),
        ) {
            itemsIndexed(itemsWithBuffer) { index, item ->
                val opacity by remember {
                    derivedStateOf {
                        val currentItemInfo = listState.layoutInfo.visibleItemsInfo
                            .firstOrNull { it.index == (index - visibleItemsMiddle) }
                            ?: return@derivedStateOf 0.5f
                        val itemHalfSize = currentItemInfo.size / 2
                        (1f - minOf(
                            1f,
                            abs(currentItemInfo.offset + itemHalfSize - halfHeight).toFloat() / halfHeight
                        ) * 0.5f)
                    }
                }
                Text(
                    text = item,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = textStyle.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier
                        .onSizeChanged { size -> itemHeightPixels = size.height }
                        .scale(opacity)
                        .then(textModifier)
                )
            }
        }



    }
}


@Composable
fun rememberPickerState() = remember { PickerState() }

class PickerState {
    var selectedItem by mutableStateOf("")
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun WheelPickerPreview() {
    TheNewCafeTheme() {

        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {

                val values = remember { (1..99).map { it.toString() } }
                val valuesPickerState = rememberPickerState()
                val units = remember { listOf("seconds", "minutes", "hours") }
                val unitsPickerState = rememberPickerState()

                Text(text = "Example Picker", modifier = Modifier.padding(top = 16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Picker(
                        state = valuesPickerState,
                        items = values,
                        visibleItemsCount = 7,
                        startIndex = 3,
                        modifier = Modifier.weight(0.3f),
                        textModifier = Modifier.padding(8.dp),
                        textStyle = TextStyle(fontSize = 32.sp),
                        dividerColor = MaterialTheme.colorScheme.primary,
                    )
                    Picker(
                        state = unitsPickerState,
                        items = units,
                        visibleItemsCount = 5,
                        modifier = Modifier.weight(0.7f),
                        textModifier = Modifier.padding(8.dp),
                        textStyle = TextStyle(fontSize = 32.sp),
                        dividerColor = MaterialTheme.colorScheme.primary,
                    )
                }

                Text(
                    text = "Interval: ${valuesPickerState.selectedItem} ${unitsPickerState.selectedItem}",
                    modifier = Modifier.padding(vertical = 16.dp)
                )

            }
        }
    }

}
