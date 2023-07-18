package io.github.jeddchoi.designsystem.component.input

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import io.github.jeddchoi.common.CafeIcons
import io.github.jeddchoi.common.UiText


@Composable
fun PasswordField(
    value: String,
    changeValue: (String) -> Unit,
    modifier: Modifier = Modifier,
    labelText: String = "",
    supportingText: UiText? = null,
    isError: Boolean = false,
    isLastButton: Boolean = false,
    pressKeyboardDone: () -> Unit = {},
) {
    var showPassword by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = value,
        onValueChange = changeValue,
        shape = RoundedCornerShape(15.dp),
        label = {
            Text(text = labelText)
        },
        isError = isError,
        supportingText = {
            supportingText?.asString()?.let { Text(text = it) }
        },
        singleLine = true,
        modifier = modifier,
        keyboardOptions = KeyboardOptions(
            imeAction = if (isLastButton) ImeAction.Done else ImeAction.Next,
            keyboardType = KeyboardType.Password
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) },
            onDone = {
                focusManager.clearFocus()
                pressKeyboardDone()
            },
        ),
        visualTransformation = if (!showPassword) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = {
            IconButton(
                onClick = { showPassword = !showPassword }
            ) {
                Icon(
                    imageVector = if (showPassword) CafeIcons.Visibility else CafeIcons.VisibilityOff,
                    tint = Color.Gray,
                    contentDescription = "Password Toggle"
                )
            }
        }
    )
}