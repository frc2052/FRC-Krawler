package com.team2052.frckrawler.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp

/**
 * TODO: USE A CUSTOM LAYOUT TO CREATE TABLE
 * This would use fixed sizes define as a constant to
 * position elements within the table (Table Row)
 */

@Composable
fun Table(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) = Column { content() }

@Composable
fun TableHeader(
    modifier: Modifier = Modifier,
    items: Int,
    checkable: Boolean = false,
    content: @Composable () -> Unit,
) {
    ProvideTextStyle(MaterialTheme.typography.body1) {
        TableRow(
            modifier = modifier,
            items = items,
            checkable = checkable,
            content = content,
        )
    }
}

@Composable
fun TableRow(
    modifier: Modifier = Modifier,
    items: Int,
    checkable: Boolean = false,
    draggable: Boolean = false,
    draggedIcons: @Composable RowScope.() -> Unit = { },
    content: @Composable () -> Unit,
) {
    /**
     * https://material.io/components/data-tables#anatomy
     * Each item within the row will have 16dp between them
     */

    Layout(
        modifier = modifier.padding(16.dp).background(Color.Red),
        content = content,
    ) { measurables, constraints ->

        /**
         * 1. Measure the total height of all the items
         * 2. Measure the max width of the layout and divide by item count
         *  subtracting the width of the checked icon
         * 3.
         */

        val placeables = measurables.mapIndexed { index, measurable ->
            val placeable = measurable.measure(constraints)
            placeable
        }

        val itemWidth = constraints.maxWidth / items
        val height = placeables.maxOf { it.height }
        /*
        val height = placeables.sumBy { it.height }.coerceIn(
            constraints.minHeight.rangeTo(constraints.maxHeight)
        )
        */

        layout(constraints.maxWidth, height) {
            placeables.forEachIndexed { index, placeable ->
                placeable.placeRelative(index * itemWidth, height - placeable.height)
            }
        }
    }
}

@Composable
fun FRCKrawlerTable(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) = Column { content() }

@Composable
fun FRCKrawlerTableRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
    ) {
        content()
    }
}

@Composable
fun FRCKrawlerTableRowEntry(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
    ) {
        content()
    }
}