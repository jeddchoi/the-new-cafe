package io.github.jeddchoi.designsystem.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PrimaryButton(
    buttonText: String,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        enabled = enabled,
        onClick = { onClick() },
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        contentPadding = ButtonDefaults.TextButtonContentPadding,
        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp),
    ) {
        Text(text = buttonText, fontSize = 20.sp, color = Color.White, )
    }
}