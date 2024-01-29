package com.team2052.frckrawler.ui.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

/*
 Modified from
 https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:compose/foundation/foundation/integration-tests/foundation-demos/src/main/java/androidx/compose/foundation/demos/LazyColumnDragAndDropDemo.kt
 */

@Composable
fun rememberDragDropState(
  lazyListState: LazyListState,
  onMove: (Int, Int) -> Unit,
  onDragEnded: () -> Unit,
): DragDropState {
  val scope = rememberCoroutineScope()
  val state = remember(lazyListState) {
    DragDropState(
      state = lazyListState,
      onMove = onMove,
      onDragEnded = onDragEnded,
      scope = scope
    )
  }
  LaunchedEffect(state) {
    while (true) {
      val diff = state.scrollChannel.receive()
      lazyListState.scrollBy(diff)
    }
  }
  return state
}

class DragDropState internal constructor(
  private val state: LazyListState,
  private val scope: CoroutineScope,
  private val onMove: (Int, Int) -> Unit,
  private val onDragEnded: () -> Unit,
) {
  var draggingItemKey by mutableStateOf<Any?>(null)
    private set

  internal val scrollChannel = Channel<Float>()

  private var draggingItemDraggedDelta by mutableFloatStateOf(0f)
  private var draggingItemInitialOffset by mutableIntStateOf(0)
  private val maxDragOffset: Int
    get() {
      val lastDraggableItem = state.layoutInfo.visibleItemsInfo.lastOrNull {
        it.contentType is DraggableContent
      }

      val lastItemOffset = lastDraggableItem?.offset ?: Int.MIN_VALUE
      return draggingItemLayoutInfo?.let {
        lastItemOffset - it.offset
      } ?: 0
    }
  private val minDragOffset: Int
    get() {
      val firstDraggableItem = state.layoutInfo.visibleItemsInfo.firstOrNull {
        it.contentType is DraggableContent
      }

      val firstItemOffset = firstDraggableItem?.offset ?: Int.MIN_VALUE
      return draggingItemLayoutInfo?.let {
        firstItemOffset - it.offset
      } ?: 0
    }

  internal val draggingItemOffset: Float
    get() {
      val offset = draggingItemLayoutInfo?.let { item ->
        draggingItemInitialOffset + draggingItemDraggedDelta - item.offset
      } ?: 0f

      return min(max(offset, minDragOffset.toFloat()), maxDragOffset.toFloat())
    }

  private val draggingItemLayoutInfo: LazyListItemInfo?
    get() = state.layoutInfo.visibleItemsInfo
      .firstOrNull { it.key == draggingItemKey }

  internal var previousKeyOfDraggedItem by mutableStateOf<Any?>(null)
    private set
  internal var previousItemOffset = Animatable(0f)
    private set

  internal fun onDragStart(offset: Offset) {
    state.layoutInfo.visibleItemsInfo
      .firstOrNull { item ->
        offset.y.toInt() in item.offset..(item.offset + item.size)
                && item.contentType is DraggableContent
      }?.also {
        draggingItemKey = it.key
        draggingItemInitialOffset = it.offset
      }
  }

  internal fun onDragHandleStart(key: Any) {
    draggingItemKey = key

    val item = state.layoutInfo.visibleItemsInfo.firstOrNull { item -> item.key == key }
    item?.let {
      draggingItemInitialOffset = it.offset
    }
  }

  internal fun onDragInterrupted() {
    if (draggingItemKey != null) {
      previousKeyOfDraggedItem = draggingItemKey
      val startOffset = draggingItemOffset
      scope.launch {
        previousItemOffset.snapTo(startOffset)
        previousItemOffset.animateTo(
          0f,
          spring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = 1f
          )
        )
        previousKeyOfDraggedItem = null
      }
    }
    draggingItemDraggedDelta = 0f
    draggingItemKey = null
    draggingItemInitialOffset = 0
  }

  internal fun onDragEnded() {
    onDragInterrupted()
    onDragEnded.invoke()
  }

  internal fun onDrag(offset: Offset) {
    draggingItemDraggedDelta += offset.y

    val draggingItem = draggingItemLayoutInfo ?: return
    val startOffset = draggingItem.offset + draggingItemOffset
    val endOffset = startOffset + draggingItem.size
    val middleOffset = startOffset + (endOffset - startOffset) / 2f

    val targetItem = state.layoutInfo.visibleItemsInfo.find { item ->
      middleOffset.toInt() in item.offset..item.offsetEnd &&
              draggingItem.index != item.index
              && item.contentType is DraggableContent
    }

    if (targetItem != null) {
      val scrollToIndex = if (targetItem.index == state.firstVisibleItemIndex) {
        draggingItem.index
      } else if (draggingItem.index == state.firstVisibleItemIndex) {
        targetItem.index
      } else {
        null
      }

      val fromIndex = (draggingItem.contentType as? DraggableContent)?.itemPositionInList
      val toIndex = (targetItem.contentType as? DraggableContent)?.itemPositionInList
      if (fromIndex != null && toIndex != null) {
        if (scrollToIndex != null) {
          scope.launch {
            // this is needed to neutralize automatic keeping the first item first.
            state.scrollToItem(scrollToIndex, state.firstVisibleItemScrollOffset)
            onMove.invoke(fromIndex, toIndex)
          }
        } else {
          onMove.invoke(fromIndex, toIndex)
        }
      }
    } else {
      val overscroll = when {
        draggingItemDraggedDelta > 0 ->
          (endOffset - state.layoutInfo.viewportEndOffset).coerceAtLeast(0f)
        draggingItemDraggedDelta < 0 ->
          (startOffset - state.layoutInfo.viewportStartOffset).coerceAtMost(0f)
        else -> 0f
      }
      if (overscroll != 0f) {
        scrollChannel.trySend(overscroll)
      }
    }
  }

  private val LazyListItemInfo.offsetEnd: Int
    get() = this.offset + this.size
}

