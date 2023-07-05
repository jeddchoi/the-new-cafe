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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.jeddchoi.designsystem.TheNewCafeTheme

@Composable
fun PrimaryButton(
    buttonText: String,
    isLoading: Boolean,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Button(
        enabled = enabled,
        onClick = onClick,
        modifier = modifier.requiredHeight(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        contentPadding = ButtonDefaults.TextButtonContentPadding,
        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp),
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Text(
                text = buttonText,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PrimaryButtonPreview() {
    TheNewCafeTheme {
        PrimaryButton(
            buttonText = "HELLO", isLoading = false, onClick = { /*TODO*/ }, enabled = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}