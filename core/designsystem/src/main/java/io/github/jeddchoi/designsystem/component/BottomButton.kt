package io.github.jeddchoi.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.jeddchoi.common.UiText
import io.github.jeddchoi.designsystem.TheNewCafeTheme

enum class BottomButtonType {
    Elevated,
    Filled,
    FilledTonal,
    Outlined,
    Text,
    ;
}

@Composable
fun BottomButton(
    text: UiText,
    isLoading: Boolean,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    type: BottomButtonType = BottomButtonType.Filled
) {
    val focusManager = LocalFocusManager.current
    val onButtonClick = {
        onClick()
        focusManager.clearFocus()
    }
    val shape = RoundedCornerShape(12.dp)
    when (type) {
        BottomButtonType.Elevated -> {
            ElevatedButton(
                enabled = enabled,
                onClick = onButtonClick,
                modifier = modifier.requiredHeight(56.dp),
                shape = shape,
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text(text = text.asString())
                }
            }
        }
        BottomButtonType.Filled -> {
            Button(
                enabled = enabled,
                onClick = onButtonClick,
                modifier = modifier.requiredHeight(56.dp),
                shape = shape,
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text(text = text.asString())
                }
            }
        }
        BottomButtonType.FilledTonal -> {
            FilledTonalButton(
                enabled = enabled,
                onClick = onButtonClick,
                modifier = modifier.requiredHeight(56.dp),
                shape = shape,
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text(text = text.asString())
                }
            }
        }
        BottomButtonType.Outlined -> {
            OutlinedButton(
                enabled = enabled,
                onClick = onButtonClick,
                modifier = modifier.requiredHeight(56.dp),
                shape = shape,
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text(text = text.asString())
                }
            }
        }
        BottomButtonType.Text -> {
            TextButton(
                enabled = enabled,
                onClick = onButtonClick,
                modifier = modifier.requiredHeight(56.dp),
                shape = shape,
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text(text = text.asString())
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun PrimaryButtonPreview() {
    TheNewCafeTheme {
        BottomButton(
            text = UiText.DynamicString(""),
            isLoading = false,
            onClick = { /*TODO*/ },
            enabled = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}