package io.github.jeddchoi.designsystem.component.lottie

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.jeddchoi.designsystem.R

@Composable
fun ErrorLottie(
    modifier: Modifier = Modifier
) {
    IterateForeverLottie(
        resId = R.raw.error,
        modifier = modifier
    )
}