package com.team2052.frckrawler.ui.game.detail

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberModalBottomSheetState
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import com.team2052.frckrawler.ui.components.FRCKrawlerAppBar
import com.team2052.frckrawler.ui.components.FRCKrawlerScaffold
import com.team2052.frckrawler.ui.event.add.AddEventSheetContent
import com.team2052.frckrawler.ui.game.AddMetricSetDialog
import com.team2052.frckrawler.ui.navigation.Screen
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GameDetailScreen(
  gameId: Int,
  navController: NavController,
) {
  val scope = rememberCoroutineScope()
  val viewModel: GameDetailViewModel = hiltViewModel()
  val state = viewModel.state.collectAsState().value

  val addEventSheetState = rememberModalBottomSheetState(
    initialValue = ModalBottomSheetValue.Hidden,
    skipHalfExpanded = true
  )
  var showAddMetricSet by remember { mutableStateOf(false) }
  var showDeleteConfirmation by remember { mutableStateOf(false) }

  val title = if (state is GameDetailState.Content) {
    state.game.name
  } else ""

  LaunchedEffect(true) {
    viewModel.loadGame(gameId)
  }

  FRCKrawlerScaffold(
    appBar = {
      FRCKrawlerAppBar(
        navController = navController,
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
  ) {
    ModalBottomSheetLayout(
      sheetState = addEventSheetState,
      sheetContent = {
        AddEventSheetContent(
          gameId = gameId,
          onClose = {
            scope.launch {
              addEventSheetState.hide()
            }
          },
        )
      }
    ) {
      if (state is GameDetailState.Content) {
        GameDetailContent(
          events = state.events,
          onAddEventClick = {
            scope.launch {
              addEventSheetState.show()
            }
          },
          onEventClick = { event -> navController.navigate(Screen.Event(event.id).route) },
          metricSets = state.metrics,
          onMetricSetClick = { set -> navController.navigate(Screen.MetricSet(set.id).route) },
          onAddMetricSetClick = { showAddMetricSet = true },
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
            navController.popBackStack()
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
) {
  Column(
    modifier = modifier.padding(16.dp)
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
          Divider()
        }
      }
    } else {
      Text(
        modifier = Modifier.padding(vertical = 8.dp),
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
      style = MaterialTheme.typography.subtitle1
    )
    Text(
      modifier = Modifier.alpha(.6f),
      text = pluralStringResource(
        id = R.plurals.game_event_team_count,
        count = event.teamCount,
        event.teamCount,
      ),
      style = MaterialTheme.typography.body2
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
          Divider()
        }
      }
    } else {
      Text(
        modifier = Modifier.padding(vertical = 12.dp),
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
        style = MaterialTheme.typography.subtitle1
      )
      Text(
        modifier = Modifier.alpha(.6f),
        text = pluralStringResource(
          id = R.plurals.game_metric_count,
          count = metricSet.metricCount,
          metricSet.metricCount,
        ),
        style = MaterialTheme.typography.body2
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
            color = MaterialTheme.colors.primary,
            shape = MaterialTheme.shapes.small
        )
        .padding(horizontal = 4.dp, vertical = 2.dp),
    text = text,
    style = MaterialTheme.typography.caption,
    color = MaterialTheme.colors.primary,
  )
}

@Composable
private fun GameDetailCardLayout(
  modifier: Modifier = Modifier,
  title: String,
  onAddClicked: () -> Unit,
  content: @Composable () -> Unit,
) {
  Card(
    modifier = modifier,
    elevation = 2.dp
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
          style = MaterialTheme.typography.h6
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