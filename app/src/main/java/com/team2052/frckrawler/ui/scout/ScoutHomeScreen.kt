package com.team2052.frckrawler.ui.scout

import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.team2052.frckrawler.data.model.DeviceType
import com.team2052.frckrawler.ui.RequestEnableBluetooth
import com.team2052.frckrawler.ui.components.*
import com.team2052.frckrawler.ui.navigation.Screen.*
import com.team2052.frckrawler.ui.permissions.BluetoothPermissionRequestDialogs
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import com.team2052.frckrawler.ui.theme.spaceLarge

@Composable
fun ScoutHomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val viewModel: ScoutViewModel = hiltViewModel()
    val scaffoldState = rememberScaffoldState()

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
            scaffoldState = scaffoldState,
            appBar = {
                FRCKrawlerAppBar(
                    navController = navController,
                    scaffoldState = scaffoldState,
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
            drawerContent = {
                FRCKrawlerDrawer()
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

@Preview
@Composable
private fun ScoutScreenPreviewLight() {
    FrcKrawlerTheme(darkTheme = false) {
        ScoutHomeScreen(navController = rememberNavController())
    }
}

@Preview
@Composable
private fun ScoutScreenPreviewDark() {
    FrcKrawlerTheme(darkTheme = true) {
        ScoutHomeScreen(navController = rememberNavController())
    }
}