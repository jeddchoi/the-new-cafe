package io.github.jeddchoi.designsystem.component.input

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.jeddchoi.common.UiText

@Composable
fun GeneralTextField(
    value: String,
    changeValue: (String) -> Unit,
    modifier: Modifier = Modifier,
    labelText: String = "",
    isError: Boolean = false,
    supportingText: UiText? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    isLastButton: Boolean = false,
    pressKeyboardDone: () -> Unit = {},
) {
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
            keyboardType = keyboardType
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) },
            onDone = {
                focusManager.clearFocus()
                pressKeyboardDone()
            },
        ),
    )
}