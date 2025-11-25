package com.team2052.frckrawler.ui.server.home

import android.Manifest
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Hardware
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.model.DeviceType
import com.team2052.frckrawler.di.viewmodel.metroViewModel
import com.team2052.frckrawler.ui.RequestEnableBluetooth
import com.team2052.frckrawler.ui.components.FRCKrawlerAppBar
import com.team2052.frckrawler.ui.components.StartScoutingCard
import com.team2052.frckrawler.ui.navigation.Screen
import com.team2052.frckrawler.ui.permissions.BluetoothPermissionRequestDialogs
import com.team2052.frckrawler.ui.theme.spaceLarge

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ServerHomeScreen(
  gameId: Int,
  eventId: Int,
  modifier: Modifier = Modifier,
  backStack: NavBackStack<NavKey>,
) {
  val viewModel: ServerHomeViewModel = metroViewModel()

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

    LaunchedEffect(gameId, eventId) {
      viewModel.loadGameAndEvent(gameId, eventId)
    }

    Scaffold(
      modifier = modifier,
      topBar = {
        FRCKrawlerAppBar(
          backStack = backStack,
          title = {
            Text(stringResource(R.string.server_screen_title))
          }
        )
      },
    ) { contentPadding ->
      Column(
        modifier = Modifier
          .verticalScroll(rememberScrollState())
          .padding(contentPadding)
          .consumeWindowInsets(contentPadding)
          .padding(spaceLarge)
      ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
          val hasDismissed by viewModel.hasDismissedNotificationPrompt.collectAsState(true)
          val permissionState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
          AnimatedVisibility(!hasDismissed && !permissionState.status.isGranted) {
            NotificationPromptCard(
              modifier = modifier.fillMaxWidth(),
              onDismiss = { viewModel.dismissNotificationPrompt() },
              onAccept = {
                permissionState.launchPermissionRequest()
                viewModel.dismissNotificationPrompt()
             },
            )
            Spacer(Modifier.height(16.dp))
          }
        }

        val state by viewModel.serverState.collectAsState(ServerState.Disabled)
        ServerConfigCard(
          modifier = modifier,
          serverState = state,
          event = viewModel.event,
          game = viewModel.game,
          toggleServer = {
            if (state is ServerState.Enabled) {
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

        Spacer(Modifier.height(16.dp))

        StartScoutingCard(
          icon = Icons.Default.EmojiEvents,
          label = stringResource(R.string.remote_scout_start_match_scouting),
          onClick = {
            backStack.add(
              Screen.MatchScout(
                eventId = viewModel.event?.id!!,
                metricSetId = viewModel.game?.matchMetricsSetId!!
              )
            )
          },
          enabled = viewModel.event != null && viewModel.game?.matchMetricsSetId != null,
        )

        Spacer(Modifier.height(16.dp))

        StartScoutingCard(
          icon = Icons.Default.Hardware,
          label = stringResource(R.string.remote_scout_start_pit_scouting),
          onClick = {
            backStack.add(
              Screen.MatchScout(
                eventId = viewModel.event?.id!!,
                metricSetId = viewModel.game?.pitMetricsSetId!!
              )
            )
          },
          enabled = viewModel.event != null && viewModel.game?.pitMetricsSetId != null,
        )
      }
    }
  }
}