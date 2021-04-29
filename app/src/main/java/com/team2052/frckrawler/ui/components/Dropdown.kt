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

@Composable
fun LabeledDropdown(
    modifier: Modifier,
    label: String,
    content: @Composable () -> Unit
) = Row(verticalAlignment = Alignment.CenterVertically) {
    Text(modifier = Modifier.sizeIn(minWidth = 128.dp), text = "${label}:")
    content()
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

@Composable
fun Dropdown(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(16.dp),
    defaultSelectedIndex: Int = 0,
    dropdownList: List<String>
) = Column {
    var selectedIndex by remember { mutableStateOf(if(defaultSelectedIndex in dropdownList.indices) defaultSelectedIndex else dropdownList.size - 1) }
    var expanded by remember { mutableStateOf(false) }

    var dropdownWidth by remember { mutableStateOf(0) }

    Row(
        modifier = modifier
            .sizeIn(minWidth = 256.dp)
            .width(dropdownWidth.dp)
            .graphicsLayer { shadowElevation = if (expanded) 8f else 0f }
            .clickable { expanded = true }
            .clip(RoundedCornerShape(4.dp))
            .background(if (expanded) MaterialTheme.colors.surface else Color.Transparent)
            .padding(paddingValues),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = dropdownList[selectedIndex])
        Spacer(modifier = Modifier.width(32.dp))
        Icon(
            imageVector = if (expanded) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowDropUp,
            contentDescription = "Dropdown Expand Toggle"
        )
    }
    DropdownMenu(
        modifier = Modifier.onGloballyPositioned { dropdownWidth = it.size.width },
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        dropdownList.forEach { i ->
//            DropdownMenuItem(
//                modifier = Modifier.sizeIn(), // sets size constraints to undefined
//                contentPadding = paddingValues,
//                onClick = {
//                    if(selectedIndex == index) {
//                        expanded = false
//                    } else {
//                        selectedIndex = index
//                    }
//                }
//            ) { Text(text = s, softWrap = false) }
//            DropdownItem(onClick = { /*TODO*/ }) {
//
//            }
        }
    }
}

//@Composable
//internal fun DropdownItem(
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier,
//    enabled: Boolean = true,
//    contentPadding: PaddingValues = PaddingValues(16.dp, 0.dp),
//    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
//    content: @Composable RowScope.() -> Unit
//) {
//    // TODO(popam, b/156911853): investigate replacing this Row with ListItem
//    Row(
//        modifier = modifier
//            .clickable(
//                enabled = enabled,
//                onClick = onClick,
//                interactionSource = interactionSource,
//                indication = rememberRipple(true)
//            )
//            .fillMaxWidth()
//            .padding(contentPadding),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        val typography = MaterialTheme.typography
//        ProvideTextStyle(typography.subtitle1) {
//            val contentAlpha = if (enabled) ContentAlpha.high else ContentAlpha.disabled
//            CompositionLocalProvider(LocalContentAlpha provides contentAlpha) {
//                content()
//            }
//        }
//    }
//}

@Composable
fun DropdownFixed(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(16.dp),
    defaultSelectedIndex: Int = 0,
    dropdownContentList: List<@Composable () -> Unit>
) = Column(modifier = modifier) {
    var selectedIndex by remember {
        mutableStateOf(if(defaultSelectedIndex in dropdownContentList.indices) defaultSelectedIndex else dropdownContentList.size - 1)
    }
    var expanded by remember { mutableStateOf(false) }

    var dropdownContentWidth = 0
    val dropdownContent: (@Composable () -> Unit) = {
        Layout(
            modifier = modifier.background(Color.Blue),
            content = {
                dropdownContentList.forEachIndexed { index, item ->
                    DropdownMenuItem(onClick = { selectedIndex = index }) { item() }
                }
            }
        ) { measurables, constraints ->
            var dropdownContentHeight = IntArray(dropdownContentList.size) { 0 }
            val placeables = measurables.mapIndexed { index, measurable ->
                val placeable = measurable.measure(constraints)
                dropdownContentHeight[index] = placeable.height
                //max(dropdownContentWidth, placeable.width)
                if(dropdownContentWidth > placeable.width) dropdownContentWidth = placeable.width
                placeable
            }
            layout(dropdownContentWidth, dropdownContentHeight.sumBy { it }) {
                placeables.forEachIndexed { index, placeable ->
                    placeable.placeRelative(0, 0)
                }
            }
        }
    }
    Row(
        modifier = modifier
            .sizeIn(minWidth = 256.dp)
            .width(dropdownContentWidth.dp)
            .graphicsLayer { shadowElevation = if (expanded) 8f else 0f }
            .clip(RoundedCornerShape(4.dp))
            .clickable { expanded = true }
            .background(Color.Transparent) // if(expanded) MaterialTheme.colors.surface else
            .padding(paddingValues),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Hello")
        Spacer(modifier = Modifier.width(32.dp))
        Icon(
            imageVector = if (expanded) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowDropUp,
            contentDescription = "Dropdown Expand Toggle"
        )
    }
    DropdownMenu(modifier = Modifier.background(Color.Red), expanded = expanded, onDismissRequest = { expanded = false }) {
        dropdownContent()
    }
}