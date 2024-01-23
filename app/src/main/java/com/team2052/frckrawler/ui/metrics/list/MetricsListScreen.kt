package com.team2052.frckrawler.ui.metrics.list


import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.model.Metric
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import com.team2052.frckrawler.ui.components.FRCKrawlerAppBar
import com.team2052.frckrawler.ui.components.FRCKrawlerScaffold
import com.team2052.frckrawler.ui.metrics.edit.AddEditMetricDialog
import com.team2052.frckrawler.ui.metrics.edit.AddEditMetricMode
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import com.team2052.frckrawler.ui.theme.highlightSurface
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MetricsListScreen(
  modifier: Modifier = Modifier,
  navController: NavController,
  metricSetId: Int
) {
  val viewModel: MetricsListViewModel = hiltViewModel()
  val sheetState = rememberModalBottomSheetState(
    initialValue = ModalBottomSheetValue.Hidden,
    skipHalfExpanded = true
  )
  val scope = rememberCoroutineScope()
  var sheetMode: AddEditMetricMode by remember { mutableStateOf(AddEditMetricMode.New()) }
  var showDeleteConfirmation by remember { mutableStateOf(false) }

  LaunchedEffect(true) {
    viewModel.loadMetrics(metricSetId)
  }

  val state = viewModel.state.collectAsState().value

  FRCKrawlerScaffold(
    modifier = modifier,
    appBar = {
      FRCKrawlerAppBar(
        title = {
          if (state is MetricListScreenState.Content) {
            Text(state.setName)
          }
        },
        navController = navController,
        actions = {
          if (state is MetricListScreenState.Content) {
            IconButton(
              onClick = { showDeleteConfirmation = true }
            ) {
              Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = stringResource(R.string.delete)
              )
            }
          }
        }
      )
    },
    floatingActionButton = {
      if (sheetState.targetValue == ModalBottomSheetValue.Hidden) {
        MetricActions(
          onAddClick = {
            sheetMode = AddEditMetricMode.New()
            scope.launch {
              sheetState.show()
            }
          }
        )
      }
    },
  ) {
    ModalBottomSheetLayout(
      sheetState = sheetState,
      sheetContent = {
        AddEditMetricDialog(
          mode = sheetMode,
          metricSetId = metricSetId,
          onClose = {
            scope.launch {
              sheetState.hide()
            }
          },
        )
      }
    ) {
      Column {
        if (state is MetricListScreenState.Content) {
          if (state.metrics.isEmpty()) {
            EmptyBackground()
          } else {
            MetricListContent(
              metrics = state.metrics,
              onMetricClick = { metric ->
                sheetMode = AddEditMetricMode.Edit(metric)
                scope.launch {
                  sheetState.show()
                }
              },
              gameName = state.gameName,
              isMatchMetrics = state.isMatchMetricSet,
              onIsMatchMetricsChanged = { viewModel.setIsMatchMetrics(it) },
              isPitMetrics = state.isPitMetricSet,
              onIsPitMetricsChanged = { viewModel.setIsPitMetrics(it) },
            )
          }
        } else {
          // Show nothing while loading. Could do a loading spinner in the future
        }
      }

      if (showDeleteConfirmation) {
        AlertDialog(
          title = { Text(stringResource(R.string.delete_metric_set_dialog_title)) },
          text = { Text(stringResource(R.string.delete_metric_set_dialog_description)) },
          onDismissRequest = { showDeleteConfirmation = false },
          confirmButton = {
            TextButton(onClick = {
              viewModel.deleteMetricSet()
              navController.popBackStack()
            }) {
              Text(stringResource(R.string.delete))
            }
          },
          dismissButton = {
            TextButton(onClick = { showDeleteConfirmation = false }) {
              Text(stringResource(R.string.cancel))
            }
          },
        )
      }
    }
  }
}

@Composable
private fun EmptyBackground() {
  Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Icon(
      modifier = Modifier.size(128.dp),
      imageVector = Icons.Filled.Analytics,
      contentDescription = null,
      tint = MaterialTheme.colors.secondary
    )
    Text(
      text = stringResource(R.string.metric_list_empty_text),
      style = MaterialTheme.typography.h4
    )
  }
}

@Composable
private fun MetricActions(onAddClick: () -> Unit) {
  FloatingActionButton(
    onClick = { onAddClick() }

  ) {
    Icon(
      imageVector = Icons.Filled.Add,
      contentDescription = stringResource(R.string.add_metric_button_description)
    )
  }
}

