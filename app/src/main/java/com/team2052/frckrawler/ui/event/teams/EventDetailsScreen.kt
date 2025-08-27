package com.team2052.frckrawler.ui.event.teams

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Groups
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavBackStack
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.local.TeamAtEvent
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import com.team2052.frckrawler.ui.components.FRCKrawlerAppBar
import com.team2052.frckrawler.ui.event.teams.edit.AddEditTeamSheet
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventTeamListScreen(
  eventId: Int,
  backStack: NavBackStack,
) {
  val scope = rememberCoroutineScope()
  val viewModel: EventDetailsViewModel = hiltViewModel()
  var showEditTeamSheet by remember { mutableStateOf(false) }
  val editTeamSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var teamBeingEdited: TeamAtEvent? by remember { mutableStateOf(null) }
  val event by viewModel.event.collectAsState()
  val teams by viewModel.teams.collectAsState()
  var showDeleteConfirmation by remember { mutableStateOf(false) }

  LaunchedEffect(true) {
    viewModel.loadEvent(eventId)
  }

  Scaffold(
    topBar = {
      FRCKrawlerAppBar(
        backStack = backStack,
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
      if (editTeamSheetState.targetValue == SheetValue.Hidden) {
        Actions(
          onAddClick = {
            teamBeingEdited = null
            showEditTeamSheet = true
          }
        )
      }
    },
  ) { contentPadding ->
    if (teams.isEmpty()) {
      NoTeamsContent(
        modifier = Modifier.padding(contentPadding)
          .consumeWindowInsets(contentPadding)
      )
    } else {
      TeamListContent(
        contentPadding = contentPadding,
        teams = teams,
        onTeamClicked = { team ->
          teamBeingEdited = team
          showEditTeamSheet = true
        }
      )
    }

    if (showEditTeamSheet) {
      ModalBottomSheet(
        sheetState = editTeamSheetState,
        onDismissRequest = {
          showEditTeamSheet = false
          teamBeingEdited = null
        }
      ) {
        AddEditTeamSheet(
          eventId = eventId,
          team = teamBeingEdited,
          onClose = {
            scope.launch {
              editTeamSheetState.hide()
            }.invokeOnCompletion {
              if (!editTeamSheetState.isVisible) {
                showEditTeamSheet = false
              }
            }
            teamBeingEdited = null
          },
        )
      }
    }

    if (showDeleteConfirmation) {
      AlertDialog(
        title = { Text(stringResource(R.string.delete_event_title)) },
        text = { Text(stringResource(R.string.delete_event_body)) },
        onDismissRequest = { showDeleteConfirmation = false },
        confirmButton = {
          TextButton(onClick = {
            viewModel.deleteEvent()
            backStack.removeLastOrNull()
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
  modifier: Modifier = Modifier,
  contentPadding: PaddingValues = PaddingValues(0.dp),
  teams: List<TeamAtEvent>,
  onTeamClicked: (TeamAtEvent) -> Unit,
) {
  LazyColumn(
    modifier = modifier.consumeWindowInsets(contentPadding),
    contentPadding = contentPadding,
  ) {
    items(teams) { team ->
      TeamRow(
        team = team,
        onTeamClick = onTeamClicked
      )

      if (team != teams.last()) {
        HorizontalDivider()
      }
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
      style = MaterialTheme.typography.titleLarge
    )
    Text(
      modifier = Modifier.alpha(.6f),
      text = team.name,
      style = MaterialTheme.typography.bodyLarge
    )
  }
}

@Composable
private fun Actions(
  onAddClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  FloatingActionButton(
    modifier = modifier,
    onClick = onAddClick
  ) {
    Icon(
      imageVector = Icons.Filled.Add,
      contentDescription = stringResource(R.string.event_add_team_description)
    )
  }
}

@Composable
private fun NoTeamsContent(
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Icon(
      modifier = Modifier.size(128.dp),
      imageVector = Icons.Filled.Groups,
      tint = MaterialTheme.colorScheme.primary,
      contentDescription = null
    )
    Text(
      text = stringResource(R.string.event_no_teams),
      style = MaterialTheme.typography.headlineMedium
    )
  }
}

@FrcKrawlerPreview
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