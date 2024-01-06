package com.team2052.frckrawler.ui.event.teams

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.local.TeamAtEvent
import com.team2052.frckrawler.ui.components.FRCKrawlerAppBar
import com.team2052.frckrawler.ui.components.FRCKrawlerDrawer
import com.team2052.frckrawler.ui.components.FRCKrawlerScaffold
import com.team2052.frckrawler.ui.event.teams.edit.AddEditTeamSheet
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EventTeamListScreen(
    eventId: Int,
    navController: NavController,
) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val viewModel: EventDetailsViewModel = hiltViewModel()
    val editTeamSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    var teamBeingEdited: TeamAtEvent? by remember {  mutableStateOf(null) }
    val event by viewModel.event.collectAsState()
    val teams by viewModel.teams.collectAsState()
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        viewModel.loadEvent(eventId)
    }

    FRCKrawlerScaffold(
        scaffoldState = scaffoldState,
        appBar = {
            FRCKrawlerAppBar(
                navController = navController,
                scaffoldState = scaffoldState,
                title = { Text(event?.name ?: "") },
                actions = {
                    IconButton(
                        onClick = { showDeleteConfirmation = true }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.delete)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (editTeamSheetState.targetValue == ModalBottomSheetValue.Hidden) {
                Actions(
                    onAddClick = {
                        teamBeingEdited = null
                        scope.launch {
                            editTeamSheetState.show()
                        }
                    }
                )
            }
        },
        drawerContent = {
            FRCKrawlerDrawer()
        },
    ) {
        ModalBottomSheetLayout(
            sheetState = editTeamSheetState,
            sheetContent = {
                AddEditTeamSheet(
                    eventId = eventId,
                    team = teamBeingEdited,
                    onClose = {
                        scope.launch {
                            editTeamSheetState.hide()
                        }
                        teamBeingEdited = null
                    },
                )
            }
        ) {
            TeamListContent(
                teams = teams,
                onTeamClicked = { team ->
                    scope.launch {
                        teamBeingEdited = team
                        editTeamSheetState.show()
                    }
                }
            )
        }

        if (showDeleteConfirmation) {
            AlertDialog(
                title = { Text(stringResource(R.string.delete_event_title)) },
                text = { Text(stringResource(R.string.delete_event_body)) },
                onDismissRequest = { showDeleteConfirmation = false },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteEvent()
                        navController.popBackStack()
                    }) {
                        Text(stringResource(R.string.delete).uppercase())
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmation = false }) {
                        Text(stringResource(R.string.cancel).uppercase())
                    }
                },
            )
        }
    }
}

@Composable
private fun TeamListContent(
    teams: List<TeamAtEvent>,
    onTeamClicked: (TeamAtEvent) -> Unit,
) {
    LazyColumn {
        items(teams) { team ->
            TeamRow(
                team = team,
                onTeamClick = onTeamClicked
            )
        }
    }
}

@Composable
private fun TeamRow(
    team: TeamAtEvent,
    onTeamClick: (TeamAtEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onTeamClick(team) }
            .padding(vertical = 12.dp)
            .padding(start = 16.dp)
    ) {
        Text(
            text = team.number,
            style = MaterialTheme.typography.h6
        )
        Text(
            modifier = Modifier.alpha(.6f),
            text = team.name,
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
private fun Actions(onAddClick: () -> Unit) {
    val fabModifier = Modifier.padding(bottom = 24.dp)
    FloatingActionButton(
        modifier = fabModifier,
        onClick = onAddClick
    ) {
        Icon(imageVector = Icons.Filled.Add, contentDescription = stringResource(R.string.event_add_team_description))
    }
}

@Preview
@Composable
private fun TeamListPreview() {
    val teams = listOf(
        TeamAtEvent(number = "1234", name = "Test team", eventId = 0),
        TeamAtEvent(number = "1234", name = "Test team", eventId = 0),
        TeamAtEvent(number = "1234", name = "Test team", eventId = 0),
        TeamAtEvent(number = "1234", name = "Test team", eventId = 0),
    )
    FrcKrawlerTheme {
        Surface {
            TeamListContent(
                teams = teams,
                onTeamClicked = {}
            )
        }
    }
}