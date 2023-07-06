package io.github.jeddchoi.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.jeddchoi.designsystem.TheNewCafeTheme

enum class BottomButtonType {
    Primary,
    Secondary,
    Tertiary,
    ;

    @Composable
    fun textColor() =
        when (this) {
            Primary -> MaterialTheme.colorScheme.onPrimaryContainer
            Secondary -> MaterialTheme.colorScheme.onSecondaryContainer
            Tertiary -> MaterialTheme.colorScheme.onTertiaryContainer
        }

    @Composable
    fun containerColor() =
        when (this) {
            Primary -> MaterialTheme.colorScheme.primaryContainer
            Secondary -> MaterialTheme.colorScheme.secondaryContainer
            Tertiary -> MaterialTheme.colorScheme.tertiaryContainer
        }
}

@Composable
fun BottomButton(
    buttonText: String,
    isLoading: Boolean,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    type: BottomButtonType = BottomButtonType.Primary
) {
    val focusManager = LocalFocusManager.current
    Button(
        enabled = enabled,
        onClick = {
            onClick()
            focusManager.clearFocus()
        },
        modifier = modifier.requiredHeight(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = type.containerColor(),
        ),
        contentPadding = ButtonDefaults.TextButtonContentPadding,
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Text(
                text = buttonText,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                color = type.textColor()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PrimaryButtonPreview() {
    TheNewCafeTheme {
        BottomButton(
            buttonText = "HELLO", isLoading = false, onClick = { /*TODO*/ }, enabled = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}