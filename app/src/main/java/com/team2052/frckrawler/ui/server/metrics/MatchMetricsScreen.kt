package com.team2052.frckrawler.ui.server.metrics

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.local.Metric
import com.team2052.frckrawler.ui.components.FRCKrawlerAppBar
import com.team2052.frckrawler.ui.components.FRCKrawlerDrawer
import com.team2052.frckrawler.ui.components.FRCKrawlerScaffold
import com.team2052.frckrawler.ui.components.FRCKrawlerTabBar
import com.team2052.frckrawler.ui.components.fields.FRCKrawlerTextField
import com.team2052.frckrawler.ui.navigation.Screen
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun MatchMetricsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val scaffoldState = rememberScaffoldState()
    val viewModel: MatchMetricsViewModel = hiltViewModel()

    var addMetricsDialogOpen by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        viewModel.loadMetrics()
    }

    FRCKrawlerScaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        appBar = {
            FRCKrawlerAppBar(
                navController = navController,
                scaffoldState = scaffoldState,
                title = {
                    Text(
                        stringResource(
                            R.string.metrics_screen
                        )
                    )
                }
            )
        },
        tabBar = {
            FRCKrawlerTabBar(navigation = Screen.Metrics, currentScreen = Screen.MatchMetrics) { screen ->
                navController.navigate(screen.route) {
                    popUpTo(Screen.Metrics.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
        },
        floatingActionButton = {
            MetricActions(
                onOpen = { addMetricsDialogOpen = true }
            )
        },
        drawerContent = {
            FRCKrawlerDrawer()
        },
        background = {
            if (viewModel.metrics.isEmpty()) {
                EmptyBackground()
            }
        }
    ) { contentPadding ->
        ServerSeasonsMetricContent(
            modifier = Modifier.padding(contentPadding),
            listOfMetrics = viewModel.metrics,
            onOpen = {},
            navController = navController
        )
        if (addMetricsDialogOpen) {
            AddMetricDialog(
                onAddMetric = { newMetric -> viewModel.makeMetric(newMetric) },
                onClose = { addMetricsDialogOpen = false},
            )
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
private fun MetricActions(onOpen: () -> Unit) {
    var fabExpanded by remember { mutableStateOf(false) }
    Column {
        if (fabExpanded) {
            val fabModifier = Modifier.padding(bottom = 24.dp)
            FloatingActionButton(
                modifier = fabModifier,
                onClick = { onOpen() }

            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Manual Metric Add"
                )
            }
            FloatingActionButton(
                modifier = fabModifier,
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Manual Metric Add"
                )
            }
        }
        FloatingActionButton(
            onClick = {
                fabExpanded = !fabExpanded
            }
        ) {
           if (!fabExpanded) {
               Icon(
                   imageVector = Icons.Filled.Menu,
                   contentDescription = "Manual Metric Add"
               )
           } else {
               Icon(
                   imageVector = Icons.Filled.Close,
                   contentDescription = "Manual Metric Add"
               )
           }
        }
    }
}

@Composable
private fun AddMetricDialog(
    onAddMetric: (String) -> Unit,
    onClose: () -> Unit
) {
    var metricName by remember { mutableStateOf("") }
    AlertDialog(
        modifier = Modifier.fillMaxWidth(0.75f),
        onDismissRequest = { onClose() },
        title = { Text("Add New Metric") },
        text = {
            FRCKrawlerTextField(
                modifier = Modifier.padding(top = 24.dp),
                value = metricName,
                onValueChange = { metricName = it},
                label = "Name"
            )
        },
        buttons = {
            ProvideTextStyle(
                LocalTextStyle.current.copy(
                    color = MaterialTheme.colors.secondary
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        modifier = Modifier.padding(12.dp),
                        onClick = {
                            onClose()
                        }
                    ) {
                        Text("CANCEL")
                    }
                    TextButton(
                        modifier = Modifier.padding(12.dp),
                        onClick = {
                            onAddMetric(metricName)
                            onClose()
                        }
                    ) {
                        Text("SAVE")
                    }
                }
            }
        }
    )
}

@Composable
fun ServerSeasonsMetricContent(
    modifier: Modifier = Modifier,
    listOfMetrics: List<Metric>,
    onOpen: () -> Unit,
    navController: NavController
) {
    Column(modifier = modifier.fillMaxWidth()) {
        listOfMetrics.forEach { metric ->
            TextButton(
                modifier = Modifier.padding(10.dp),
                onClick = {
                    onOpen()
                    navController.navigate(Screen.MatchMetrics.route)
                }
            ) {
                Text(
                    text = metric.name,
                    style = MaterialTheme.typography.button
                )
            }
            Divider()
        }
    }
}

@Preview
@Composable
private fun ServerSeasonsScreenPreviewLight() {
    FrcKrawlerTheme(
        darkTheme = false
    ) {
        MatchMetricsScreen(navController = rememberNavController())
    }
}

@Preview
@Composable
private fun ServerSeasonsScreenPreviewDark() {
    FrcKrawlerTheme(
        darkTheme = true
    ) {
        MatchMetricsScreen(navController = rememberNavController())
    }
}