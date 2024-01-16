package com.team2052.frckrawler.ui.scout.match

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.team2052.frckrawler.ui.metrics.MetricInput
import com.team2052.frckrawler.ui.navigation.Arguments.gameId
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import com.team2052.frckrawler.ui.theme.secondarySurface

@Composable
fun ScoutMatchScreen(
    navController: NavController,
    metricSetId: Int,
) {
    val viewModel: ScoutMatchViewModel = hiltViewModel()

    LaunchedEffect(true) {
        viewModel.loadGame(gameId)
    }

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
        ScoutingForm(
            header = {
                MatchInfo(
                    matchNumber = ,
                    onMatchChanged = ,
                    teams = ,
                    selectedTeam = ,
                    onTeamChanged = 
                )
            },
            metrics =,
            onMetricStateChanged = ,
        )

    }
}

@Composable
private fun MatchInfo(
    matchNumber: Int,
    onMatchChanged: (Int) -> Unit,
    teams: List<TeamAtEvent>,
    selectedTeam: TeamAtEvent,
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
                    value = matchNumber,
                    onValueChanged = onMatchChanged
                )
            }

            Spacer(Modifier.width(16.dp))

            FRCKrawlerDropdown(
                value = selectedTeam,
                getLabel = { "${selectedTeam.number} - ${selectedTeam.name}" },
                onValueChange = { newTeam ->
                    newTeam?.let { onTeamChanged(it) }
                },
                label = stringResource(R.string.match_scout_team_label),
                dropdownItems = teams,
            )
        }
    }
}

@FrcKrawlerPreview
@Composable
private fun ScoutMatchPreview() {
    FrcKrawlerTheme {
        Surface {
            ScoutMatchScreenContent(
                matchNumber = 23,
                onMatchChanged = {},
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
                ),
                onTeamChanged = {},
                metrics = listOf(
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
                ),
                onMetricStateChanged = {}
            )
        }
    }
}