package com.team2052.frckrawler.ui.game.detail

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.team2052.frckrawler.R
import dev.zacsweers.metrox.viewmodel.metroViewModel
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import com.team2052.frckrawler.ui.components.FRCKrawlerAppBar
import com.team2052.frckrawler.ui.event.add.AddEventSheetContent
import com.team2052.frckrawler.ui.game.AddMetricSetDialog
import com.team2052.frckrawler.ui.navigation.Screen
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailScreen(
  gameId: Int,
  backStack: NavBackStack<NavKey>,
) {
  val scope = rememberCoroutineScope()
  val viewModel: GameDetailViewModel = metroViewModel()
  val state = viewModel.state.collectAsState().value

  var showAddEventSheet by remember { mutableStateOf(false) }
  val addEventSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var showAddMetricSet by remember { mutableStateOf(false) }
  var showDeleteConfirmation by remember { mutableStateOf(false) }

  val title = if (state is GameDetailState.Content) {
    state.game.name
  } else ""

  LaunchedEffect(gameId) {
    viewModel.loadGame(gameId)
  }

  Scaffold(
    topBar = {
      FRCKrawlerAppBar(
        backStack = backStack,
        title = {
          Text(title)
        },
        actions = {
          IconButton(
            onClick = { showDeleteConfirmation = true }
          ) {
            Icon(
              imageVector = Icons.Filled.Delete,
              contentDescription = stringResource(R.string.delete)
            )
          }
        }
      )
    },
  ) { contentPadding ->
    if (state is GameDetailState.Content) {
      GameDetailContent(
        events = state.events,
        onAddEventClick = {
          showAddEventSheet = true
        },
        onEventClick = { event -> backStack.add(Screen.Event(event.id)) },
        metricSets = state.metrics,
        onMetricSetClick = { set -> backStack.add(Screen.MetricSet(set.id)) },
        onAddMetricSetClick = { showAddMetricSet = true },
        contentPadding = contentPadding,
      )
    }

    if (showAddEventSheet) {
      ModalBottomSheet(
        sheetState = addEventSheetState,
        onDismissRequest = { showAddEventSheet = false }
      ) {
        AddEventSheetContent(
          gameId = gameId,
          onClose = {
            scope.launch {
              addEventSheetState.hide()
            }.invokeOnCompletion {
              if (!addEventSheetState.isVisible) {
                showAddEventSheet = false
              }
            }
          },
        )
      }
    }

    if (showAddMetricSet) {
      AddMetricSetDialog(
        onAddMetricSet = { name ->
          viewModel.createNewMetricSet(name)
        },
        onClose = { showAddMetricSet = false }
      )
    }

    if (showDeleteConfirmation) {
      AlertDialog(
        title = { Text(stringResource(R.string.delete_game_confirmation_title)) },
        text = { Text(stringResource(R.string.delete_game_confirmation_body)) },
        onDismissRequest = { showDeleteConfirmation = false },
        confirmButton = {
          TextButton(onClick = {
            viewModel.deleteGame()
            backStack.removeLastOrNull()
          }) {
            Text(stringResource(R.string.delete).uppercase())
          }
        },
        dismissButton = {
          TextButton(onClick = { showDeleteConfirmation = false }) {
            Text(stringResource(R.string.cancel).uppercase())
          }
        },
      )
    }
  }
}

@Composable
private fun GameDetailContent(
  events: List<GameDetailEvent>,
  onAddEventClick: () -> Unit,
  onEventClick: (GameDetailEvent) -> Unit,
  metricSets: List<GameDetailMetricSet>,
  onAddMetricSetClick: () -> Unit,
  onMetricSetClick: (GameDetailMetricSet) -> Unit,
  modifier: Modifier = Modifier,
  contentPadding: PaddingValues = PaddingValues(0.dp),
) {
  val scrollState = rememberScrollState()
  Column(
    modifier = modifier
      .verticalScroll(scrollState)
      .padding(16.dp)
      .padding(contentPadding)
  ) {
    EventListCard(
      modifier = Modifier.fillMaxWidth(),
      onAddEventClick = onAddEventClick,
      onEventClick = onEventClick,
      events = events
    )
    Spacer(Modifier.height(16.dp))
    MetricSetsCard(
      modifier = Modifier.fillMaxWidth(),
      onAddMetricSetClick = onAddMetricSetClick,
      onMetricSetClick = onMetricSetClick,
      metricSets = metricSets
    )
    Spacer(
      Modifier.windowInsetsBottomHeight(WindowInsets.systemBars)
        .consumeWindowInsets(WindowInsets.systemBars.only(WindowInsetsSides.Bottom))
    )
  }
}

@Composable
private fun EventListCard(
  events: List<GameDetailEvent>,
  onAddEventClick: () -> Unit,
  onEventClick: (GameDetailEvent) -> Unit,
  modifier: Modifier = Modifier,
) {
  GameDetailCardLayout(
    modifier = modifier,
    title = stringResource(R.string.game_detail_event_card_title),
    onAddClicked = onAddEventClick
  ) {
    if (events.isNotEmpty()) {
      events.forEach { event ->
        EventRow(
          event = event,
          onEventClick = onEventClick
        )
        if (event != events.last()) {
          HorizontalDivider()
        }
      }
    } else {
      Text(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        text = stringResource(R.string.game_detail_no_events),
        fontStyle = FontStyle.Italic
      )
    }
  }
}

