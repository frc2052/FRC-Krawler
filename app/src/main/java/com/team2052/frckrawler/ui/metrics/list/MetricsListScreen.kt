package com.team2052.frckrawler.ui.metrics.list


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.local.MetricCategory
import com.team2052.frckrawler.data.model.Metric
import com.team2052.frckrawler.ui.components.FRCKrawlerAppBar
import com.team2052.frckrawler.ui.components.FRCKrawlerDrawer
import com.team2052.frckrawler.ui.components.FRCKrawlerTabBar
import com.team2052.frckrawler.ui.metrics.edit.AddEditMetricDialog
import com.team2052.frckrawler.ui.navigation.Screen
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MetricsListScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    category: MetricCategory,
    gameId: Int
) {
    val scaffoldState = rememberScaffoldState()
    val viewModel: MetricsListViewModel = hiltViewModel()
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        viewModel.loadMatchMetrics(category, gameId)
    }

    Scaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        topBar = {
            FRCKrawlerAppBar(
                navController = navController,
                scaffoldState = scaffoldState,
                title = { Text(stringResource(R.string.metrics_screen) ) }
            )
        },
        floatingActionButton = {
            if (
                sheetState.targetValue == ModalBottomSheetValue.Hidden
            ) {
                MetricActions(
                    onAddClick = {
                        scope.launch {
                            sheetState.show()
                        }
                    }
                )
            }
        },
        drawerContent = { FRCKrawlerDrawer() },
    ) { contentPadding ->
        ModalBottomSheetLayout(
            sheetState = sheetState,
            sheetContent = {
                AddEditMetricDialog(
                    category = category,
                    gameId = gameId,
                    onClose = {
                        scope.launch {
                            sheetState.hide()
                        }
                    },
                )
            }
        ) {
            Column(Modifier.padding(contentPadding)) {
                val currentScreen = when (category) {
                    MetricCategory.Match -> Screen.MatchMetrics(gameId)
                    MetricCategory.Pit -> Screen.PitMetrics(gameId)
                }
                FRCKrawlerTabBar(
                    navigation = Screen.Metrics(gameId),
                    currentScreen = currentScreen
                ) { screen ->
                    navController.navigate(screen.route) {
                        popUpTo(Screen.Metrics(gameId).route) { inclusive = true }
                        launchSingleTop = true
                    }
                }

                if (viewModel.metrics.isEmpty()) {
                    EmptyBackground()
                } else {
                    MetricListContent(
                        modifier = Modifier.padding(contentPadding),
                        metrics = viewModel.metrics,
                        onMetricClick = {
                            // TODO edit metric
                        },
                    )
                }
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
            contentDescription = "Background",
            tint = MaterialTheme.colors.secondary
        )
        Text(text = "No Metrics", style = MaterialTheme.typography.h4)
    }
}

@Composable
private fun MetricActions(onAddClick: () -> Unit) {
    FloatingActionButton(
        onClick = { onAddClick() }

    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Manual Metric Add"
        )
    }
}

@Composable
private fun MetricListContent(
    modifier: Modifier = Modifier,
    metrics: List<Metric>,
    onMetricClick: (Metric) -> Unit,
) {
    LazyColumn(modifier) {
        items(metrics) { metric ->
            MetricListRow(
                metric = metric,
                onMetricClick = onMetricClick
            )
        }
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
            .fillMaxWidth()
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

@Preview
@Composable
private fun MetricListRowPreview() {
    FrcKrawlerTheme {
        Surface {
            MetricListRow(
                metric = Metric.CounterMetric(
                    id = 1,
                    name = "Number of LEDs",
                    category = MetricCategory.Match,
                    priority = 1,
                    enabled = true,
                    range = 1..1000 step 5
                ),
                onMetricClick = {}
            )
        }
    }
}