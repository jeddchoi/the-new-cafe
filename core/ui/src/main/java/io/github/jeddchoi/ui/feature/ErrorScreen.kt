package io.github.jeddchoi.ui.feature

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.github.jeddchoi.designsystem.component.lottie.EmptyLottie
import io.github.jeddchoi.ui.R

@Composable
fun ErrorScreen(
    exception: Throwable,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        EmptyLottie()
        SelectionContainer {
            Text(stringResource(id = R.string.error_with_exception, exception.localizedMessage ?: "No Message"))
            Text(
                text = exception.stackTraceToString(),
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}