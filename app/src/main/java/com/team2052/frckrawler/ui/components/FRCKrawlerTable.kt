package com.team2052.frckrawler.ui.components

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * TABLE GUIDE LINE
 *
 * Each Column must have the same width and height.
 * Every Cell in a Row must have the same width.
 * The largest Row will expand to fill any white space within the table.
 * The table header height will be 56dp and all other rows will be 52dp.
 * Each Cell (excluding checkboxes) will have 16dp of horizontal padding.
 * Overflow is truncated with an ellipsis.
 * Clicking on overflow reveals tool tip with full text.
 */

private const val borderWidth = 1
private val borderColor = Color(0xFFC6C6C6)

@Composable
fun FRCKrawlerDataTable(
    modifier: Modifier = Modifier,
    dataTableSource: TableSource,
    onCheckedChange: ((Int, Boolean) -> Unit)? = null,
) {
    val columns = dataTableSource.header.items
    val rows = dataTableSource.rows

    val selectable = onCheckedChange != null

    Layout(
        modifier = modifier,
        content = {
            // Creates datatable checkboxes
            if (selectable) {
                val checkboxModifier = Modifier.padding(horizontal = 16.dp)
                Checkbox(
                    modifier = checkboxModifier
                        .height(56.dp),
                    checked = dataTableSource.header.checked,
                    onCheckedChange = { checked ->
                        dataTableSource.header.checked = checked
                        onCheckedChange!!(0, dataTableSource.header.checked)
                        rows.forEachIndexed { index, dataRow ->
                            dataRow.checked = checked
                            onCheckedChange!!(index + 1, dataRow.checked)
                        }
                    },
                )
                rows.forEachIndexed { index, dataRow ->
                    Checkbox(
                        modifier = checkboxModifier
                            .height(52.dp),
                        checked = dataRow.checked,
                        onCheckedChange = { checked ->
                            dataRow.checked = checked
                            onCheckedChange!!(index + 1, dataRow.checked)
                            if (!checked) {
                                onCheckedChange!!(0, false)
                            }
                        },
                    )
                }
            }

            // Creates datatable items
            for (columnIndex in columns.indices) {
                // Column header
                Box(
                    Modifier
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    ProvideTextStyle(LocalTextStyle.current.copy(fontWeight = FontWeight.SemiBold)) {
                        columns[columnIndex](Modifier)
                    }
                }

                // Column items
                rows.forEachIndexed { index, dataRow ->
                    // Get the current column item from the row
                    Box(
                        Modifier
                            .height(52.dp)
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        dataRow.items[columnIndex](dataRow.modifier)
                    }
                }
            }

            // Creates row dividers
            // The dividers will be placed properly after the table is created
            repeat(dataTableSource.rows.size) {
                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    color = borderColor,
                    thickness = borderWidth.dp
                )
            }
        },
    ) { measurables, constraints ->

        // Number of rows (including the header)
        val rowCount = rows.size + 1

        val placeables: List<Placeable> = measurables.map { it.measure(constraints) }
        val selectorPlaceables = placeables.subList(0, rowCount)
        val itemPlaceables = placeables.subList(rowCount, placeables.size - rows.size)
        val dividerPlaceables = placeables.subList(placeables.size - rows.size, placeables.size)

        val checkboxWidth = if (selectable) selectorPlaceables[0].width else 0

        // Lists containing the width and height of each column
        val columnHeights: MutableList<Int> = MutableList(columns.size) { 0 }
        val columnWidths: MutableList<Int> = MutableList(columns.size) { 0 }

        // Cycle through each placeable and calculate it's effect on it's columns width and height
        var columnIndex = -1
        itemPlaceables.forEachIndexed { index, placeable ->
            // Increments the column index
            if (index % rowCount == 0) {
                columnIndex++
            }

            // Add the height of the placeable to the total column height
            columnHeights[columnIndex] += placeable.height

            // Finds the maximum placeable width and sets the columns width accordingly
            val columnWidth = columnWidths[columnIndex]
            columnWidths[columnIndex] = if (placeable.width > columnWidth) placeable.width else columnWidth
        }

        // Calculate the maximum row height to use for the layout dimensions
        val layoutHeight = columnHeights.maxOf { it } + (borderWidth * rows.size)

        // Identify the maximum column width to use for filling white space in the table
        val maxColumnWidth = columnWidths.maxOf { it }
        columnWidths[columnWidths.indexOf(maxColumnWidth)] = constraints.maxWidth - checkboxWidth - columnWidths.sumOf {
            if (maxColumnWidth != it) it else 0
        }

        layout(constraints.maxWidth, layoutHeight) {
            var xOffset = 0
            var yOffset = 0

            selectorPlaceables.forEachIndexed { index, placeable ->
                placeable.placeRelative(xOffset, yOffset)
                yOffset += if (index == 0) { 56 } else { 52 } + borderWidth
            }

            // Places each placeable with the correct offset defined by the column widths and heights
            xOffset = checkboxWidth
            yOffset = 0
            itemPlaceables.forEachIndexed { index, placeable ->
                placeable.placeRelative(xOffset, yOffset)
                yOffset += if (index % rowCount == 0) { 56 } else { 52 } + borderWidth
                if ((index + 1) % rowCount == 0) {
                    yOffset = 0
                    xOffset += columnWidths[(index / rows.size) - 1]
                }
            }

            yOffset = 0
            dividerPlaceables.forEachIndexed { index, placeable ->
                yOffset += if (index == 0) { 56 } else { 52 + borderWidth }
                placeable.placeRelative(0, yOffset)
            }
        }
    }
}

class TableSource(
    val header: TableRow,
    vararg rows: TableRow,
) {
    val rows = rows.map { it }
}

class TableRow(
    vararg items: @Composable (Modifier) -> Unit,
    val modifier: Modifier = Modifier,
    var checked: Boolean = false,
) {
    val items = items.map { it }
}
