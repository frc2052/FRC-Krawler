package com.team2052.frckrawler.ui.event.add

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.remote.model.TbaSimpleEvent
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import com.team2052.frckrawler.ui.components.fields.FRCKrawlerDropdown
import com.team2052.frckrawler.ui.components.fields.FRCKrawlerTextField
import com.team2052.frckrawler.ui.components.fields.buildHighlightedText
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import dev.zacsweers.metrox.viewmodel.metroViewModel
import java.time.Year
import kotlinx.coroutines.launch

@Composable
fun AddEventSheetContent(
  gameId: Int,
  onClose: () -> Unit
) {
  val scope = rememberCoroutineScope()
  val viewModel: AddEventViewModel = metroViewModel()
  val state = viewModel.state.collectAsState()

  LaunchedEffect(true) {
    viewModel.loadEventsForYear(Year.now().value)
  }

  AddEventSheetLayout(
    state = state.value,
    onSaveAutoEvent = { event ->
      scope.launch {
        viewModel.saveAutoEvent(gameId, event)
        onClose()
      }
    },
    onSaveManualEvent = { name ->
      scope.launch {
        viewModel.saveManualEvent(gameId, name)
        onClose()
      }
    },
    onYearSelected = { viewModel.loadEventsForYear(it) },
    onClose = onClose,
  )
}

