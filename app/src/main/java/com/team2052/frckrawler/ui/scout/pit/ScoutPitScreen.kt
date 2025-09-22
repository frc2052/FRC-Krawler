package com.team2052.frckrawler.ui.scout.pit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.local.TeamAtEvent
import com.team2052.frckrawler.data.model.Metric
import com.team2052.frckrawler.data.model.MetricState
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import com.team2052.frckrawler.ui.components.FRCKrawlerAppBar
import com.team2052.frckrawler.ui.components.fields.FRCKrawlerDropdown
import com.team2052.frckrawler.ui.scout.AnimatedSaveButton
import com.team2052.frckrawler.ui.scout.ScoutingForm
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun ScoutPitScreen(
  backStack: NavBackStack<NavKey>,
  metricSetId: Int,
  eventId: Int,
) {
  val viewModel: ScoutPitViewModel = hiltViewModel()

  LaunchedEffect(metricSetId, eventId) {
    viewModel.loadMetricsAndTeams(
      metricSetId = metricSetId,
      eventId = eventId
    )
  }

  val state by viewModel.state.collectAsState()

  Scaffold(
    topBar = {
      FRCKrawlerAppBar(
        backStack = backStack,
        title = {
          Text(stringResource(R.string.scout_screen_title))
        }
      )
    },
    floatingActionButton = {
      AnimatedSaveButton(
        onSave = viewModel::saveMetricData
      )
    },
  ) { contentPadding ->
    state?.let { state ->
      ScoutingForm(
        header = {
          TeamInfo(
            modifier = Modifier.fillMaxWidth(),
            availableTeams = state.availableTeams,
            selectedTeam = state.selectedTeam,
            onTeamChanged = viewModel::updateTeam
          )
        },
        metrics = state.metricStates,
        onMetricStateChanged = viewModel::updateMetricState,
        contentPadding = contentPadding,
      )
    }

  }
}

@Composable
private fun TeamInfo(
  availableTeams: List<TeamAtEvent>,
  selectedTeam: TeamAtEvent,
  onTeamChanged: (TeamAtEvent) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
        .background(
            color = MaterialTheme.colorScheme.surfaceContainerLow
        )
        .padding(16.dp)
  ) {
    Text(
      text = stringResource(R.string.pit_scout_team_info_title),
      style = MaterialTheme.typography.titleLarge
    )

    Spacer(Modifier.height(16.dp))

    FRCKrawlerDropdown(
      value = selectedTeam,
      getLabel = { team ->
        team?.let {
          "${team.number} - ${team.name}"
        } ?: ""
      },
      onValueChange = { newTeam ->
        newTeam?.let { onTeamChanged(it) }
      },
      label = stringResource(R.string.scout_team_label),
      dropdownItems = availableTeams,
    )
  }
}

@FrcKrawlerPreview
@Composable
private fun ScoutPitPreview() {
  val demoMetrics = listOf(
    MetricState(
      metric = Metric.SliderMetric(
        name = "Sample metric",
        priority = 1,
        enabled = true,
        range = 0..10 step 1
      ),
      value = "10"
    ),
    MetricState(
      metric = Metric.SliderMetric(
        name = "Sample metric",
        priority = 1,
        enabled = true,
        range = 0..10 step 1
      ),
      value = "10"
    )
  )

  FrcKrawlerTheme {
    Surface {
      ScoutingForm(
        header = {
          TeamInfo(
            availableTeams = listOf(
              TeamAtEvent(
                number = "2052",
                name = "KnightKrawler",
                eventId = 0
              )
            ),
            selectedTeam = TeamAtEvent(
              number = "2052",
              name = "KnightKrawler",
              eventId = 0
            ),
            onTeamChanged = {},
          )
        },
        metrics = demoMetrics,
        onMetricStateChanged = {},
      )
    }
  }
}