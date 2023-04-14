package io.github.jeddchoi.designsystem.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PrimaryButton(
    buttonText: String,
    isBusy: Boolean,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {

    Button(
        enabled = enabled && !isBusy,
        onClick = { onClick() },
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        contentPadding = ButtonDefaults.TextButtonContentPadding,
        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp),
    ) {
        if (isBusy) {
            CircularProgressIndicator()
        } else {
            Text(text = buttonText, fontSize = 20.sp, color = Color.White)
        }
    }
}