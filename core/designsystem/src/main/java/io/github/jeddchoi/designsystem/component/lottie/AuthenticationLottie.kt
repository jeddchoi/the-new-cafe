package io.github.jeddchoi.designsystem.component.lottie

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.jeddchoi.designsystem.R

@Composable
fun AuthenticationLottie(
    modifier: Modifier = Modifier
) {
    IterateForeverLottie(
        resId = R.raw.authentication,
        modifier = modifier
    )
}

