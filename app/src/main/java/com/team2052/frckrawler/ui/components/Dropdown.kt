package com.team2052.frckrawler.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp

/**
 * WORK IN PROGRESS
 */

@Composable
fun LabeledDropdown(
    modifier: Modifier,
    label: String,
    content: @Composable () -> Unit
) = Row(verticalAlignment = Alignment.CenterVertically) {
    Text(modifier = Modifier.sizeIn(minWidth = 128.dp), text = "${label}:")
    content()
}

// Potential work around: use data class to pass in required data to construct a DropdownItem
@Composable
fun Dropdown(
    content: @Composable ColumnScope.() -> Unit
) {

}

@Composable
fun FinalDropdown(
    onClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    enabled: Boolean = true,
    paddingValues: PaddingValues = PaddingValues(16.dp),
    defaultSelectedIndex: Int = 0,
    content: List<@Composable () -> Unit>
) {
    var dropdownWidth by remember { mutableStateOf(256) }

    Column(modifier = Modifier.sizeIn(minWidth = 256.dp, maxWidth = dropdownWidth.dp)) {
        var selectedIndex by remember { mutableStateOf(if(defaultSelectedIndex in content.indices) defaultSelectedIndex else content.size - 1) }

        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable { if (enabled) onClick(true) }
                .clip(RoundedCornerShape(4.dp))
                .background(if (expanded) MaterialTheme.colors.surface else Color.Transparent)
                .padding(paddingValues),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            content[selectedIndex]()
            Spacer(modifier = Modifier.width(32.dp))
            Icon(
                imageVector = if (expanded) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowDropUp,
                contentDescription = "Dropdown Expand Toggle"
            )
        }
        DropdownMenu(
            modifier = modifier.fillMaxWidth().onGloballyPositioned { dropdownWidth = it.size.width },
            expanded = expanded,
            onDismissRequest = { onClick(false) }
        ) {
            content.forEachIndexed { index, item ->
                FinalDropdownItem(onClick = { selectedIndex = index }) {
                    item()
                }
            }
        }
    }
}

@Composable
fun FinalDropdownItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .clickable(
                enabled = enabled,
                onClick = if(enabled) onClick else {{}},
                interactionSource = interactionSource,
                indication = rememberRipple(true)
            )
            .fillMaxWidth()
            .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProvideTextStyle(MaterialTheme.typography.subtitle1) {
            val contentAlpha = if (enabled) ContentAlpha.high else ContentAlpha.disabled
            CompositionLocalProvider(LocalContentAlpha provides contentAlpha) {
                content()
            }
        }
    }
}