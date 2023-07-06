package io.github.jeddchoi.ui.feature

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.ui.R


@Composable
fun LoadingScreen(modifier: Modifier = Modifier, title: io.github.jeddchoi.common.UiText = io.github.jeddchoi.common.UiText.StringResource(R.string.loading)) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Text(title.asString())
        }
    }
}