@Composable
private fun AddEventSheetLayout(
  state: AddEventScreenState,
  onSaveAutoEvent: (TbaSimpleEvent) -> Unit,
  onSaveManualEvent: (String) -> Unit,
  onYearSelected: (Int) -> Unit,
  onClose: () -> Unit
) {
  var selectedTabIndex by remember { mutableIntStateOf(0) }
  var autoEventSelectedYear: Int by remember { mutableIntStateOf(Year.now().value) }
  var autoEventSelectedEvent: TbaSimpleEvent? by remember { mutableStateOf(null) }
  val manualEventName = rememberTextFieldState()

  val saveEnabled by remember {
    derivedStateOf {
      val validAutoEvent = selectedTabIndex == 0 && autoEventSelectedYear != 0 && autoEventSelectedEvent != null
      val validManualEvent = selectedTabIndex == 1 && manualEventName.text.isNotBlank()
      (validAutoEvent || validManualEvent) && !state.isSavingEvent
    }
  }

  Column(
    modifier = Modifier.imePadding()
  ) {
    Text(
      modifier = Modifier.padding(horizontal = 16.dp),
      text = stringResource(R.string.add_event_sheet_title),
      style = MaterialTheme.typography.titleLarge
    )

    Spacer(Modifier.height(12.dp))

    AddEventTabs(
      selectedTabIndex = selectedTabIndex,
      onTabSelected = { selectedTabIndex = it }
    )
    HorizontalDivider(color = MaterialTheme.colorScheme.primary)

    Box(
      modifier = Modifier.weight(1f, fill = false)
    ) {
      when (selectedTabIndex) {
        0 -> AutoEventEntry(
          years = state.years,
          selectedYear = autoEventSelectedYear,
          onYearSelected = {
            autoEventSelectedYear = it
            autoEventSelectedEvent = null
            onYearSelected(it)
          },
          events = state.events,
          selectedEvent = autoEventSelectedEvent,
          onEventSelected = { autoEventSelectedEvent = it },
          areEventsLoading = state.areEventsLoading,
          hasEventNetworkError = state.hasNetworkError,
          isSavingEvent = state.isSavingEvent,
          onRetryFetchEvents = {
            onYearSelected(autoEventSelectedYear)
          }
        )

        1 -> ManualEventEntry(
          name = manualEventName,
          isSavingEvent = state.isSavingEvent,
        )
      }
    }

    Row(
      horizontalArrangement = Arrangement.End
    ) {
      TextButton(
        modifier = Modifier.padding(12.dp),
        enabled = !state.isSavingEvent,
        onClick = onClose
      ) {
        Text(stringResource(R.string.cancel).uppercase())
      }
      TextButton(
        modifier = Modifier.padding(12.dp),
        enabled = saveEnabled,
        onClick = {
          when (selectedTabIndex) {
            0 -> autoEventSelectedEvent?.let { onSaveAutoEvent(it) }
            1 -> onSaveManualEvent(manualEventName.text.toString())
          }
        }
      ) {
        Text(stringResource(R.string.save).uppercase())
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEventTabs(
  selectedTabIndex: Int,
  onTabSelected: (Int) -> Unit,
) {
  SecondaryTabRow(
    selectedTabIndex = selectedTabIndex,
    contentColor = contentColorFor(BottomSheetDefaults.ContainerColor),
    containerColor = BottomSheetDefaults.ContainerColor
  ) {
    Tab(
      selected = selectedTabIndex == 0,
      onClick = { onTabSelected(0) },
      text = { Text(stringResource(R.string.add_event_tab_auto)) }
    )
    Tab(
      selected = selectedTabIndex == 1,
      onClick = { onTabSelected(1) },
      text = { Text(stringResource(R.string.add_event_tab_manual)) }
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AutoEventEntry(
  years: List<Int>,
  selectedYear: Int,
  onYearSelected: (Int) -> Unit,
  events: List<TbaSimpleEvent>,
  selectedEvent: TbaSimpleEvent?,
  onEventSelected: (TbaSimpleEvent) -> Unit,
  areEventsLoading: Boolean,
  isSavingEvent: Boolean,
  hasEventNetworkError: Boolean,
  onRetryFetchEvents: () -> Unit
) {
  val focusManager = LocalFocusManager.current
  var isEventExpanded by remember { mutableStateOf(false) }
  var isEditing by remember(selectedEvent) { mutableStateOf(false) }
  var showEventError by remember { mutableStateOf(false) }
  val eventFieldState = remember(selectedEvent) { TextFieldState(selectedEvent?.name ?: "") }
  val eventsEnabled = !areEventsLoading && !hasEventNetworkError

  val collapseEventList = {
    isEventExpanded = false
    isEditing = false
    eventFieldState.setTextAndPlaceCursorAtEnd(selectedEvent?.name ?: "")
    showEventError = selectedEvent == null
    focusManager.clearFocus()
  }

  BackHandler(enabled = isEventExpanded) {
    collapseEventList()
  }

  val filterQuery = if (isEditing) eventFieldState.text.toString() else ""
  val filteredEvents = remember(events, filterQuery) {
    val trimmed = filterQuery.trim()
    if (trimmed.isEmpty()) {
      events
    } else {
      events.filter { event ->
        event.name.contains(trimmed, ignoreCase = true)
      }
    }
  }

  Box(
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
  ) {
    AnimatedContent(isSavingEvent, label = "autoEntryContent") { isSaving ->
      if (isSaving) {
        SavingEvent()
      } else {
        Column {
          AnimatedVisibility(visible = !isEventExpanded) {
            FRCKrawlerDropdown(
              value = selectedYear,
              getLabel = { it.toString() },
              onValueChange = { selected -> selected?.let { onYearSelected(it) } },
              label = stringResource(R.string.add_event_year_label),
              dropdownItems = years
            )
          }

          Row(
            verticalAlignment = Alignment.CenterVertically
          ) {
            FRCKrawlerTextField(
              state = eventFieldState,
              inputTransformation = InputTransformation {
                if (!asCharSequence().contentEquals(eventFieldState.text)) {
                  isEditing = true
                  isEventExpanded = true
                }
              },
              onFocusChange = { focused ->
                if (focused && eventsEnabled) {
                  isEventExpanded = true
                } else if (!focused && !isEventExpanded) {
                  showEventError = selectedEvent == null
                }
              },
              icon = {
                IconButton(
                  enabled = eventsEnabled,
                  onClick = {
                    if (isEventExpanded) {
                      collapseEventList()
                    } else {
                      isEventExpanded = true
                    }
                  }
                ) {
                  ExposedDropdownMenuDefaults.TrailingIcon(expanded = isEventExpanded)
                }
              },
              validity = !showEventError,
              isError = { validity -> !validity },
              label = stringResource(R.string.add_event_event_label),
              enabled = eventsEnabled,
            )

            Spacer(Modifier.width(8.dp))

            if (areEventsLoading) {
              CircularProgressIndicator(
                modifier = Modifier.size(36.dp)
              )
            } else if (hasEventNetworkError) {
              IconButton(
                modifier = Modifier.size(36.dp),
                onClick = onRetryFetchEvents
              ) {
                Icon(
                  imageVector = Icons.Default.Refresh,
                  contentDescription = stringResource(R.string.add_event_retry_fetch_events)
                )
              }
            }
          }

          AnimatedVisibility(
            visible = isEventExpanded && eventsEnabled,
            modifier = Modifier.weight(1f, fill = false),
          ) {
            Surface(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
              shape = MaterialTheme.shapes.extraSmall,
              color = MenuDefaults.containerColor,
              tonalElevation = MenuDefaults.TonalElevation,
              shadowElevation = MenuDefaults.ShadowElevation,
            ) {
              if (filteredEvents.isEmpty()) {
                DropdownMenuItem(
                  text = {
                    Text(
                      text = stringResource(R.string.no_matching_events),
                      style = MaterialTheme.typography.bodyMedium,
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                  },
                  enabled = false,
                  onClick = {},
                )
              } else {
                val listState = rememberLazyListState()
                val scrollbarColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                val consumeVerticalOverscroll = remember {
                  object : NestedScrollConnection {
                    override fun onPostScroll(
                      consumed: Offset,
                      available: Offset,
                      source: NestedScrollSource,
                    ): Offset = available.copy(x = 0f)

                    override suspend fun onPostFling(
                      consumed: Velocity,
                      available: Velocity,
                    ): Velocity = available.copy(x = 0f)
                  }
                }
                LazyColumn(
                  state = listState,
                  modifier = Modifier
                    .fillMaxWidth()
                    .nestedScroll(consumeVerticalOverscroll)
                    .verticalScrollbar(
                      state = listState,
                      color = scrollbarColor,
                    )
                ) {
                  items(filteredEvents, key = { it.key }) { event ->
                    DropdownMenuItem(
                      text = {
                        if (filterQuery.trim().isNotEmpty()) {
                          Text(text = buildHighlightedText(event.name, filterQuery))
                        } else {
                          Text(event.name)
                        }
                      },
                      onClick = {
                        onEventSelected(event)
                        eventFieldState.setTextAndPlaceCursorAtEnd(event.name)
                        showEventError = false
                        isEditing = false
                        isEventExpanded = false
                        focusManager.clearFocus()
                      },
                    )
                  }
                }
              }
            }
          }

          if (hasEventNetworkError) {
            Spacer(Modifier.height(8.dp))
            Text(
              text = stringResource(R.string.add_event_network_error),
              color = MaterialTheme.colorScheme.error
            )
          }
        }
      }
    }
  }
}

private fun Modifier.verticalScrollbar(
  state: LazyListState,
  color: Color,
  width: Dp = 4.dp,
  endPadding: Dp = 2.dp,
  verticalPadding: Dp = 4.dp,
  minThumbHeight: Dp = 24.dp,
): Modifier = drawWithContent {
  drawContent()

  val layoutInfo = state.layoutInfo
  val visibleItems = layoutInfo.visibleItemsInfo
  val totalItemsCount = layoutInfo.totalItemsCount
  if (visibleItems.isEmpty() || totalItemsCount <= visibleItems.size) {
    return@drawWithContent
  }

  val averageItemSize = visibleItems.sumOf { it.size }.toFloat() / visibleItems.size
  val estimatedTotalSize = averageItemSize * totalItemsCount
  val viewportHeight = size.height
  val trackHeight = (viewportHeight - verticalPadding.toPx() * 2).coerceAtLeast(0f)
  if (trackHeight <= 0f || estimatedTotalSize <= viewportHeight) {
    return@drawWithContent
  }

  val thumbHeight = (trackHeight * (viewportHeight / estimatedTotalSize))
    .coerceIn(minThumbHeight.toPx().coerceAtMost(trackHeight), trackHeight)

  val firstItem = visibleItems.first()
  val scrolledOffset = firstItem.index * averageItemSize - firstItem.offset
  val maxScrollOffset = (estimatedTotalSize - viewportHeight).coerceAtLeast(1f)
  val scrollFraction = (scrolledOffset / maxScrollOffset).coerceIn(0f, 1f)

  val thumbWidthPx = width.toPx()
  val thumbOffsetY = verticalPadding.toPx() + scrollFraction * (trackHeight - thumbHeight)
  val thumbOffsetX = size.width - thumbWidthPx - endPadding.toPx()

  drawRoundRect(
    color = color,
    topLeft = Offset(thumbOffsetX, thumbOffsetY),
    size = Size(thumbWidthPx, thumbHeight),
    cornerRadius = CornerRadius(thumbWidthPx / 2f),
  )
}

@Composable
private fun ManualEventEntry(
  name: TextFieldState,
  isSavingEvent: Boolean,
) {
  var hasHadFocus by remember { mutableStateOf(false) }
  Column(
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
  ) {

    AnimatedContent(isSavingEvent, label = "manualEventContent") { isSaving ->
      if (isSaving) {
        SavingEvent()
      } else {
        FRCKrawlerTextField(
          state = name,
          isError = { hasHadFocus && name.text.isBlank() },
          onFocusChange = { focused -> if (!hasHadFocus && focused) hasHadFocus = true },
          label = stringResource(R.string.add_event_name_label)
        )
      }
    }
  }
}

@Composable
private fun SavingEvent(
  modifier: Modifier = Modifier
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier.fillMaxWidth()
      .padding(16.dp)
  ) {
    CircularProgressIndicator(
      modifier = Modifier.size(64.dp)
    )
    Spacer(Modifier.height(8.dp))
    Text(stringResource(R.string.add_event_saving))
  }
}

@FrcKrawlerPreview
@Composable
private fun AddEventPreview() {
  val state = AddEventScreenState(
    years = listOf(2023, 2022),
    events = listOf(
      TbaSimpleEvent(
        key = "2023mnmi",
        name = "10,000 Lakes Regional"
      )
    ),
    areEventsLoading = false,
    hasNetworkError = false,
  )
  FrcKrawlerTheme {
    Surface {
      AddEventSheetLayout(
        state = state,
        onSaveAutoEvent = {},
        onSaveManualEvent = {},
        onYearSelected = {},
        onClose = {}
      )
    }
  }
}

@FrcKrawlerPreview
@Composable
private fun AddEventErrorPreview() {
  val state = AddEventScreenState(
    years = listOf(2023, 2022),
    events = listOf(
      TbaSimpleEvent(
        key = "2023mnmi",
        name = "10,000 Lakes Regional"
      )
    ),
    areEventsLoading = false,
    hasNetworkError = true,
  )
  FrcKrawlerTheme {
    Surface {
      AddEventSheetLayout(
        state = state,
        onSaveAutoEvent = {},
        onSaveManualEvent = {},
        onYearSelected = {},
        onClose = {}
      )
    }
  }
}

@FrcKrawlerPreview
@Composable
private fun AddEventLoadingPreview() {
  val state = AddEventScreenState(
    years = listOf(2023, 2022),
    events = listOf(
      TbaSimpleEvent(
        key = "2023mnmi",
        name = "10,000 Lakes Regional"
      )
    ),
    areEventsLoading = true,
    hasNetworkError = false,
  )
  FrcKrawlerTheme {
    Surface {
      AddEventSheetLayout(
        state = state,
        onSaveAutoEvent = {},
        onSaveManualEvent = {},
        onYearSelected = {},
        onClose = {}
      )
    }
  }
}

@FrcKrawlerPreview
@Composable
private fun AddEventSavingPreview() {
  val state = AddEventScreenState(
    years = listOf(2023, 2022),
    events = listOf(
      TbaSimpleEvent(
        key = "2023mnmi",
        name = "10,000 Lakes Regional"
      )
    ),
    areEventsLoading = true,
    hasNetworkError = false,
    isSavingEvent = true,
  )
  FrcKrawlerTheme {
    Surface {
      AddEventSheetLayout(
        state = state,
        onSaveAutoEvent = {},
        onSaveManualEvent = {},
        onYearSelected = {},
        onClose = {}
      )
    }
  }
}

@FrcKrawlerPreview
@Composable
private fun ManualEventEntryPreview() {
  val name = rememberTextFieldState()
  FrcKrawlerTheme {
    Surface {
      ManualEventEntry(
        name = name,
        isSavingEvent = false
      )
    }
  }
}


@FrcKrawlerPreview
@Composable
private fun ManualEventSavingPreview() {
  val name = rememberTextFieldState()
  FrcKrawlerTheme {
    Surface {
      ManualEventEntry(
        name = name,
        isSavingEvent = true
      )
    }
  }
}
