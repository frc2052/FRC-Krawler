package com.team2052.frckrawler.ui.analyze

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.local.TeamAtEvent
import com.team2052.frckrawler.data.model.Metric
import com.team2052.frckrawler.data.summary.DoubleSummaryValue
import com.team2052.frckrawler.ui.components.FRCKrawlerAppBar
import com.team2052.frckrawler.ui.components.fields.UnlabeledDropdown
import com.team2052.frckrawler.ui.navigation.Screen
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyzeDataScreen(
  gameId: Int,
  eventId: Int,
  navController: NavController,
  modifier: Modifier = Modifier,
) {
  val viewModel: AnalyzeDataViewModel = hiltViewModel()
  val scope = rememberCoroutineScope()
  var showFilterSheet by remember { mutableStateOf(false) }

  LaunchedEffect(gameId, eventId) {
    viewModel.loadGameAndEvent(gameId, eventId)
  }

  Scaffold(
    modifier = modifier,
    topBar = {
      FRCKrawlerAppBar(
        navController = navController,
        title = {
          Text(stringResource(R.string.analyze_screen_title))
        },
        actions = {
          IconButton(
            onClick = {
              navController.navigate(
                Screen.Export(
                  gameId = gameId,
                  eventId = eventId
                ).route
              )
            }
          ) {
            Icon(
              imageVector = Icons.Filled.Download,
              contentDescription = stringResource(R.string.analyze_export_label)
            )
          }
        }
      )
    },
  ) { contentPadding ->
    val state by viewModel.state.collectAsState()

    // Not bothering with a loading screen for now, it is generally fast enough
    if (state is AnalyzeDataScreenState.Content) {
      val content = state as AnalyzeDataScreenState.Content
      AnalyzeScreenContent(
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding,
        state = content,
        onSortSelected = viewModel::setSortMode,
        onFilterClick = { showFilterSheet = true },
      )

      if (showFilterSheet) {
        AnalyzeDataFilterSheet(
          onDismissRequest = { showFilterSheet = false },
          filterState = content.filterState,
          onFilter = { metric, metricOption ->
            viewModel.setSelectedMetric(metric.id)
            viewModel.setSelectedMetricOption(metricOption)
            showFilterSheet = false
          }
        )
      }
    }

  }
}

@Composable
private fun AnalyzeScreenContent(
  state: AnalyzeDataScreenState.Content,
  onFilterClick: () -> Unit,
  onSortSelected: (AnalyzeSortMode) -> Unit,
  modifier: Modifier = Modifier,
  contentPadding: PaddingValues = PaddingValues(0.dp),
) {
  Column(modifier = modifier.padding(
    top = contentPadding.calculateTopPadding(),
  ).consumeWindowInsets(
    PaddingValues(top = contentPadding.calculateTopPadding())
  )) {
    AnalyzeDataHeader(
      modifier = Modifier.fillMaxWidth(),
      onSortSelected = onSortSelected,
      onFilterClick = onFilterClick,
      state = state,
    )


    val listState = rememberLazyListState()
    // Keep current scroll position when reordering items
    SideEffect {
      listState.requestScrollToItem(
        index = listState.firstVisibleItemIndex,
        scrollOffset = listState.firstVisibleItemScrollOffset
      )
    }

    LazyColumn(
      modifier = Modifier
        .padding(bottom = contentPadding.calculateBottomPadding())
        .consumeWindowInsets(
          PaddingValues(bottom = contentPadding.calculateBottomPadding())
        ),
      state = listState
    ) {

      items(
        items = state.teamData,
        key = { it.team.number }
      ) { teamData ->
        TeamDataRow(
          data = teamData,
          modifier = Modifier.fillMaxWidth()
        )
        HorizontalDivider()
      }
    }
  }
}

@Composable
private fun AnalyzeDataHeader(
  state: AnalyzeDataScreenState.Content,
  onFilterClick: () -> Unit,
  onSortSelected: (AnalyzeSortMode) -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    color = MaterialTheme.colorScheme.surfaceContainer,
    modifier = modifier,
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
      Text(
        text = "${state.gameName} - ${state.eventName}"
      )
      AnalyzeFilterOptions(
        state = state.filterState,
        onFilterClick = onFilterClick,
        onSortSelected = onSortSelected,
      )
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AnalyzeFilterOptions(
  modifier: Modifier = Modifier,
  state: SortFilterState,
  onFilterClick: () -> Unit,
  onSortSelected: (AnalyzeSortMode) -> Unit,
) {
  Row(
    modifier = modifier,
  ) {
    Text(
      modifier = Modifier.alignByBaseline(),
      text = stringResource(R.string.sort_label)
    )
    OutlinedButton(
      modifier = Modifier.alignByBaseline()
        .weight(1f, fill = false)
        .padding(8.dp),
      onClick = onFilterClick,
    ) {
      val filterLabel = if (state.selectedMetricOption != null) {
        "${state.selectedMetric.name} - ${state.selectedMetricOption}"
      } else {
        state.selectedMetric.name
      }
      Text(
        modifier = Modifier.weight(1f, fill = false),
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        text = filterLabel
      )
      Icon(
        modifier = Modifier.padding(start = 8.dp),
        imageVector = Icons.Default.KeyboardArrowDown,
        contentDescription = null
      )
    }

    val ascendingLabel = stringResource(R.string.sort_ascending)
    val descendingLabel = stringResource(R.string.sort_descending)
    UnlabeledDropdown(
      modifier = Modifier.alignByBaseline(),
      dropdownItems = AnalyzeSortMode.entries,
      getValueLabel = { mode ->
        when (mode) {
          AnalyzeSortMode.Ascending -> ascendingLabel
          AnalyzeSortMode.Descending -> descendingLabel
        }
      },
      value = state.sortMode,
      onValueChange = onSortSelected
    )
  }
}

@Composable
private fun TeamDataRow(
  data: TeamMetricData,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier.padding(vertical = 12.dp, horizontal = 16.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Column(
      modifier = Modifier.weight(weight = 1f, fill = false),
    ) {
      Text(
        text = data.team.name,
        style = MaterialTheme.typography.titleMedium
      )
      Text(
        text = data.team.number,
        style = MaterialTheme.typography.bodyMedium
      )
    }
    Spacer(Modifier.width(16.dp))
    Text(data.summary.asDisplayString())
  }
}

@Preview
@Composable
private fun AnalyzeScreenPreview() {
  val metric = Metric.BooleanMetric(
    id = "1",
    name = "Has wheels",
    priority = 0,
    enabled = true
  )
  val teamData = listOf(
    TeamMetricData(
      team = TeamAtEvent(
        number = "2052",
        name = "KnightKrawler",
        eventId = 0
      ),
      summary = DoubleSummaryValue(100.0, true)
    )
  )
  val state = AnalyzeDataScreenState.Content(
    gameName = "Reefscape",
    eventName = "Northern Lights",
    teamData = teamData,
    filterState = SortFilterState(
      availableMetrics = listOf(metric),
      selectedMetric = metric,
      selectedMetricOption = "Red",
      sortMode = AnalyzeSortMode.Ascending,
    ),
  )
  FrcKrawlerTheme {
    Surface {
      AnalyzeScreenContent(
        state = state,
        onFilterClick = {},
        onSortSelected = {},
      )
    }
  }
}