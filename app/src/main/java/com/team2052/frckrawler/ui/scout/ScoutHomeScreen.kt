package com.team2052.frckrawler.ui.scout

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.team2052.frckrawler.data.model.DeviceType
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import com.team2052.frckrawler.ui.RequestEnableBluetooth
import com.team2052.frckrawler.ui.components.Card
import com.team2052.frckrawler.ui.components.CardHeader
import com.team2052.frckrawler.ui.components.FRCKrawlerAppBar
import com.team2052.frckrawler.ui.components.FRCKrawlerScaffold
import com.team2052.frckrawler.ui.components.FRCKrawlerTabBar
import com.team2052.frckrawler.ui.navigation.Screen.Scout
import com.team2052.frckrawler.ui.navigation.Screen.ScoutHome
import com.team2052.frckrawler.ui.permissions.BluetoothPermissionRequestDialogs
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import com.team2052.frckrawler.ui.theme.spaceLarge

@Composable
fun ScoutHomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val viewModel: ScoutViewModel = hiltViewModel()

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
            tabBar = {
                FRCKrawlerTabBar(navigation = Scout, currentScreen = ScoutHome) { screen ->
                    navController.navigate(screen.route) {
                        launchSingleTop = true
                    }
                }
            },
        ) { contentPadding ->
            ScoutHomeScreenContent(
                modifier = modifier.padding(contentPadding),
                serverState = viewModel.serverConnectionState,
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
    onFindServerClicked: () -> Unit,
    onSyncClicked: () -> Unit,
) {
    Column(modifier = modifier.padding(spaceLarge)) {
        ConnectionStatusCard(
            state = serverState,
            onFindServerClicked = onFindServerClicked
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onSyncClicked) {
            Text("Sync")
        }
    }
}

@Composable
private fun ConnectionStatusCard(
    modifier: Modifier = Modifier,
    state: ServerConnectionState,
    onFindServerClicked: () -> Unit
) {
    Card(
        modifier = modifier,
        header = {
            CardHeader(
                title = { Text("Server Connection") }
            )
        },
    ) {
        when (state) {
            is ServerConnectionState.Connected -> {
                ServerConnected(state)
            }
            is ServerConnectionState.Connecting -> {
                ServerConnecting()
            }
            else -> {
                ServerNotConnected(
                    state = state,
                    onFindServerClicked = onFindServerClicked
                )
            }
        }
    }
}

@Composable
private fun ServerConnected(state: ServerConnectionState.Connected) {
    Text("Connected to ${state.name}")
}


@Composable
private fun ServerConnecting() {
    Row {
        CircularProgressIndicator(
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text("Connecting...")
    }
}

@Composable
private fun ServerNotConnected(
    state: ServerConnectionState,
    onFindServerClicked: () -> Unit
) {
    Column {
        Text("Connect to a server to start scouting.")

        when (state) {
            is ServerConnectionState.PairingFailed -> {
                Text(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = MaterialTheme.colors.error,
                    text = "Pairing failed, please try again."
                )
            }
            is ServerConnectionState.NoFrcKrawlerServiceFound -> {
                Text(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = MaterialTheme.colors.error,
                    text = "FRCKrawler server is not running on the selected device. " +
                      "Please ensure it is running and try again."
                )
            }
            else -> {} // No error, so no text needed
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomEnd
        ) {
            Button(
                onClick = onFindServerClicked,
            ) {
                Text("Find Server")
            }
        }
    }
}

@FrcKrawlerPreview
@Composable
private fun ScoutScreenConnectedPreview() {
    FrcKrawlerTheme {
        Surface {
            ScoutHomeScreenContent(
                serverState = ServerConnectionState.Connected(
                    "KnightKrawler Server"
                ),
                onFindServerClicked = { },
                onSyncClicked = { }
            )
        }
    }
}

@FrcKrawlerPreview
@Composable
private fun ScoutScreenNotConnectedPreview() {
    FrcKrawlerTheme {
        Surface {
            ScoutHomeScreenContent(
                serverState = ServerConnectionState.NotConnected,
                onFindServerClicked = { },
                onSyncClicked = { }
            )
        }
    }
}