fun Modifier.dragContainer(dragDropState: DragDropState): Modifier {
  return this.pointerInput(dragDropState) {
    detectDragGesturesAfterLongPress(
      onDrag = { change, offset ->
        change.consume()
        dragDropState.onDrag(offset = offset)
      },
      onDragStart = { offset -> dragDropState.onDragStart(offset) },
      onDragEnd = { dragDropState.onDragEnded() },
      onDragCancel = { dragDropState.onDragInterrupted() }
    )
  }
}

fun Modifier.dragHandle(dragDropState: DragDropState, key: Any): Modifier {
  return this.pointerInput(dragDropState) {
    detectDragGestures(
      onDrag = { change, offset ->
        change.consume()
        dragDropState.onDrag(offset = offset)
      },
      onDragStart = { _ -> dragDropState.onDragHandleStart(key) },
      onDragEnd = { dragDropState.onDragInterrupted() },
      onDragCancel = { dragDropState.onDragInterrupted() }
    )
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyItemScope.DraggableItem(
  dragDropState: DragDropState,
  key: Any,
  modifier: Modifier = Modifier,
  content: @Composable BoxScope.(isDragging: Boolean) -> Unit
) {
  val dragging = key == dragDropState.draggingItemKey
  val draggingModifier = if (dragging) {
    Modifier
      .zIndex(1f)
      .graphicsLayer {
        translationY = dragDropState.draggingItemOffset
      }
  } else if (key == dragDropState.previousKeyOfDraggedItem) {
    Modifier.zIndex(1f)
      .graphicsLayer {
        translationY = dragDropState.previousItemOffset.value
      }
  } else {
    Modifier.animateItemPlacement()
  }
  Box(modifier = modifier.then(draggingModifier)) {
    content(dragging)
  }
}

/**
 * Use with as the content type for LazyList items to indicate that the item is draggable
 */
interface DraggableContent {
  /**
   * Position of this item in its list. This may be different from its position
   * in the LazyList due to headers or other intermediate items
   */
  val itemPositionInList: Int
}

/**
 * Basic implementation of [DraggableContent]
 */
class BasicDraggableContent(
  override val itemPositionInList: Int
) : DraggableContent