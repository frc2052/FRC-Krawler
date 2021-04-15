package com.team2052.frckrawler.base

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlin.math.max

@Composable
fun LabeledDropdown() {
    Dropdown(onDismissRequest = { /*TODO*/ }) {

    }
}
@Composable
fun Dropdown4(
    modifier: Modifier = Modifier,
    selectedIndex: Int = 0,
    dropdownList: List<String>
) {
    val verifiedSelectedIndex = if(selectedIndex in dropdownList.indices) selectedIndex else dropdownList.size - 1
    var expanded by remember { mutableStateOf(false) }
    Column {
        Row(
            modifier = modifier
                .defaultMinSize(minWidth = 256.dp)
                .width(IntrinsicSize.Min)
                //.fillMaxWidth()
                .clickable {
                    expanded = true
                }
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colors.surface)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = dropdownList[verifiedSelectedIndex])
            Icon(imageVector = if (expanded) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowDropUp, contentDescription = "Dropdown Expand Toggle")
        }
        DropdownMenu(
            modifier = Modifier
                .defaultMinSize(minWidth = 256.dp),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Surface(
                modifier = Modifier.background(MaterialTheme.colors.surface),
                elevation = 8.dp
            ) {
                Column {
                    Text("Holla!", modifier = Modifier.fillMaxWidth().background(Color.Blue))
                    for (s in dropdownList) {
                        DropdownMenuItem(modifier = Modifier.background(Color.Red), onClick = { /*TODO*/ }) {
                            Text(text = s)
                        }
                    }
                }
            }
        }
    }
}
//@Composable
//fun Dropdown4(
//    modifier: Modifier = Modifier,
//    paddingValues: PaddingValues = PaddingValues(16.dp),
//    selectedIndex: Int = 0,
//    dropdownList: List<String>
//) {
//    val verifiedSelectedIndex = if(selectedIndex in dropdownList.indices) selectedIndex else dropdownList.size - 1
//
//    var expanded by remember { mutableStateOf(false) }
//
//    Layout(content = {
//        Row(
//            modifier = modifier.padding()
//        ) {
//
//        }
//    }) { measurables, constraints ->
//        val selectorPlaceable = measurables[0].measure(constraints)
//        val dropdownPlaceable = measurables[1].measure(constraints)
//
//        layout(max(selectorPlaceable.width, dropdownPlaceable.width), selectorPlaceable.height) {
//            selectorPlaceable.placeRelative(0, 0)
//            if(expanded) {
//                dropdownPlaceable.placeRelative(0, selectorPlaceable.height)
//            }
//        }
//    }
//}

/**
 * 1. Layout
 *  a. Items
 *  b. Dropdown Content (Layout)
 */
@Composable
fun Dropdown3(
    modifier: Modifier = Modifier,
    selectedIndex: Int = 0,
    dropdownList: List<String>
) {
    var expanded by remember { mutableStateOf(false) }

    Layout(
        modifier = Modifier
            .width(IntrinsicSize.Max)
            .background(MaterialTheme.colors.surface),
        content = {
            Row(
                modifier = modifier
                    .defaultMinSize(minWidth = 256.dp)
                    .clickable {
                        expanded = !expanded
                    }
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = dropdownList[selectedIndex])
                Icon(
                    imageVector = if (expanded) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowDropUp,
                    contentDescription = "Dropdown Expand Toggle"
                )
            }
            Surface(
                modifier = Modifier
                    .width(IntrinsicSize.Max)
                    .padding(4.dp)
                    .background(Color.Blue)
            ) {
                Column(
                    modifier = Modifier.background(Color.Blue)
                ) {
                    for (item in dropdownList) {
                        Text(modifier = Modifier
                            .padding(8.dp)
                            .zIndex(1000f), text = item)
                    }
                }
            }
        }
    ) { measurables, constraints ->
        val selectorPlaceable = measurables[0].measure(constraints)
        val dropdownPlaceable = measurables[1].measure(constraints)

        layout(max(selectorPlaceable.width, dropdownPlaceable.width), selectorPlaceable.height) {
            selectorPlaceable.placeRelative(0, 0)
            if(expanded) {
                dropdownPlaceable.placeRelative(0, selectorPlaceable.height)
            }
        }
    }
