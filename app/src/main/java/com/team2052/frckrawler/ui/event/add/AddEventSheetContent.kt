package com.team2052.frckrawler.ui.event.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import java.time.Year

@Composable
fun AddEventSheetContent(
  gameId: Int,
  onClose: () -> Unit
) {
  val viewModel: AddEventViewModel = hiltViewModel()
  val state = viewModel.state.collectAsState()

  LaunchedEffect(true) {
    viewModel.loadEventsForYear(Year.now().value)
  }

  AddEventSheetLayout(
    state = state.value,
    onSaveAutoEvent = { event ->
      viewModel.saveAutoEvent(gameId, event)
      onClose()
    },
    onSaveManualEvent = { name ->
      viewModel.saveManualEvent(gameId, name)
      onClose()
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
  var autoEventSelectedYear: Int by remember { mutableStateOf(Year.now().value) }
  var autoEventSelectedEvent: TbaSimpleEvent? by remember { mutableStateOf(null) }
  var manualEventName by remember { mutableStateOf("") }

  val saveEnabled by remember {
    derivedStateOf {
      (selectedTabIndex == 0 && autoEventSelectedYear != 0 && autoEventSelectedEvent != null)
              || (selectedTabIndex == 1) && manualEventName.isNotBlank()
    }
  }

  Column {
    Text(
      modifier = Modifier.padding(16.dp),
      text = stringResource(R.string.add_event_sheet_title),
      style = MaterialTheme.typography.h6
    )

    AddEventTabs(
      selectedTabIndex = selectedTabIndex,
      onTabSelected = { selectedTabIndex = it }
    )
    Divider(color = MaterialTheme.colors.primary)

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
        onRetryFetchEvents = {
          onYearSelected(autoEventSelectedYear)
        }
      )

      1 -> ManualEventEntry(
        name = manualEventName,
        onNameChanged = { manualEventName = it }
      )
    }

    Row(
      horizontalArrangement = Arrangement.End
    ) {
      TextButton(
        modifier = Modifier.padding(12.dp),
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

@Composable
private fun AddEventTabs(
  selectedTabIndex: Int,
  onTabSelected: (Int) -> Unit,
) {
  TabRow(
    selectedTabIndex = selectedTabIndex,
    backgroundColor = MaterialTheme.colors.surface,
    contentColor = MaterialTheme.colors.primary
  ) {
    Tab(
      modifier = Modifier.padding(vertical = 16.dp),
      selected = selectedTabIndex == 0,
      onClick = { onTabSelected(0) },

      ) {
      Text(
        text = stringResource(R.string.add_event_tab_auto).uppercase(),
      )
    }
    Tab(
      modifier = Modifier.padding(vertical = 12.dp),
      selected = selectedTabIndex == 1,
      onClick = { onTabSelected(1) },
    ) {
      Text(
        text = stringResource(R.string.add_event_tab_manual).uppercase(),
      )
    }
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
  hasEventNetworkError: Boolean,
  onRetryFetchEvents: () -> Unit
) {
  Column(
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
  ) {
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
        color = MaterialTheme.colors.error
      )
    }
  }
}

@Composable
private fun ManualEventEntry(
  name: String,
  onNameChanged: (String) -> Unit
) {
  var hasHadFocus by remember { mutableStateOf(false) }
  Column(
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
  ) {
    FRCKrawlerTextField(
      value = name,
      onValueChange = onNameChanged,
      isError = { hasHadFocus && name.isBlank() },
      onFocusChange = { focused -> if (!hasHadFocus && focused) hasHadFocus = true },
      label = stringResource(R.string.add_event_name_label)
    )
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
private fun ManualEventEntryPreview() {
  var name by remember { mutableStateOf("") }
  FrcKrawlerTheme {
    Surface {
      ManualEventEntry(
        name = name,
        onNameChanged = { name = it }
      )
    }
  }
}
