package io.github.jeddchoi.designsystem.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.jeddchoi.designsystem.TheNewCafeTheme

@Composable
fun GeneralTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholderMsg: String,
    isError: Boolean,
    errorMsg: String,
    modifier: Modifier = Modifier,
) {
    TheTextField(
        value = value,
        onValueChange = onValueChange,
        shape = RoundedCornerShape(15.dp),
        label = {
            Text(text = placeholderMsg, color = Color.LightGray)
        },
        isError = value.isNotEmpty() && isError,
        errorMsg = errorMsg,
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
    )
}


@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholderMsg: String,
    isError: Boolean,
    errorMsg: String,

    modifier: Modifier = Modifier,
) {

    var showPassword by remember { mutableStateOf(false) }

    TheTextField(
        value = value,
        onValueChange = onValueChange,
        shape = RoundedCornerShape(15.dp),
        label = {
            Text(text = placeholderMsg, color = Color.LightGray)
        },
        isError = value.isNotEmpty() && isError,
        errorMsg = errorMsg,
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
        visualTransformation = if (!showPassword) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = {
            IconButton(
                onClick = { showPassword = !showPassword }
            ) {
                Icon(
                    imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    tint = Color.Gray,
                    contentDescription = "Password Toggle"
                )
            }
        }
    )
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TheTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = MaterialTheme.shapes.small,
    colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors(),
    errorMsg: String = ""
) {
    Column(
        modifier = Modifier
            .padding(
                bottom = if (isError) {
                    0.dp
                } else {
                    10.dp
                }
            )
    ) {

        OutlinedTextField(
            enabled = enabled,
            readOnly = readOnly,
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            singleLine = singleLine,
            textStyle = textStyle,
            label = label,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            maxLines = maxLines,
            interactionSource = interactionSource,
            shape = shape,
            colors = colors,
        )

        if (isError) {
            Text(
                text = errorMsg,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }

}


@Preview(showBackground = true)
@Composable
private fun TheTextFieldPreview() {
    TheNewCafeTheme {

        Column {
            GeneralTextField(
                value = "Hello",
                onValueChange = {},
                placeholderMsg = "placeholder",
                isError = false,
                errorMsg = ""
            )
            GeneralTextField(
                value = "",
                onValueChange = {},
                placeholderMsg = "placeholder",
                isError = false,
                errorMsg = ""
            )

            GeneralTextField(
                value = "Hello",
                onValueChange = {},
                placeholderMsg = "placeholder",
                isError = true,
                errorMsg = "Error message",
                modifier = Modifier.weight(1f)
            )
        }

    }
}