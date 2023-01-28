package com.team2052.frckrawler.ui.components.fields

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.R

@Composable
fun <T> FRCKrawlerDropdown(
    modifier: Modifier = Modifier,
    value: T?,
    getLabel: (T?) -> String,
    onValueChange: (T?) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    validity: Boolean = true,
    enabled: Boolean = true,
    label: String,
    dropdownItems: List<T>,
) {
    // TODO: Implement interaction between modeselect screen and the server home screen
    val focusManager = LocalFocusManager.current

    var width by remember { mutableStateOf(0) }

    var hasFocus by remember { mutableStateOf(false) }

    var lastValue by remember { mutableStateOf(value) }

    Column(modifier = modifier) {
        FRCKrawlerTextField(
            modifier = Modifier.onGloballyPositioned { width = it.size.width },
            value = getLabel(value),
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
            isError = { validity -> !validity },
            enabled = enabled,
            readOnly = true,
            label = label,
            onFocusChange = { focus ->
                hasFocus = focus
                onFocusChange(focus)
            }
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
                            onValueChange(null)
                            null
                        }

                        focusManager.clearFocus()
                    },
                ) { Text(getLabel(item)) }
            }
        }
    }
}