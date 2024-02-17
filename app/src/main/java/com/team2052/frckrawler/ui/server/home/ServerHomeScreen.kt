package com.team2052.frckrawler.ui.server.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.model.DeviceType
import com.team2052.frckrawler.ui.RequestEnableBluetooth
import com.team2052.frckrawler.ui.components.FRCKrawlerAppBar
import com.team2052.frckrawler.ui.components.FRCKrawlerScaffold
import com.team2052.frckrawler.ui.permissions.BluetoothPermissionRequestDialogs
import com.team2052.frckrawler.ui.theme.spaceLarge

@Composable
fun ServerHomeScreen(
  gameId: Int,
  eventId: Int,
  modifier: Modifier = Modifier,
  navController: NavController,
) {
  val viewModel: ServerHomeViewModel = hiltViewModel()

  Box {

    if (viewModel.showPermissionRequests) {
      BluetoothPermissionRequestDialogs(
        deviceType = DeviceType.Server,
        onAllPermissionsGranted = { viewModel.startServer() },
        onCanceled = { viewModel.showPermissionRequests = false }
      )
    }

    if (viewModel.requestEnableBluetooth) {
      RequestEnableBluetooth(
        deviceType = DeviceType.Server,
        onEnabled = { viewModel.startServer() },
        onCanceled = { viewModel.requestEnableBluetooth = false }
      )
    }

    LaunchedEffect(true) {
      viewModel.loadGameAndEvent(gameId, eventId)
    }

    FRCKrawlerScaffold(
      modifier = modifier,
      appBar = {
        FRCKrawlerAppBar(
          navController = navController,
          title = {
            Text(stringResource(R.string.server_screen_title))
          }
        )
      },
    ) {
      Column(
        modifier = Modifier
          .padding(spaceLarge)
      ) {
        ServerConfigCard(
          modifier = modifier,
          serverState = viewModel.serverState,
          event = viewModel.event,
          game = viewModel.game,
          toggleServer = {
            if (viewModel.serverState == ServerState.ENABLED) {
              viewModel.stopServer()
            } else {
              viewModel.startServer()
            }
          },
        )

        Spacer(Modifier.height(16.dp))

        ConnectedScoutsList(
          modifier = modifier,
          scouts = viewModel.connectedScouts
        )
      }
    }
  }
}