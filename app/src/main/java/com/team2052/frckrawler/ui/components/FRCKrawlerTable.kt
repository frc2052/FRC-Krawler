package com.team2052.frckrawler.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.ui.theme.borderWidth
import timber.log.Timber

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

@Composable
fun DataTable(
  modifier: Modifier = Modifier,
  selectable: Boolean = true,
  onSelectionChanged: (List<Boolean>) -> Unit = { },
  builder: DataTableBuilder.() -> Unit,
) {
  val dataTableBuilder = remember {
    DataTableBuilder().apply(builder)
  }
  val dataTable = remember {
    dataTableBuilder.build()
  }

  if (dataTable.header == null && dataTable.rows.isEmpty()) return

  var headerSelected by remember { mutableStateOf(false) }
  var rowSelections by remember { mutableStateOf(List(dataTable.rows.size) { false }) }

  val header = if (dataTable.header != null) {
    val headerRowScope = DataTableRowScope().apply(dataTable.header)
    headerRowScope.items.toList()
  } else null

  val rowCount = dataTable.rows.size + if (dataTable.header != null) 1 else 0

  val rows = mutableListOf<List<@Composable () -> Unit>>()
  dataTable.rows.forEach {
    val rowScope = DataTableRowScope().apply(it)
    rows += rowScope.items.toList()
  }

  val columnCount = header?.size ?: rows[0].size
  for (row in rows) {
    if (row.size != columnCount) throw Exception("Unmatched row item counts in DataTable!")
  }

  val columns = List<MutableList<@Composable () -> Unit>>(columnCount) { mutableListOf() }
  for (index in columns.indices) {
    if (header != null) columns[index] += header[index]
    rows.forEach { row ->
      columns[index] += row[index]
    }
  }

  Layout(
    modifier = modifier,
    content = {
      if (selectable) {
        repeat(rowCount) { index ->
          Checkbox(
            modifier = Modifier
              .padding(horizontal = 16.dp)
              .height(56.dp),
            checked = (index != 0 && rowSelections[index - 1]) || headerSelected,
            onCheckedChange = { state ->
              if (index == 0) {
                headerSelected = state
                rowSelections = List(dataTable.rows.size) { state }
              } else {
                val mutableRowSelections = rowSelections.toMutableList()
                mutableRowSelections[index - 1] = state
                rowSelections = mutableRowSelections
              }
              onSelectionChanged(if (header != null) rowSelections + headerSelected else rowSelections)
            }
          )
        }
      }

      columns.forEach { column ->
        var startingIndex = 0
        if (header != null) {
          Box(
            Modifier
              .height(56.dp)
              .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
          ) {
            ProvideTextStyle(LocalTextStyle.current.copy(fontWeight = FontWeight.SemiBold)) {
              column[0]()
            }
          }
          startingIndex = 1
        }
        for (index in startingIndex until column.size) {
          Box(
            Modifier
              .height(56.dp)
              .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
          ) {
            column[index]()
          }
        }
      }

      repeat(rowCount - 1) {
        Divider(
          modifier = Modifier.fillMaxWidth(),
          color = borderColor,
          thickness = borderWidth
        )
      }
    }
  ) { measurables, constraints ->
    val placeables: List<Placeable> = measurables.map { it.measure(constraints) }
    val selectorPlaceables = placeables.subList(0, rowCount)
    val itemPlaceables =
      placeables.subList(if (selectable) rowCount else 0, placeables.size - (rowCount - 1))
    val dividerPlaceables = placeables.subList(placeables.size - (rowCount - 1), placeables.size)

    Timber.d("DataTable PLACEABLES: ${placeables.size}; SELECTORS: ${selectorPlaceables.size}; ITEMS: ${itemPlaceables.size}; DIVIDERS: ${dividerPlaceables.size};")

    val checkboxWidth = if (selectable) selectorPlaceables[0].width else 0

    // Lists containing the width and height of each column
    val columnHeights = MutableList(columns.size) { 0 }
    val columnWidths = MutableList(columns.size) { 0 }

    var columnIndex = -1
    itemPlaceables.forEachIndexed { index, placeable ->

      // Increments the column index
      if ((index) % rowCount == 0) columnIndex++

      // Add the height of the placeable to the total column height
      columnHeights[columnIndex] += placeable.height

      // Finds the maximum placeable width and sets the columns width accordingly
      val columnWidth = columnWidths[columnIndex]
      columnWidths[columnIndex] =
        if (placeable.width > columnWidth) placeable.width else columnWidth

    }

    // Calculate the maximum row height to use for the layout dimensions
    val layoutHeight = columnHeights.maxOf { it } + (borderWidth.value.toInt() * rows.size)

    // Identify the maximum column width to use for filling white space in the table
    val maxColumnWidth = columnWidths.maxOf { it }
    columnWidths[columnWidths.indexOf(maxColumnWidth)] =
      constraints.maxWidth - checkboxWidth - columnWidths.sumOf {
        if (maxColumnWidth != it) it else 0
      }

    layout(constraints.maxWidth, layoutHeight) {
      var xOffset = 0
      var yOffset = 0

      if (selectable) {
        selectorPlaceables.forEachIndexed { index, placeable ->
          placeable.placeRelative(xOffset, yOffset)
          yOffset += if (index == 0) {
            56
          } else {
            52
          } + borderWidth.value.toInt()
        }
      }

      // Places each placeable with the correct offset defined by the column widths and heights
      xOffset = checkboxWidth
      yOffset = 0
      itemPlaceables.forEachIndexed { index, placeable ->
        placeable.placeRelative(xOffset, yOffset)
        yOffset += if (index % rowCount == 0) {
          56
        } else {
          52
        } + borderWidth.value.toInt()
        if ((index + 1) % rowCount == 0) {
          yOffset = 0
          xOffset += columnWidths[(index / rows.size) - 1]
        }
      }

      yOffset = 0
      dividerPlaceables.forEachIndexed { index, placeable ->
        yOffset += if (index == 0) {
          56
        } else {
          52 + borderWidth.value.toInt()
        }
        placeable.placeRelative(0, yOffset)
      }
    }
  }
}

data class DataTable(
  val header: (DataTableRowScope.() -> Unit)?,
  val rows: List<DataTableRowScope.() -> Unit>,
)

open class DataTableBuilder {
  private var header: (DataTableRowScope.() -> Unit)? = null
  private val rows: MutableList<DataTableRowScope.() -> Unit> = mutableListOf()

  fun header(content: DataTableRowScope.() -> Unit) {
    header = content
  }

  fun row(content: DataTableRowScope.() -> Unit) {
    rows += content
  }

  fun rows(count: Int, content: DataTableRowScope.(Int) -> Unit) {
    for (index in 0 until count) {
      row { content(index) }
    }
  }

  fun build() = DataTable(header = header, rows = rows.toList())
}

open class DataTableRowScope {
  val items: MutableList<@Composable () -> Unit> = mutableListOf()

  fun item(text: String) = item { Text(text) }

  fun item(content: @Composable () -> Unit) {
    items += content
  }
}

private val borderColor = Color(0xFFC6C6C6)
