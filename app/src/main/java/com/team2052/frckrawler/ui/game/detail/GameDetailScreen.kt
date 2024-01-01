package com.team2052.frckrawler.ui.game.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.local.Event
import com.team2052.frckrawler.data.local.MetricSet
import com.team2052.frckrawler.ui.components.FRCKrawlerAppBar
import com.team2052.frckrawler.ui.components.FRCKrawlerDrawer
import com.team2052.frckrawler.ui.components.FRCKrawlerScaffold
import com.team2052.frckrawler.ui.game.AddMetricSetDialog
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun GameDetailScreen(
    gameId: Int,
    navController: NavController,
) {
    val scaffoldState = rememberScaffoldState()
    val viewModel: GameDetailViewModel = hiltViewModel()
    val game by viewModel.game.collectAsState()

    var showAddEvent by remember { mutableStateOf(false) }
    var showAddMetricSet by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        viewModel.loadGame(gameId)
    }

    FRCKrawlerScaffold(
        scaffoldState = scaffoldState,
        appBar = {
            FRCKrawlerAppBar(
                navController = navController,
                scaffoldState = scaffoldState,
                title = {
                    Text(game?.name ?: "")
                }
            )
        },
        drawerContent = {
            FRCKrawlerDrawer()
        },
    ) { contentPadding ->
        val events by viewModel.events.collectAsState()
        val metricSets by viewModel.metricSets.collectAsState()
        GameDetailContent(
            modifier = Modifier.padding(contentPadding),
            events = events,
            onAddEventClick = { showAddEvent = true },
            metricSets = metricSets,
            onAddMetricSetClick = { showAddMetricSet = true },
        )

        if (showAddMetricSet) {
            AddMetricSetDialog(
                onAddMetricSet = { name ->
                    viewModel.createNewMetricSet(name)
                },
                onClose = { showAddMetricSet = false }
            )
        }
    }
}

@Composable
private fun GameDetailContent(
    events: List<Event>,
    onAddEventClick: () -> Unit,
    metricSets: List<MetricSet>,
    onAddMetricSetClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
      modifier = modifier.padding(16.dp)
    ) {
        EventListCard(
            modifier = Modifier.fillMaxWidth(),
            onAddEventClick = onAddEventClick,
            events = events
        )
        Spacer(Modifier.height(16.dp))
        MetricSetsCard(
            modifier = Modifier.fillMaxWidth(),
            onAddMetricSetClick = onAddMetricSetClick,
            metricSets = metricSets
        )
    }
}

@Composable
private fun EventListCard(
    events: List<Event>,
    onAddEventClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GameDetailCardLayout(
        modifier = modifier,
        title = stringResource(R.string.game_detail_event_card_title),
        onAddClicked = onAddEventClick
    ) {
        if (events.isNotEmpty()) {
            events.forEach { event ->
                EventRow(event = event)
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
    event: Event,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = event.name,
            style = MaterialTheme.typography.subtitle1
        )
    }
}

@Composable
private fun MetricSetsCard(
    metricSets: List<MetricSet>,
    onAddMetricSetClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GameDetailCardLayout(
        modifier = modifier,
        title = stringResource(R.string.game_detail_metrics_card_title),
        onAddClicked = onAddMetricSetClick
    ) {
        if (metricSets.isNotEmpty()) {
            metricSets.forEach { set ->
                MetricSetRow(metricSet = set)
                if (set != metricSets.last()) {
                    Divider()
                }
            }
        } else {
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = stringResource(R.string.game_detail_no_metric_sets),
                fontStyle = FontStyle.Italic
            )
        }
    }
}

@Composable
private fun MetricSetRow(
    metricSet: MetricSet,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = metricSet.name,
            style = MaterialTheme.typography.subtitle1
        )
    }
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
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
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

@Preview
@Composable
private fun GameDetailPreview() {
    FrcKrawlerTheme {
        Surface {
            GameDetailContent(
                events = listOf(
                    Event(
                        name = "10,000 Lakes Regional",
                        gameId = 0,
                    ),
                    Event(
                        name = "Lake Superior Regional",
                        gameId = 0,
                    )
                ),
                onAddEventClick = {},
                metricSets = listOf(
                    MetricSet(
                        name = "Regional metrics",
                        gameId = 0
                    )
                ),
                onAddMetricSetClick = {},
            )
        }
    }
}

@Preview
@Composable
private fun GameDetailEmptyPreview() {
    FrcKrawlerTheme {
        Surface {
            GameDetailContent(
                events = emptyList(),
                onAddEventClick = {},
                metricSets = emptyList(),
                onAddMetricSetClick = {},
            )
        }
    }
}