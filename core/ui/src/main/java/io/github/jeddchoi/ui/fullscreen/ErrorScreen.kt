package io.github.jeddchoi.ui.fullscreen

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.designsystem.TheNewCafeTheme
import io.github.jeddchoi.designsystem.component.ExpandableCard
import io.github.jeddchoi.designsystem.component.lottie.ErrorLottie
import io.github.jeddchoi.ui.R

@Composable
fun ErrorScreen(
    exception: Throwable,
    modifier: Modifier = Modifier,
) {
    Surface {
        Column(
            modifier = modifier.padding(top = 96.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            ErrorLottie(
                modifier = Modifier
                    .height(192.dp)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(
                        id = R.string.error_with_exception,
                        exception.localizedMessage ?: "No Message"
                    ),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.error,
                )
                Spacer(modifier = Modifier.height(16.dp))

                ExpandableCard(
                    title = UiText.StringResource(R.string.view_stacktrace_error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SelectionContainer(
                        modifier = Modifier.verticalScroll(rememberScrollState()).padding(16.dp)
                    ) {
                        Text(exception.stackTraceToString())
                    }
                }
            }
        }
    }
}


@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    showBackground = false, showSystemUi = true
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    showBackground = false, showSystemUi = true
)
@Composable
fun ErrorScreenPreview() {
    TheNewCafeTheme {
        ErrorScreen(
            modifier = Modifier.fillMaxSize(),
            exception = Throwable("Hello Error")
        )
    }

}