package io.github.jeddchoi.ui.feature

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.designsystem.component.lottie.EmptyLottie
import io.github.jeddchoi.ui.R

@Composable
fun EmptyResultScreen(
    subject: UiText,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        EmptyLottie()
        Text(stringResource(id = R.string.empty_with_subject, subject.asString()))
    }
}