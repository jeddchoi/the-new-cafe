package io.github.jeddchoi.designsystem.component.lottie

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import io.github.jeddchoi.designsystem.R

@Composable
fun ConfettiLottie(
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.confetti))
    LottieAnimation(
        composition,
        modifier = modifier,
        contentScale = ContentScale.Crop,
        alignment = Alignment.Center,
    )
}