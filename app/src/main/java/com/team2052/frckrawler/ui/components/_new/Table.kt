package com.team2052.frckrawler.ui.components._new

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.ui.theme.spaceMedium
import com.team2052.frckrawler.ui.theme.spaceSmall
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun TableExample() {
    Table {
        tableRow(title = { Text(text = "Scouter #1") }, description = { Text(text = "Synced") }) {
            TableAction(onClick = {  }, backgroundColor = Color(0xFFDA4A4A)) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove")
            }
            TableAction(onClick = {  }, backgroundColor = Color(0xFF4972DA)) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
            }
        }
        tableRow(title = { Text(text = "Scouter #2") }, description = { Text(text = "Synced") })
        tableRow(title = { Text(text = "Scouter #3") }, description = { Text(text = "Synced") })
    }
}

@Composable
fun TableAction(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    backgroundColor: Color = Color.Transparent,
    content: @Composable () -> Unit,
) {
    var height by remember { mutableStateOf(0) }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .onGloballyPositioned { height = it.size.height }
            .width(height.dp)
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(LocalContentColor provides Color.White) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Table(
    modifier: Modifier = Modifier,
    table: TableBuilder.() -> Unit,
) {
    val tableBuilder = remember { TableBuilder().apply(table) }
    val table = remember { tableBuilder.build() }

    Column(modifier = modifier) {
        table.forEachIndexed { index, tableRow ->
            val context = rememberCoroutineScope()
            val swipeableState = rememberSwipeableState(0)

            var tableRowHeight by remember { mutableStateOf(0) }
            var tableRowWidth by remember { mutableStateOf(0) }

            Box(contentAlignment = Alignment.CenterEnd) {
                tableRow.actions?.let {
                    Row(
                        modifier = Modifier
                            .height(tableRowHeight.dp)
                            .onGloballyPositioned { tableRowWidth = it.size.width }
                            .clip(RoundedCornerShape(topEnd = 5.dp, bottomEnd = 5.dp))
                    ) {
                        it(index)
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
                        .shadow(((abs(swipeableState.offset.value) / tableRowWidth) * 4).dp)
                        .background(
                            LocalElevationOverlay.current?.apply(
                                color = MaterialTheme.colors.surface,
                                elevation = (((abs(swipeableState.offset.value) / 100f) * 2) + 1).dp
                            ) ?: Color.Transparent
                        )
                        .onGloballyPositioned { tableRowHeight = it.size.height }
                        .padding(spaceMedium)
                        .swipeable(
                            state = swipeableState,
                            anchors = mapOf(
                                0f to 0,
                                with(LocalDensity.current) { -100.dp.toPx() } to 1),
                            thresholds = { _, _ -> FractionalThreshold(1f) },
                            orientation = Orientation.Horizontal
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        ProvideTextStyle(MaterialTheme.typography.subtitle1) {
                            tableRow.title(index)
                        }
                        CompositionLocalProvider(
                            LocalTextStyle provides MaterialTheme.typography.body2,
                            LocalContentAlpha provides ContentAlpha.medium,
                        ) {
                            tableRow.description(index)
                        }
                    }

                    Icon(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable {
                                context.launch {
                                    swipeableState.animateTo(
                                        if (abs(swipeableState.offset.value) >= 100f) {
                                            0
                                        } else {
                                            1
                                        },
                                        tween(250),
                                    )
                                }
                            },
                        imageVector = if (abs(swipeableState.offset.value) >= 100f) {
                            Icons.Default.Close
                        } else {
                            Icons.Default.DragHandle
                        },
                        contentDescription = "Drag Handle",
                    )
                }
            }

            if (index != table.size - 1) {
                Divider(modifier = Modifier.padding(vertical = spaceSmall))
            }
        }
    }
}

data class TableRow(
    val title: @Composable (Int) -> Unit,
    val description: @Composable (Int) -> Unit,
    val actions: (@Composable RowScope.(Int) -> Unit)? = null,
)

open class TableBuilder {
    private val tableRows: MutableList<TableRow> = mutableListOf()

    fun tableRow(
        title: @Composable (Int) -> Unit,
        description: @Composable (Int) -> Unit,
        actions: (@Composable RowScope.(Int) -> Unit)? = null,
    ) {
        tableRows += TableRow(title, description, actions)
    }

    fun build() = tableRows
}