@Composable
private fun MetricListContent(
  modifier: Modifier = Modifier,
  metrics: List<Metric>,
  onMetricClick: (Metric) -> Unit,
  gameName: String,
  isMatchMetrics: Boolean,
  onIsMatchMetricsChanged: (Boolean) -> Unit,
  isPitMetrics: Boolean,
  onIsPitMetricsChanged: (Boolean) -> Unit,
) {
  LazyColumn(
    contentPadding = WindowInsets.navigationBars.asPaddingValues()
  ) {
    item {
      val matchMetricsLabel = getMetricsLabel(
        prefixResId = R.string.metric_list_is_match_set,
        gameName = gameName
      )
      GameMetricSetSwitchRow(
        modifier = Modifier.fillMaxWidth(),
        checked = isMatchMetrics,
        onCheckedChanged = onIsMatchMetricsChanged,
        label = matchMetricsLabel
      )
    }

    item {
      Divider()
    }

    item {
      val pitMetricsLabel = getMetricsLabel(
        prefixResId = R.string.metric_list_is_pit_set,
        gameName = gameName
      )
      GameMetricSetSwitchRow(
        modifier = Modifier.fillMaxWidth(),
        checked = isPitMetrics,
        onCheckedChanged = onIsPitMetricsChanged,
        label = pitMetricsLabel
      )
    }

    item {
      Divider()
    }

    items(metrics) { metric ->
      MetricListRow(
        modifier = Modifier.fillMaxWidth(),
        metric = metric,
        onMetricClick = onMetricClick
      )
    }
  }
}

@Composable
private fun getMetricsLabel(
  @StringRes prefixResId: Int,
  gameName: String,
): AnnotatedString {
  val matchLabelPrefix = stringResource(prefixResId)
  val label = remember(gameName, matchLabelPrefix) {
    buildAnnotatedString {
      append(matchLabelPrefix)
      append(" ")
      pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
      append(gameName)
    }
  }
  return label
}

@Composable
private fun GameMetricSetSwitchRow(
  checked: Boolean,
  onCheckedChanged: (Boolean) -> Unit,
  label: AnnotatedString,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier
      .background(MaterialTheme.colors.highlightSurface)
      .padding(horizontal = 16.dp, vertical = 12.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = label
    )
    Spacer(Modifier.width(12.dp))
    Switch(
      checked = checked,
      onCheckedChange = onCheckedChanged
    )
  }
}

@Composable
private fun MetricListRow(
  modifier: Modifier = Modifier,
  metric: Metric,
  onMetricClick: (Metric) -> Unit
) {
  val description = remember(metric) {
    when (metric) {
      is Metric.BooleanMetric -> "Boolean"
      is Metric.CheckboxMetric -> {
        val optionsString = metric.options.joinToString(", ")
        "Checkbox ($optionsString)"
      }

      is Metric.ChooserMetric -> {
        val optionsString = metric.options.joinToString(", ")
        "Chooser ($optionsString)"
      }

      is Metric.CounterMetric -> {
        "Counter (min: ${metric.range.first}, max: ${metric.range.last}, step: ${metric.range.step})"
      }

      is Metric.SliderMetric -> {
        "Slider (min: ${metric.range.first}, max: ${metric.range.last})"
      }

      is Metric.StopwatchMetric -> "Stopwatch"
      is Metric.TextFieldMetric -> "Text field"
    }
  }

  Column(
    modifier = modifier
      .background(color = MaterialTheme.colors.background)
      .clickable { onMetricClick(metric) }
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
      ) {
        Text(
          text = metric.name,
          style = MaterialTheme.typography.h5
        )
        Text(
          text = description,
          style = MaterialTheme.typography.subtitle1
        )
      }
    }
    Divider()
  }
}

@FrcKrawlerPreview
@Composable
private fun MetricListRowPreview() {
  FrcKrawlerTheme {
    Surface {
      MetricListRow(
        metric = Metric.CounterMetric(
          id = "",
          name = "Number of LEDs",
          priority = 1,
          enabled = true,
          range = 1..1000 step 5
        ),
        onMetricClick = {}
      )
    }
  }
}

@FrcKrawlerPreview
@Composable
private fun MetricListPreview() {
  val metric = Metric.CounterMetric(
    id = "",
    name = "Number of LEDs",
    priority = 1,
    enabled = true,
    range = 1..1000 step 5
  )
  FrcKrawlerTheme {
    Surface {
      MetricListContent(
        metrics = listOf(metric, metric, metric),
        onMetricClick = {},
        gameName = "Crescendo",
        isMatchMetrics = false,
        onIsMatchMetricsChanged = {},
        isPitMetrics = true,
        onIsPitMetricsChanged = {},
      )
    }
  }
}