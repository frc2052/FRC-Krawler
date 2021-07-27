package com.team2052.frckrawler.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp

@Composable
fun FRCKrawlerDropdown(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    label: String,
    dropdownItems: List<String>,
) {
    // TODO: Implement interaction between modeselect screen and the server home screen
    val focusManager = LocalFocusManager.current

    var width by remember { mutableStateOf(0) }

    val interactionSource = remember { MutableInteractionSource() }
    val hasFocus by interactionSource.collectIsFocusedAsState()

    Column(modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier.onGloballyPositioned { width = it.size.width },
            value = value,
            onValueChange = {
                onValueChange(it)
            },
            enabled = enabled,
            readOnly = true,
            label = {
                Text(text = label)
            },
            trailingIcon = {
                Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "dropdown")
            },
            singleLine = true,
            interactionSource = interactionSource,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colors.secondary,
                focusedLabelColor = MaterialTheme.colors.secondary,
            ),
        )
        DropdownMenu(modifier = Modifier.width(width.dp), expanded = hasFocus, onDismissRequest = { focusManager.clearFocus() }) {
            dropdownItems.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        onValueChange(item)
                        focusManager.clearFocus()
                    },
                ) {
                    Text(item)
                }
            }
        }
    }
}