@Composable
private fun EventRow(
  event: GameDetailEvent,
  onEventClick: (GameDetailEvent) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .clickable { onEventClick(event) }
      .padding(horizontal = 16.dp, vertical = 12.dp)
  ) {
    Text(
      text = event.name,
      style = MaterialTheme.typography.bodyLarge
    )
    Text(
      modifier = Modifier.alpha(.6f),
      text = pluralStringResource(
        id = R.plurals.game_event_team_count,
        count = event.teamCount,
        event.teamCount,
      ),
      style = MaterialTheme.typography.bodyMedium
    )
  }
}

@Composable
private fun MetricSetsCard(
  metricSets: List<GameDetailMetricSet>,
  onAddMetricSetClick: () -> Unit,
  onMetricSetClick: (GameDetailMetricSet) -> Unit,
  modifier: Modifier = Modifier,
) {
  GameDetailCardLayout(
    modifier = modifier,
    title = stringResource(R.string.game_detail_metrics_card_title),
    onAddClicked = onAddMetricSetClick
  ) {
    if (metricSets.isNotEmpty()) {
      metricSets.forEach { set ->
        MetricSetRow(
          metricSet = set,
          onMetricSetClick = onMetricSetClick
        )
        if (set != metricSets.last()) {
          HorizontalDivider()
        }
      }
    } else {
      Text(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        text = stringResource(R.string.game_detail_no_metric_sets),
        fontStyle = FontStyle.Italic
      )
    }
  }
}

@Composable
private fun MetricSetRow(
  metricSet: GameDetailMetricSet,
  onMetricSetClick: (GameDetailMetricSet) -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .clickable { onMetricSetClick(metricSet) }
      .padding(horizontal = 16.dp, vertical = 12.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Column {
      Text(
        text = metricSet.name,
        style = MaterialTheme.typography.bodyLarge
      )
      Text(
        modifier = Modifier.alpha(.6f),
        text = pluralStringResource(
          id = R.plurals.game_metric_count,
          count = metricSet.metricCount,
          metricSet.metricCount,
        ),
        style = MaterialTheme.typography.bodyMedium
      )
    }

    Spacer(Modifier.width(8.dp))

    if (metricSet.isMatchMetrics || metricSet.isPitMetrics) {
      Row {
        if (metricSet.isMatchMetrics) {
          MetricSetChip(text = stringResource(R.string.metric_set_match_chip_label))
        }
        if (metricSet.isPitMetrics) {
          MetricSetChip(text = stringResource(R.string.metric_set_pit_chip_label))
        }
      }
    }
  }
}

@Composable
private fun MetricSetChip(
  text: String,
  modifier: Modifier = Modifier,
) {
  Text(
    modifier = modifier
      .padding(horizontal = 2.dp)
      .border(
        width = 1.dp,
        color = MaterialTheme.colorScheme.primary,
        shape = MaterialTheme.shapes.small
      )
      .padding(horizontal = 4.dp, vertical = 2.dp),
    text = text,
    style = MaterialTheme.typography.bodySmall,
    color = MaterialTheme.colorScheme.primary,
  )
}

@Composable
private fun GameDetailCardLayout(
  modifier: Modifier = Modifier,
  title: String,
  onAddClicked: () -> Unit,
  content: @Composable () -> Unit,
) {
  ElevatedCard(
    modifier = modifier
  ) {
    Column {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 16.dp, start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.titleLarge
        )

        Button(onClick = onAddClicked) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null
          )
          Text(text = stringResource(R.string.add).uppercase())
        }
      }

      Spacer(Modifier.height(8.dp))

      content()
    }
  }
}

@FrcKrawlerPreview
@Composable
private fun GameDetailPreview() {
  FrcKrawlerTheme {
    Surface {
      GameDetailContent(
        events = listOf(
          GameDetailEvent(
            name = "10,000 Lakes Regional",
            id = 0,
            teamCount = 36
          ),
          GameDetailEvent(
            name = "Lake Superior Regional",
            id = 0,
            teamCount = 49
          )
        ),
        onAddEventClick = {},
        onEventClick = {},
        metricSets = listOf(
          GameDetailMetricSet(
            name = "Regional metrics",
            id = 0,
            metricCount = 21,
            isMatchMetrics = true,
            isPitMetrics = true,
          ),
          GameDetailMetricSet(
            name = "Regional metrics 2",
            id = 0,
            metricCount = 21,
            isMatchMetrics = false,
            isPitMetrics = true,
          ),
          GameDetailMetricSet(
            name = "Regional metrics 3",
            id = 0,
            metricCount = 21,
            isMatchMetrics = true,
            isPitMetrics = false,
          ),
          GameDetailMetricSet(
            name = "Regional metrics 3",
            id = 0,
            metricCount = 21,
            isMatchMetrics = false,
            isPitMetrics = false,
          ),
        ),
        onMetricSetClick = {},
        onAddMetricSetClick = {},
      )
    }
  }
}

@FrcKrawlerPreview
@Composable
private fun GameDetailEmptyPreview() {
  FrcKrawlerTheme {
    Surface {
      GameDetailContent(
        events = emptyList(),
        onAddEventClick = {},
        onEventClick = {},
        metricSets = emptyList(),
        onMetricSetClick = {},
        onAddMetricSetClick = {},
      )
    }
  }
}