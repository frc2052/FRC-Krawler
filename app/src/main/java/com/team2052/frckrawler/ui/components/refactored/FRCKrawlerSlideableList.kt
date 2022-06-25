package com.team2052.frckrawler.ui.components.refactored

import android.inputmethodservice.Keyboard
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.SwipeableDefaults.resistanceConfig
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.ui.theme.spaceMedium
import com.team2052.frckrawler.ui.theme.spaceSmall
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

private val LocalItemPadding = compositionLocalOf { PaddingValues(0.dp) }

@Composable
fun SwipeListTest() {
    val events = listOf(
        Event("Northern Lights Regional", "Infinite Recharge"),
        Event("10,000 Lakes Regional", "Infinite Recharge")
    )

    SwipeList {
        items(events) { event ->
            SwipeListItem(
                primaryText = { Text(event.name) },
                secondaryText = { Text(event.game) },
                icons = {
                    SwipeListItemIcon(backgroundColor = Color.Yellow) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "")
                    }
                    SwipeListItemIcon(backgroundColor = Color.Red) {
                        Icon(imageVector = Icons.Default.Recycling, contentDescription = "")
                    }
                }
            )
        }
    }
}

private data class Event(
    val name: String,
    val game: String,
)

@Composable
fun SwipeList(
    modifier: Modifier = Modifier,
    builder: SwipeListBuilder.() -> Unit,
) {
    val swipeListBuilder = remember {
        SwipeListBuilder().apply(builder)
    }

    LazyColumn(modifier = Modifier) {
        val items = swipeListBuilder.build()

        itemsIndexed(items) { index, item ->
            Row {
                CompositionLocalProvider(
                    LocalItemPadding provides PaddingValues(
                        top = if (index != 0) spaceMedium else 0.dp,
                        bottom = if (index != items.size - 1) spaceMedium else 0.dp
                    )
                ) {
                    item()
                }
            }
            if (index != items.size - 1) {
                Divider(
                    modifier = Modifier.padding(vertical = spaceSmall),
                    color = Color(0xFF2f2f2f),
                    thickness = 1.dp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, androidx.compose.animation.ExperimentalAnimationApi::class)
@Composable
fun SwipeListItem(
    modifier: Modifier = Modifier,
    primaryText: (@Composable RowScope.() -> Unit)? = null,
    secondaryText: (@Composable RowScope.() -> Unit)? = null,
    icons: @Composable RowScope.() -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val swipeableState = rememberSwipeableState(0)
    val anchors = mapOf(0f to 0, -100f to 1)
    var opened by remember { mutableStateOf(false) }

    opened = abs(swipeableState.offset.value) > 0

    Row(
        modifier = modifier
            .background(Color.Red)
            .fillMaxWidth()
            .padding(LocalItemPadding.current)
            .padding(horizontal = spaceSmall)
            .background(Color.Blue)
            .offset(swipeableState.offset.value.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            primaryText?.let { primaryText ->
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.SemiBold),
                ) {
                    Row { primaryText() }
                }
            }
            secondaryText?.let { secondaryText ->
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.body2,
                    LocalContentAlpha provides ContentAlpha.medium,
                ) {
                    Row { secondaryText() }
                }
            }
        }
        Icon(
            modifier = Modifier
                .swipeable(
                    state = swipeableState,
                    anchors = anchors,
                    orientation = Orientation.Horizontal,
                    thresholds = { _, _ -> FractionalThreshold(1f) },
                    resistance = resistanceConfig(anchors.keys, 20f, 10f),
                )
                .clickable {
                    if (opened) {
                        coroutineScope.launch {
                            swipeableState.snapTo(0)
                        }
                    }
                },
            imageVector = when (opened) {
                true -> Icons.Default.Close
                false -> Icons.Default.DragHandle
            },
            contentDescription = "Drag Handle",
        )
    }
}

@Composable
fun SwipeListItemIcon(
    backgroundColor: Color,
    content: @Composable () -> Unit,
) {

}

open class SwipeListBuilder {
    private val listItems: MutableList<@Composable RowScope.() -> Unit> = mutableListOf()

    fun <T> items(items: List<T>, itemContent: @Composable RowScope.(T) -> Unit) {
        items.forEach { item(it, itemContent) }
    }

    fun <T> item(item: T, itemContent: @Composable RowScope.(T) -> Unit) {
        listItems += { Row { itemContent(item) } }
    }

    fun build() = listItems.toList()
}