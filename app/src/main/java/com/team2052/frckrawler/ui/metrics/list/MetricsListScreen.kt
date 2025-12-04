package com.team2052.frckrawler.ui.metrics.list


import androidx.annotation.StringRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.model.Metric
import dev.zacsweers.metrox.viewmodel.metroViewModel
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import com.team2052.frckrawler.ui.common.BasicDraggableContent
import com.team2052.frckrawler.ui.common.DragDropState
import com.team2052.frckrawler.ui.common.DraggableItem
import com.team2052.frckrawler.ui.common.dragHandle
import com.team2052.frckrawler.ui.common.rememberDragDropState
import com.team2052.frckrawler.ui.metrics.edit.AddEditMetricDialog
import com.team2052.frckrawler.ui.metrics.edit.AddEditMetricMode
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import com.team2052.frckrawler.ui.components.FRCKrawlerAppBar
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetricsListScreen(
  modifier: Modifier = Modifier,
  backStack: NavBackStack<NavKey>,
  metricSetId: Int
) {
  val viewModel: MetricsListViewModel = metroViewModel()
  var showMetricSheet by remember { mutableStateOf(false) }
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val scope = rememberCoroutineScope()
  var sheetMode: AddEditMetricMode by remember { mutableStateOf(AddEditMetricMode.New()) }
  var showDeleteConfirmation by remember { mutableStateOf(false) }

  LaunchedEffect(metricSetId) {
    viewModel.loadMetrics(metricSetId)
  }

  val state = viewModel.state.collectAsState().value

  Scaffold(
    modifier = modifier,
    topBar = {
      FRCKrawlerAppBar(
        title = {
          if (state is MetricListScreenState.Content) {
            Text(state.setName)
          }
        },
        backStack = backStack,
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
      if (sheetState.targetValue == SheetValue.Hidden) {
        MetricActions(
          onAddClick = {
            sheetMode = AddEditMetricMode.New()
            showMetricSheet = true
          }
        )
      }
    },
  ) { contentPadding ->
    if (state is MetricListScreenState.Content) {
      if (state.metrics.isEmpty()) {
        EmptyBackground(
          modifier = Modifier.padding(contentPadding)
            .consumeWindowInsets(contentPadding)
        )
      } else {
        MetricListContent(
          metrics = state.metrics,
          onMetricClick = { metric ->
            sheetMode = AddEditMetricMode.Edit(metric)
            showMetricSheet = true
          },
          gameName = state.gameName,
          isMatchMetrics = state.isMatchMetricSet,
          onIsMatchMetricsChanged = { viewModel.setIsMatchMetrics(it) },
          isPitMetrics = state.isPitMetricSet,
          onIsPitMetricsChanged = { viewModel.setIsPitMetrics(it) },
          onMetricsReordered = { metrics ->  viewModel.updateMetricsOrder(metrics) },
          contentPadding = contentPadding,
        )
      }
    } else {
      // Show nothing while loading. Could do a loading spinner in the future
    }

    if (showMetricSheet) {
      ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { showMetricSheet = false }
      ) {
        AddEditMetricDialog(
          mode = sheetMode,
          metricSetId = metricSetId,
          onClose = {
            scope.launch {
              sheetState.hide()
            }.invokeOnCompletion {
              if (!sheetState.isVisible) {
                showMetricSheet = false
              }
            }
          },
        )
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
            backStack.removeLastOrNull()
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

@Composable
private fun EmptyBackground(
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Icon(
      modifier = Modifier.size(128.dp),
      imageVector = Icons.Filled.Analytics,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.outlineVariant
    )
    Text(
      text = stringResource(R.string.metric_list_empty_text),
      style = MaterialTheme.typography.headlineMedium
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
  metrics: ImmutableList<Metric>,
  onMetricClick: (Metric) -> Unit,
  gameName: String,
  isMatchMetrics: Boolean,
  onIsMatchMetricsChanged: (Boolean) -> Unit,
  isPitMetrics: Boolean,
  onIsPitMetricsChanged: (Boolean) -> Unit,
  onMetricsReordered: (List<Metric>) -> Unit,
  contentPadding: PaddingValues = PaddingValues(0.dp),
) {
  var localMetricList by remember(metrics) {
    mutableStateOf(metrics)
  }

  val listState = rememberLazyListState()
  val dragDropState = rememberDragDropState(
    lazyListState = listState,
    onMove =  { fromIndex, toIndex ->
      localMetricList = localMetricList.toPersistentList().mutate {
        it.add(toIndex, it.removeAt(fromIndex))
      }
    },
    onDragEnded = {
      onMetricsReordered(localMetricList)
    }
  )

  LazyColumn(
    modifier = modifier.consumeWindowInsets(contentPadding),
    state = listState,
    contentPadding = contentPadding,
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
      HorizontalDivider()
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
      HorizontalDivider()
    }

    itemsIndexed(
      items = localMetricList,
      key = { _, metric -> metric.id },
      contentType = { index, _ -> BasicDraggableContent(index) }
    ) { _, metric ->
      DraggableItem(
        dragDropState = dragDropState,
        key = metric.id
      ) { isDragging ->
        val elevation by animateDpAsState(
          targetValue = if (isDragging) 4.dp else 0.dp,
          label = "drag elevation"
        )
        Surface(
          shadowElevation = elevation
        ) {
          MetricListRow(
            modifier = Modifier.fillMaxWidth(),
            dragDropState = dragDropState,
            metric = metric,
            onMetricClick = onMetricClick
          )
        }
      }
    }

    // Extra space at the bottom to ensure the drag handle isn't covered by the FAB
    item {
      Spacer(Modifier.height(64.dp))
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
      .background(MaterialTheme.colorScheme.surfaceContainer)
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
  dragDropState: DragDropState,
  metric: Metric,
  onMetricClick: (Metric) -> Unit
) {
  val description: String =  when (metric) {
    is Metric.BooleanMetric -> stringResource(R.string.metric_list_boolean_description)
    is Metric.CheckboxMetric -> {
      val optionsString = metric.options.joinToString(", ")
      stringResource(R.string.metric_list_checkbox_description, optionsString)
    }

    is Metric.ChooserMetric -> {
      val optionsString = metric.options.joinToString(", ")
      stringResource(R.string.metric_list_chooser_description, optionsString)
    }

    is Metric.CounterMetric -> {
      stringResource(R.string.metric_list_counter_description, metric.range.first, metric.range.last, metric.range.step)
    }

    is Metric.SliderMetric -> {
      stringResource(R.string.metric_list_slider_description, metric.range.first, metric.range.last)
    }

    is Metric.StopwatchMetric -> stringResource(R.string.metric_list_stopwatch_description)
    is Metric.TextFieldMetric -> stringResource(R.string.metric_list_textfield_description)
    is Metric.SectionHeader -> stringResource(R.string.metric_list_header_description)
  }

  Column(
    modifier = modifier
      .clickable { onMetricClick(metric) }
  ) {
    Row(
      modifier = Modifier
        .background(color = MaterialTheme.colorScheme.surface)
        .fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
      ) {
        Text(
          text = metric.name,
          style = MaterialTheme.typography.headlineSmall
        )
        Text(
          text = description,
          style = MaterialTheme.typography.bodyLarge
        )
      }

      Spacer(Modifier.width(12.dp))

      Icon(
        modifier = Modifier
          .requiredSize(48.dp)
          .dragHandle(dragDropState, metric.id)
          .padding(12.dp),
        imageVector = Icons.Default.DragHandle,
        contentDescription = stringResource(R.string.cd_drag)
      )
    }
    HorizontalDivider()
  }
}

@FrcKrawlerPreview
@Composable
private fun MetricListRowPreview() {
  val dragDropState = rememberDragDropState(
    lazyListState = rememberLazyListState(),
    onMove = { _, _ -> },
    onDragEnded = {}
  )
  FrcKrawlerTheme {
    Surface {
      MetricListRow(
        dragDropState = dragDropState,
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
    id = "metric1",
    name = "Number of LEDs",
    priority = 1,
    enabled = true,
    range = 1..1000 step 5
  )
  val metric2 = Metric.BooleanMetric(
    id = "metric8",
    name = "smol",
    priority = 1,
    enabled = true,
  )
  FrcKrawlerTheme {
    Surface {
      MetricListContent(
        metrics = persistentListOf(metric, metric.copy(id = "metric2"), metric.copy(id = "metric3"), metric2),
        onMetricClick = {},
        gameName = "Crescendo",
        isMatchMetrics = false,
        onIsMatchMetricsChanged = {},
        isPitMetrics = true,
        onIsPitMetricsChanged = {},
        onMetricsReordered = {}
      )
    }
  }
}