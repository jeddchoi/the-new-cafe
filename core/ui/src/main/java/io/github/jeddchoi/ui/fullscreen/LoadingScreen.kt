package io.github.jeddchoi.ui.fullscreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.designsystem.component.lottie.LoadingLottie
import io.github.jeddchoi.ui.R


@Composable
fun LoadingScreen(modifier: Modifier = Modifier, title: UiText = UiText.StringResource(R.string.loading)) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            LoadingLottie()
            Text(title.asString())
        }
    }
}