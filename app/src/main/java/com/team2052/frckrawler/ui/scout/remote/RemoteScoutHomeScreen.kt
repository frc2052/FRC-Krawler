package com.team2052.frckrawler.ui.scout.remote

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Hardware
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.local.Event
import com.team2052.frckrawler.data.local.MetricSet
import com.team2052.frckrawler.data.model.DeviceType
import com.team2052.frckrawler.ui.RequestEnableBluetooth
import com.team2052.frckrawler.ui.components.FRCKrawlerAppBar
import com.team2052.frckrawler.ui.components.FRCKrawlerScaffold
import com.team2052.frckrawler.ui.navigation.Screen
import com.team2052.frckrawler.ui.permissions.BluetoothPermissionRequestDialogs
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import com.team2052.frckrawler.ui.theme.spaceLarge

@Composable
fun ScoutHomeScreen(
  modifier: Modifier = Modifier,
  navController: NavController,
) {
  val viewModel: RemoteScoutViewModel = hiltViewModel()

  // Don't love this, but it is what we need
  val context = LocalContext.current as ComponentActivity

  Box {
    if (viewModel.showPermissionRequests) {
      BluetoothPermissionRequestDialogs(
        deviceType = DeviceType.Client,
        onAllPermissionsGranted = { viewModel.connectToServer(context) },
        onCanceled = { viewModel.showPermissionRequests = false }
      )
    }

    if (viewModel.requestEnableBluetooth) {
      RequestEnableBluetooth(
        deviceType = DeviceType.Client,
        onEnabled = { viewModel.connectToServer(context) },
        onCanceled = { viewModel.requestEnableBluetooth = false }
      )
    }

    Scaffold(
      modifier = modifier,
      topBar = {
        FRCKrawlerAppBar(
          navController = navController,
          title = {
            Text(stringResource(R.string.scout_screen_title))
          }
        )
      },
    ) { contentPadding ->
      val hasMatchMetrics by viewModel.hasMatchMetrics.collectAsState()
      val hasPitMetrics by viewModel.hasPitMetrics.collectAsState()
      val syncState by viewModel.syncState.collectAsState()
      ScoutHomeScreenContent(
        modifier = Modifier.padding(contentPadding)
          .consumeWindowInsets(contentPadding),
        hasMatchMetrics = hasMatchMetrics,
        onStartMatchScouting = {
          navController.navigate(
            Screen.MatchScout(
              eventId = Event.SCOUT_EVENT_ID,
              metricSetId = MetricSet.SCOUT_MATCH_METRIC_SET_ID
            ).route
          )
        },
        hasPitMetrics = hasPitMetrics,
        onStartPitScouting = {
          navController.navigate(
            Screen.MatchScout(
              eventId = Event.SCOUT_EVENT_ID,
              metricSetId = MetricSet.SCOUT_PIT_METRIC_SET_ID
            ).route
          )
        },
        serverStatus = {
          RemoteScoutServerStatusCard(
            serverState = viewModel.serverConnectionState,
            syncState = syncState,
            onFindServerClicked = { viewModel.connectToServer(context) },
            onSyncClicked  = { viewModel.performSync() },
          )
        },
      )
    }
  }
}

@Composable
private fun ScoutHomeScreenContent(
  hasMatchMetrics: Boolean,
  onStartMatchScouting: () -> Unit,
  hasPitMetrics: Boolean,
  onStartPitScouting: () -> Unit,
  modifier: Modifier = Modifier,
  serverStatus: @Composable () -> Unit,
) {
  Column(modifier = modifier.padding(spaceLarge)) {
    serverStatus()

    Spacer(Modifier.height(16.dp))

    StartScoutingCard(
      icon = Icons.Default.EmojiEvents,
      label = stringResource(R.string.remote_scout_start_match_scouting),
      onClick = onStartMatchScouting,
      enabled = hasMatchMetrics,
    )

    Spacer(Modifier.height(16.dp))

    StartScoutingCard(
      icon = Icons.Default.Hardware,
      label = stringResource(R.string.remote_scout_start_pit_scouting),
      onClick = onStartPitScouting,
      enabled = hasPitMetrics,
    )
  }
}

@Composable
private fun StartScoutingCard(
  icon: ImageVector,
  label: String,
  onClick: () -> Unit,
  enabled: Boolean,
  modifier: Modifier = Modifier,
) {
  Card(
    modifier = modifier,
    enabled = enabled,
    onClick = onClick,
  ) {
    StartScoutingCardContent(
      icon = icon,
      label = label
    )
  }
}

@Composable
private fun StartScoutingCardContent(
  icon: ImageVector,
  label: String,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      modifier = Modifier.size(36.dp),
      imageVector = icon,
      contentDescription = null
    )

    Spacer(Modifier.width(24.dp))

    Text(
      text = label,
      style = MaterialTheme.typography.headlineSmall
    )
  }
}

@Preview
@Composable
private fun StartScoutingCardPreview() {
  FrcKrawlerTheme {
    Surface {
      StartScoutingCard(
        icon = Icons.Default.EmojiEvents,
        label = "Start match scouting",
        onClick = { /*TODO*/ },
        enabled = true,
      )
    }
  }
}

@Preview
@Composable
private fun StartScoutingCardDisabledPreview() {
  FrcKrawlerTheme {
    Surface {
      StartScoutingCard(
        icon = Icons.Default.EmojiEvents,
        label = "Start match scouting",
        onClick = { /*TODO*/ },
        enabled = false,
      )
    }
  }
}