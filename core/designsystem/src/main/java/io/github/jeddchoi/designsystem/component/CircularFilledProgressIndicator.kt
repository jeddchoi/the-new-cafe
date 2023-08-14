package io.github.jeddchoi.designsystem.component

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.onSizeChanged
import io.github.jeddchoi.designsystem.pixelsToDp

@Composable
fun CircularFilledProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = ProgressIndicatorDefaults.circularColor,
    trackColor: Color = ProgressIndicatorDefaults.circularTrackColor,
    strokeCap: StrokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
) {
    var strokeWidth by remember { mutableIntStateOf(16) }
    val strokeWidthDp = pixelsToDp(pixels = strokeWidth)
    CircularProgressIndicator(
        modifier = modifier.onSizeChanged {
            strokeWidth = it.width.div(2)
        },
        strokeWidth = strokeWidthDp,
        progress = progress,
    )
}