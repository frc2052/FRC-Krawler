package com.team2052.frckrawler.ui.components.fields

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.R
import timber.log.Timber

@Composable
fun FRCKrawlerDropdown(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    validity: Boolean = true,
    validityCheck: (String) -> Unit = { },
    enabled: Boolean = true,
    label: String,
    dropdownItems: List<String>,
) {
    // TODO: Implement interaction between modeselect screen and the server home screen
    val focusManager = LocalFocusManager.current

    var width by remember { mutableStateOf(0) }

    var hasFocus by remember { mutableStateOf(false) }

    var lastValue by remember { mutableStateOf("") }

    Column(modifier = modifier) {
        FRCKrawlerTextField(
            modifier = Modifier.onGloballyPositioned { width = it.size.width },
            value = value,
            onValueChange = { },
            icon = {
                if (hasFocus) {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropUp,
                        contentDescription = stringResource(R.string.cd_dropdown_collapse),
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = stringResource(R.string.cd_dropdown_expand),
                    )
                }
            },
            validity = validity,
            validityCheck = validityCheck,
            isError = { validity -> !validity },
            enabled = enabled,
            readOnly = true,
            label = label,
            onFocusChange = { focus -> hasFocus = focus }
        )

        DropdownMenu(
            modifier = Modifier.width(width.dp),
            expanded = hasFocus,
            onDismissRequest = {
                focusManager.clearFocus()
            }
        ) {
            dropdownItems.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        lastValue = if (item != lastValue) {
                            onValueChange(item)
                            item
                        } else {
                            onValueChange("")
                            ""
                        }

                        focusManager.clearFocus()
                    },
                ) { Text(item) }
            }
        }
    }
}