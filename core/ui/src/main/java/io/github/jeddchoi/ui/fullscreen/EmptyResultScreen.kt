package io.github.jeddchoi.ui.fullscreen

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.designsystem.TheNewCafeTheme
import io.github.jeddchoi.ui.R

@Composable
fun EmptyResultScreen(
    subject: UiText,
    modifier: Modifier = Modifier,
) {
    Surface {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center,
        ) {
            Text(
                stringResource(id = R.string.empty_with_subject, subject.asString()),
                color = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}


@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    showBackground = true, showSystemUi = true
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    showBackground = true, showSystemUi = true
)
@Composable
fun EmptyResultScreenPreview() {
    TheNewCafeTheme {
        EmptyResultScreen(
            subject = UiText.DynamicString("Subject"),
            modifier = Modifier.fillMaxSize()
        )
    }
}