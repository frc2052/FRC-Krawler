package com.team2052.frckrawler.ui.analyze.team

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation3.runtime.NavBackStack
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.local.Event
import com.team2052.frckrawler.data.model.Metric
import com.team2052.frckrawler.data.summary.DoubleSummaryValue
import com.team2052.frckrawler.data.summary.SummaryValue
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import com.team2052.frckrawler.ui.components.FRCKrawlerAppBar
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun TeamDataScreen(
  teamNumber: String,
  backStack: NavBackStack,
  modifier: Modifier = Modifier
) {
  val viewModel: TeamDataViewModel = hiltViewModel()
  val state = viewModel.state.collectAsState().value

  LaunchedEffect(teamNumber) {
    viewModel.loadTeamData(teamNumber)
  }

  Scaffold(
    modifier = modifier,
    topBar = {
      FRCKrawlerAppBar(
        backStack = backStack,
        title = {
          if (state is TeamDataScreenState.Content) {
            Text("${state.teamNumber} - ${state.teamName}")
          }
        },
      )
    },
  ) { contentPadding ->
    when (state) {
      is TeamDataScreenState.Content -> {
        TeamDataContent(
          content = state,
          modifier = Modifier.padding(contentPadding)
        )
      }
      is TeamDataScreenState.NoData -> {
        EmptyBackground(Modifier.padding(contentPadding))
      }
      else -> {
        // Loading
      }
    }
  }
}

@Composable
private fun TeamDataContent(
  content: TeamDataScreenState.Content,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier.verticalScroll(rememberScrollState())
  ) {
    content.data.forEach { (event, data) ->
      TeamEventSummary(event, data)
    }
  }
}

@Composable
private fun TeamEventSummary(
  event: Event,
  data: Map<Metric, SummaryValue>,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
  ) {
    Text(
      text = event.name,
      style = MaterialTheme.typography.headlineMedium,
    )

    data.forEach { (metric, summary) ->
      Row {
        Text(
          text = "${metric.name}:",
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.width(4.dp))
        Text(
          text = summary.asDisplayString(),
          style = MaterialTheme.typography.bodyMedium,
        )
      }
    }
  }
}


@Composable
private fun EmptyBackground(
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Icon(
      modifier = Modifier.size(128.dp),
      imageVector = Icons.Filled.Analytics,
      tint = MaterialTheme.colorScheme.outlineVariant,
      contentDescription = null
    )
    Text(
      text = stringResource(R.string.analyze_team_no_data),
      style = MaterialTheme.typography.headlineMedium
    )
  }
}

@FrcKrawlerPreview
@Composable
private fun TeamDataPreview() {
  val event1 = Event(
    name = "Northern Lights Regional",
    gameId = 0
  )
  val event2 = Event(
    name = "Lake Superior Regional",
    gameId = 0
  )
  val metric1 = Metric.CounterMetric(
    name = "Number of wheels",
    priority = 1,
    enabled = true,
    range = 0..10,
  )
  val metric2 = Metric.BooleanMetric(
    name = "Has wheels",
    priority = 1,
    enabled = true,
  )
  val previewData = mapOf(
    event1 to mapOf(
      metric1 to DoubleSummaryValue(4.0, isPercent = false),
      metric2 to DoubleSummaryValue(100.0, isPercent = true)
    ),

    event2 to mapOf(
      metric1 to DoubleSummaryValue(4.0, isPercent = false),
      metric2 to DoubleSummaryValue(100.0, isPercent = true)
    )
  )

  FrcKrawlerTheme {
    Surface {
      TeamDataContent(
        content = TeamDataScreenState.Content(
          teamNumber = "2052",
          teamName = "The TechnoKats",
          data = previewData
        ),
        modifier = Modifier.fillMaxSize()
      )
    }
  }
}