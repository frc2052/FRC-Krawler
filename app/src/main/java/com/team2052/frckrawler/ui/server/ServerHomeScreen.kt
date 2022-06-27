package com.team2052.frckrawler.ui.server

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberPermissionState
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.model.DeviceType
import com.team2052.frckrawler.ui.RequestEnableBluetooth
import com.team2052.frckrawler.ui.navigation.Screen.*
import com.team2052.frckrawler.ui.components.*
import com.team2052.frckrawler.ui.permissions.BluetoothPermissionRequestDialogs
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * 21 - Automatically Granted
 * 22 - Automatically Granted
 * 23 - Coarse Location Needed
 * 24 - Coarse Location Needed
 * 25 - Coarse Location Needed
 * 26 - Coarse Location Needed
 * 27 - Use Companion Device Pairing
 * 28 - Use Companion Device Pairing
 * 29 - Use Companion Device Pairing
 * 30 - Use Companion Device Pairing
 */

@Composable
fun ServerHomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val viewModel: ServerViewModel = hiltViewModel()
    val scaffoldState = rememberScaffoldState()

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
        
        FRCKrawlerScaffold(
            modifier = modifier,
            scaffoldState = scaffoldState,
            appBar = {
                FRCKrawlerAppBar(
                    navController = navController,
                    scaffoldState = scaffoldState,
                    title = {
                        Text(stringResource(R.string.server_screen_title))
                    }
                )
            },
            tabBar = {
                FRCKrawlerTabBar(navigation = Server, currentScreen = ServerHome) { screen ->
                    navController.navigate(screen.route) {
                        popUpTo(ServerHome.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            },
            drawerContent = {
                FRCKrawlerDrawer()
            },
        ) { contentPadding ->
            Column(
                modifier = Modifier.padding(contentPadding)
            ) {
                ServerProperties(
                    modifier = modifier,
                    serverState = viewModel.serverState,
                    navController = navController,
                    toggleServer = {
                        if (viewModel.serverState == ServerState.ENABLED) {
                            viewModel.stopServer()
                        } else {
                            viewModel.startServer()
                        }
                    }
                )

                ScoutsList(modifier = modifier)
            }
        }
    }
}


@Composable
private fun ServerProperties(
    modifier: Modifier = Modifier,
    serverState: ServerState,
    navController: NavController,
    toggleServer: () -> Unit,
) {
    Card(
        modifier = modifier,
        header = {
            CardHeader(
                title = { Text("Server Properties") },
                description = { Text("Control server and configuration") },
            )
        },
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    navController.navigate(ModeSelect.route)
                },
            ) {
                Text(text = "Server Config")
            }

            Spacer(modifier = Modifier.width(24.dp))

            Button(
                modifier = modifier,
                enabled = serverState == ServerState.ENABLED || serverState == ServerState.DISABLED,
                onClick = toggleServer,
            ) {
                Text(
                    when (serverState) {
                        ServerState.ENABLED -> "Stop Server"
                        ServerState.ENABLING -> "Starting Server"
                        ServerState.DISABLED -> "Start Server"
                        ServerState.DISABLING -> "Stopping Server"
                    }
                )
            }
        }
    }
}

@Composable
private fun ScoutsList(
    modifier: Modifier = Modifier,
) {
    var checkedStates by remember { mutableStateOf(emptyList<Boolean>()) }

    Card(
        modifier = modifier,
        header = {
            CardHeader(
                title = { Text("Connected Scouts") },
                //description = { Text("Connected Scouts") },
            )
        },
        actions = { modifier ->
            TextButton(modifier = modifier, onClick = { /*TODO*/ }) {
                Text(if (checkedStates.contains(true)) {
                    "REMOVE SCOUT${if (checkedStates.toList().count { it } > 1) "S" else ""}"
                } else "")
            }
            TextButton(modifier = modifier, onClick = { /*TODO*/ }) {
                Text("ADD SCOUT")
            }
        },
    ) {
        DataTable(onSelectionChanged = { states ->
            checkedStates = states
        }) {
            header {
                item("Status")
                item("Device Name")
                item("Alliance")
                item("Match")
            }
            rows(6) { index ->
                item("Online")
                item("Scouter #$index")
                item("Red")
                item("22")
            }
        }
    }
}

@Preview
@Composable
private fun ServerHomeScreenPreviewLight() {
    FrcKrawlerTheme(darkTheme = false) {
        ServerHomeScreen(navController = rememberNavController())
    }
}

@Preview
@Composable
private fun ServerHomeScreenPreviewDark() {
    FrcKrawlerTheme(darkTheme = true) {
        ServerHomeScreen(navController = rememberNavController())
    }
}