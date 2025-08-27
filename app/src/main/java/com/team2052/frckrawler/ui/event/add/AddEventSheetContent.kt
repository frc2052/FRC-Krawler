package com.team2052.frckrawler.ui.event.add

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.remote.model.TbaSimpleEvent
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import com.team2052.frckrawler.ui.components.fields.FRCKrawlerDropdown
import com.team2052.frckrawler.ui.components.fields.FRCKrawlerTextField
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import kotlinx.coroutines.launch
import java.time.Year

@Composable
fun AddEventSheetContent(
  gameId: Int,
  onClose: () -> Unit
) {
  val scope = rememberCoroutineScope()
  val viewModel: AddEventViewModel = hiltViewModel()
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
  var manualEventName by remember { mutableStateOf("") }

  val saveEnabled by remember {
    derivedStateOf {
      val validAutoEvent = selectedTabIndex == 0 && autoEventSelectedYear != 0 && autoEventSelectedEvent != null
      val validManualEvent = selectedTabIndex == 1 && manualEventName.isNotBlank()
      (validAutoEvent || validManualEvent) && !state.isSavingEvent
    }
  }

  Column {
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
        onNameChanged = { manualEventName = it },
        isSavingEvent = state.isSavingEvent,
      )
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
            1 -> onSaveManualEvent(manualEventName)
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
  Box(
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
  ) {
    AnimatedContent(isSavingEvent, label = "autoEntryContent") { isSaving ->
      if (isSaving) {
        SavingEvent()
      } else {
        Column {
          FRCKrawlerDropdown(
            value = selectedYear,
            getLabel = { it.toString() },
            onValueChange = { selected -> selected?.let { onYearSelected(it) } },
            label = stringResource(R.string.add_event_year_label),
            dropdownItems = years
          )

          Row(
            verticalAlignment = Alignment.CenterVertically
          ) {
            var showEventError by remember { mutableStateOf(false) }
            FRCKrawlerDropdown(
              value = selectedEvent,
              getLabel = { it?.name ?: "" },
              onValueChange = { selected -> selected?.let { onEventSelected(it) } },
              onFocusChange = { showEventError = selectedEvent == null },
              validity = !showEventError,
              label = stringResource(R.string.add_event_event_label),
              dropdownItems = events,
              enabled = !areEventsLoading && !hasEventNetworkError
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

@Composable
private fun ManualEventEntry(
  name: String,
  onNameChanged: (String) -> Unit,
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
          value = name,
          onValueChange = onNameChanged,
          isError = { hasHadFocus && name.isBlank() },
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
  var name by remember { mutableStateOf("") }
  FrcKrawlerTheme {
    Surface {
      ManualEventEntry(
        name = name,
        onNameChanged = { name = it },
        isSavingEvent = false
      )
    }
  }
}


@FrcKrawlerPreview
@Composable
private fun ManualEventSavingPreview() {
  var name by remember { mutableStateOf("") }
  FrcKrawlerTheme {
    Surface {
      ManualEventEntry(
        name = name,
        onNameChanged = { name = it },
        isSavingEvent = true
      )
    }
  }
}
