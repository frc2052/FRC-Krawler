package com.team2052.frckrawler.ui.components._new

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.R

@Composable
fun Dropdown(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String,
    builder: DropdownBuilder.() -> Unit,
) {
    val dropdownBuilder = remember {
        DropdownBuilder().apply(builder)
    }

    val dropdownItems = dropdownBuilder.build()

    val focusManager = LocalFocusManager.current

    var currentIndex by remember { mutableStateOf(-1) }

    var width by remember { mutableStateOf(0) }

    val interactionSource = remember { MutableInteractionSource() }
    val hasFocus by interactionSource.collectIsFocusedAsState()

    Column(modifier = modifier) {
        TextField(
            modifier = Modifier.onGloballyPositioned { width = it.size.width }.alpha(2f),
            value = if (currentIndex in 0 until dropdownItems.size) dropdownItems[currentIndex] else label,
            onValueChange = { },
            enabled = enabled,
            readOnly = true,
            label = if (currentIndex in 0 until dropdownItems.size) {
                { Text(label) }
            } else {
                null
            },
            trailingIcon = {
                Crossfade(
                    targetState = hasFocus,
                    animationSpec = tween(200),
                ) { focus ->
                    if (focus) {
                        Icon(
                            imageVector = Icons.Filled.ExpandLess,
                            contentDescription = stringResource(R.string.cd_dropdown_collapse),
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.ExpandMore,
                            contentDescription = stringResource(R.string.cd_dropdown_expand),
                        )
                    }
                }
            },
            shape = if (hasFocus) {
                MaterialTheme.shapes.small.copy(
                    bottomEnd = ZeroCornerSize,
                    bottomStart = ZeroCornerSize
                )
            } else {
                RoundedCornerShape(4.dp)
            },
            interactionSource = interactionSource,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.onSurface.copy(alpha = 0.04f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedLabelColor = MaterialTheme.colors.onSurface.copy(ContentAlpha.medium),
            ),
        )

        DropdownMenu(
            modifier = Modifier.width(width.dp),
            expanded = hasFocus,
            onDismissRequest = {
                focusManager.clearFocus()
            },
        ) {
            dropdownItems.forEachIndexed() { index, item ->
                DropdownMenuItem(
                    onClick = {
                        currentIndex = index
                        focusManager.clearFocus()
                    },
                ) { Text(item) }
            }
        }
    }
}

open class DropdownBuilder {
    private val dropdownItems: MutableList<String> = mutableListOf()

    fun dropdownItem(item: String) {
        dropdownItems += item
    }

    fun build() = dropdownItems
}