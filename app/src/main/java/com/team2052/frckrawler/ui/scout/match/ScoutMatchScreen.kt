package com.team2052.frckrawler.ui.scout.match

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.local.TeamAtEvent
import com.team2052.frckrawler.data.model.Metric
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import com.team2052.frckrawler.ui.common.StepControl
import com.team2052.frckrawler.ui.components.FRCKrawlerAppBar
import com.team2052.frckrawler.ui.components.FRCKrawlerScaffold
import com.team2052.frckrawler.ui.components.fields.FRCKrawlerDropdown
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import com.team2052.frckrawler.ui.theme.secondarySurface

@Composable
fun ScoutMatchScreen(
    navController: NavController,
    metricSetId: Int,
    eventId: Int,
) {
    val viewModel: ScoutMatchViewModel = hiltViewModel()

    LaunchedEffect(true) {
        viewModel.loadMetricsAndTeams(
            metricSetId = metricSetId,
            eventId = eventId
        )
    }

    val state by viewModel.state.collectAsState()

    FRCKrawlerScaffold(
        appBar = {
            FRCKrawlerAppBar(
                navController = navController,
                title = {
                    Text(stringResource(R.string.scout_screen_title))
                }
            )
        },
    ) { contentPadding ->
        state?.let { state ->
            ScoutingForm(
                header = {
                    MatchInfo(
                        state = state.matchInformation,
                        onMatchChanged = viewModel::updateMatchNumber,
                        onTeamChanged = viewModel::updateTeam
                    )
                },
                metrics = state.metricStates,
                onMetricStateChanged = viewModel::updateMetricState,
            )
        }

    }
}

@Composable
private fun MatchInfo(
    state: MatchInformationState,
    onMatchChanged: (Int) -> Unit,
    onTeamChanged: (TeamAtEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colors.secondarySurface
            )
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.match_scout_match_info_title),
            style = MaterialTheme.typography.h6
        )

        Spacer(Modifier.height(16.dp))

        Row {
            Column {
                Text(
                    text = stringResource(R.string.match_scout_match_number_label),
                    style = MaterialTheme.typography.subtitle2
                )
                StepControl(
                    value = state.matchNumber,
                    onValueChanged = onMatchChanged
                )
            }

            Spacer(Modifier.width(16.dp))

            FRCKrawlerDropdown(
                value = state.selectedTeam,
                getLabel = { team ->
                    team?.let {
                        "${team.number} - ${team.name}"
                    } ?: ""
                },
                onValueChange = { newTeam ->
                    newTeam?.let { onTeamChanged(it) }
                },
                label = stringResource(R.string.match_scout_team_label),
                dropdownItems = state.teams,
            )
        }
    }
}

@FrcKrawlerPreview
@Composable
private fun ScoutMatchPreview() {
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

    val demoMatchInfo = MatchInformationState(
        matchNumber = 8,
        teams = listOf(
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
        )
    )

    FrcKrawlerTheme {
        Surface {
            ScoutingForm(
                header = {
                    MatchInfo(
                        state = demoMatchInfo,
                        onMatchChanged = {},
                        onTeamChanged = {},
                    )
                },
                metrics = demoMetrics,
                onMetricStateChanged = {},
            )
        }
    }
}