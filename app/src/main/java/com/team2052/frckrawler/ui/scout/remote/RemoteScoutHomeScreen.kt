package com.team2052.frckrawler.ui.scout.remote

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.team2052.frckrawler.data.model.DeviceType
import com.team2052.frckrawler.ui.RequestEnableBluetooth
import com.team2052.frckrawler.ui.components.FRCKrawlerAppBar
import com.team2052.frckrawler.ui.components.FRCKrawlerScaffold
import com.team2052.frckrawler.ui.permissions.BluetoothPermissionRequestDialogs
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

    FRCKrawlerScaffold(
      modifier = modifier,
      appBar = {
        FRCKrawlerAppBar(
          navController = navController,
          title = {
            Text("Scout")
          }
        )
      },
    ) { contentPadding ->
      val syncState by viewModel.syncState.collectAsState(initial = ServerSyncState.NotSynced)
      ScoutHomeScreenContent(
        modifier = modifier.padding(contentPadding),
        serverState = viewModel.serverConnectionState,
        syncState = syncState,
        onFindServerClicked = { viewModel.connectToServer(context) },
        onSyncClicked = { viewModel.performSync() }
      )
    }
  }
}

@Composable
private fun ScoutHomeScreenContent(
  modifier: Modifier = Modifier,
  serverState: ServerConnectionState,
  syncState: ServerSyncState,
  onFindServerClicked: () -> Unit,
  onSyncClicked: () -> Unit,
) {
  Column(modifier = modifier.padding(spaceLarge)) {
    RemoteScoutServerStatusCard(
      serverState = serverState,
      syncState = syncState,
      onFindServerClicked = onFindServerClicked,
      onSyncClicked = onSyncClicked,
    )
    Spacer(Modifier.height(16.dp))
  }
}