//    Layout(content = {
//        Column(
//            modifier = modifier
//                .graphicsLayer {
//                    shadowElevation = if (false) 8.dp.toPx() else 0.dp.toPx()
//                }
//                .width(IntrinsicSize.Max)
//                .clip(RoundedCornerShape(4.dp))
//                .background(MaterialTheme.colors.surface)
//                .zIndex(1000f)
//        ) {
//            Layout(content = {
//                Row(
//                    modifier = modifier
//                        .defaultMinSize(minWidth = 256.dp)
//                        .clickable { }
//                        .fillMaxWidth()
//                        .padding(12.dp),
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    Text(text = dropdownList[selectedIndex])
//                    Icon(
//                        imageVector = if (false) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowDropUp,
//                        contentDescription = "Dropdown Expand Toggle"
//                    )
//                }
//                Surface(
//                    modifier = Modifier
//                        .width(IntrinsicSize.Max)
//                ) {
//                    Column {
//                        for (item in dropdownList) {
//                            Text(text = item)
//                        }
//                    }
//                }
//            }) { measurables, constraints ->
//                val selectorPlaceable = measurables[0].measure(constraints)
//                val dropdownPlaceable = measurables[1].measure(constraints)
//
//                layout(max(selectorPlaceable.width, dropdownPlaceable.width), selectorPlaceable.height * 2) {
//                    selectorPlaceable.placeRelative(0, 0)
//                    dropdownPlaceable.placeRelative(0, selectorPlaceable.height)
//                }
//            }
//        }
//    }) { measurables, constraints ->
//        val placeable = measurables[0].measure(constraints)
//
//        layout(placeable.width, 100) {
//            placeable.placeRelative(0, 0)
//        }
//    }
}

@Composable
fun OnlyMeasureByOne(
    modifier: Modifier = Modifier,
    selectedIndex: Int = 0,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->

        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }

        val index = if(selectedIndex in placeables.indices) selectedIndex else placeables.size - 1

        val height = placeables[index].height
        var yPosition = 0

        layout(0, height) {
            for (placeable in placeables) {
                if(placeable != placeables[index]) {
                    placeable.placeRelative(0, yPosition, 1000f)
                    yPosition += placeable.height
                }
            }
        }
    }
}

@Composable
fun Dropdown2(
    modifier: Modifier = Modifier,
    defaultExpandedState: Boolean = false,
    selectedIndex: Int = 0,
    content: @Composable ColumnScope.(Modifier) -> Unit,
) {
    var expanded by remember { mutableStateOf(defaultExpandedState) }
    var selectedItem by remember { mutableStateOf(false) }

    val expandedStates = remember { MutableTransitionState(false) }
    expandedStates.targetState = expanded

    val transition = updateTransition(expandedStates, "DropDownMenu")

    val scale by transition.animateFloat(
        transitionSpec = {
            if (false isTransitioningTo true) {
                // Dismissed to expanded
                tween(
                    durationMillis = 120,
                    easing = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
                )
            } else {
                // Expanded to dismissed.
                tween(
                    durationMillis = 1,
                    delayMillis = (75) - 1
                )
            }
        }, label = ""
    ) {
        if (it) {
            // Menu is expanded.
            1f
        } else {
            // Menu is dismissed.
            0.8f
        }
    }

    Column(
        modifier = modifier
            .graphicsLayer {
                shadowElevation = if (expanded) 8.dp.toPx() else 0.dp.toPx()
            }
            .width(IntrinsicSize.Max)
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colors.surface)
    ) {
        Row(
            modifier = Modifier
                .defaultMinSize(minWidth = 256.dp)
                .clickable { expanded = !expanded }
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Selected")
            Icon(imageVector = if(expanded) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowDropUp, contentDescription = "Dropdown Expand Toggle")
        }
        if(expanded) {
            Surface(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .width(IntrinsicSize.Max)
            ) {
                Column {
                    content(
                        Modifier
                            .clickable {

                            }
                            .fillMaxWidth()
                            .padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun Dropdown(
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    selectedIndex: Int = 0,
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val expandedStates = remember { MutableTransitionState(false) }
    expandedStates.targetState = expanded

    // TODO: Animations
    Column {

    }
}

//    var selectedIndex by remember { mutableStateOf(selectedIndex) }
//
//    Layout(
//        modifier = modifier,
//        content = { Column(content = content) },
//    ) { measurables, constraints ->
//        var height = 0
//        var maxWidth = 0
//
//        val placeables = measurables.map { measurable ->
//            val placeable = measurable.measure(constraints)
//
//            height += placeable.height
//            maxWidth = if(placeable.width > maxWidth) placeable.width else maxWidth
//
//            placeable
//        }
//
//        layout(maxWidth, height) {
//            var iheight = 0
//
//            placeables.forEachIndexed { index, placeable ->
//                placeable.placeRelative(
//                    x = 0,
//                    y = iheight
//                )
//                iheight += placeable.height
//            }
//        }
//    }

@Composable
fun DropdownItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    paddingValues: PaddingValues = PaddingValues(8.dp),
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .clickable(
                onClick = onClick
            )
            //.fillMaxWidth()
            .padding(16.dp, 8.dp)
    ) {
        ProvideTextStyle(value = MaterialTheme.typography.subtitle1) {
            content()
        }
    }
}