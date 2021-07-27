package com.team2052.frckrawler.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun FRCKrawlerTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    validate: (String) -> Boolean = { true },
    onValidityChange: (Boolean) -> Unit = {},
    enabled: Boolean = true,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Ascii,
    onFocusChange: (Boolean) -> Unit = {},
) {
    // TODO: Implement interaction between modeselect screen and the server home screen
    val focusManager = LocalFocusManager.current

    var validity by remember { mutableStateOf(true) }
    if (value.isNotEmpty()) {
        validity = validate(value)
        onValidityChange(validity)
    }

    val interactionSource = remember { MutableInteractionSource() }
    val hasFocus by interactionSource.collectIsFocusedAsState()
    onFocusChange(hasFocus)

    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        enabled = enabled,
        label = {
            Text(text = label)
        },
        isError = !validity,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        singleLine = true,
        interactionSource = interactionSource,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            cursorColor = MaterialTheme.colors.secondary,
            focusedBorderColor = MaterialTheme.colors.secondary,
            focusedLabelColor = MaterialTheme.colors.secondary,
        ),
    )
}