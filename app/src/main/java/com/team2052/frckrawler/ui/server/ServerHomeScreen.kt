package com.team2052.frckrawler.ui.server

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
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
import com.google.accompanist.permissions.rememberPermissionState
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.navigation.Screen.*
import com.team2052.frckrawler.ui.components.*
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
    val scope = rememberCoroutineScope()

    val viewModel: ServerViewModel = hiltViewModel()
    val scaffoldState = rememberScaffoldState()

    // Opens the settings when the locationSettingsInformation alert is confirmed
    val settingsLauncher = ComposableLauncher<Unit> {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri = Uri.fromParts("package", LocalContext.current.packageName, null)
        intent.data = uri

        LocalContext.current.startActivity(intent)
    }

    val alertController = rememberAlertController()
    AlertManager(alertController = alertController) {
        alert("bluetoothRequest") {
            Alert(
                onStateChange = { hide() },
                confirm = { Text("Enable") },
                dismiss = { Text("Cancel") },
                title = { Text("Enable Bluetooth") },
            ) {
                Text("Enabling bluetooth will allow the server " +
                        "to connect and receive data from scouts.")
            }
        }
        alert("locationRequest") {
            Alert(
                onStateChange = { state ->
                    when (state) {
                        AlertState.CONFIRMED -> {
                            hide()
                        }
                        AlertState.DISMISSED -> {
                            alertController.show("locationSettingsInformation")
                            hide()
                        }
                    }
                },
                confirm = { Text("Confirm") },
                dismiss = { Text("Decline") },
                title = { Text("Enable Coarse Location") },
            ) {
                Text("Android ${Build.VERSION.CODENAME} (${Build.VERSION.SDK_INT}) " +
                        "requires coarse location permissions to access bluetooth features.")
            }
        }
        alert("locationSettingsInformation") {
            Alert(
                onStateChange = { alertState ->
                    if (alertState == AlertState.CONFIRMED) {
                        scope.launch { settingsLauncher.launch() }
                    }
                    hide()
                },
                confirm = { Text("Open Settings") },
                title = { Text("Re-enable Coarse Location") },
            ) {
                Text("Enabling coarse location is an essential step " +
                        "in running the FRCKrawler Bluetooth Server. " +
                        "Please re-enable this permission in the app settings.")
            }
        }
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
        ServerHomeScreenContent(
            modifier = Modifier.padding(contentPadding),
            viewModel = viewModel,
            navController = navController,
            scope = scope,
            scaffoldState = scaffoldState,
            snackbarController = FRCKrawlerSnackbarController(scope),
            alertController = alertController,
        )
    }
}

@Composable
private fun ServerHomeScreenContent(
    modifier: Modifier = Modifier,
    viewModel: ServerViewModel,
    navController: NavController,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    snackbarController: FRCKrawlerSnackbarController,
    alertController: AlertController,
) {
    ServerProperties(
        modifier = modifier,
        viewModel = viewModel,
        navController = navController,
        scope = scope,
        scaffoldState = scaffoldState,
        snackbarController = snackbarController,
        alertController = alertController,
    )

    ScoutsList(modifier = modifier)
}

@Composable
private fun ServerProperties(
    modifier: Modifier = Modifier,
    viewModel: ServerViewModel,
    navController: NavController,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    snackbarController: FRCKrawlerSnackbarController,
    alertController: AlertController,
) {
    val serverLauncher = ComposableLauncher<Boolean> {
        var serverSuccessful = false
        RequestLocationPermission(alertController) {
            serverSuccessful = it
            if (!serverSuccessful) complete(false)
        }
        alertController.show("bluetoothRequest")
    }

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
                modifier = Modifier.weight(0.5f),
                enabled = viewModel.serverState.value == ServerState.ENABLED ||
                        viewModel.serverState.value == ServerState.DISABLED,
                onClick = {
                    if (viewModel.serverState.value == ServerState.DISABLED) {
                        // Begin enabling the server.
                        snackbarController.showSnackbar(
                            scaffoldState = scaffoldState,
                            message = "starting server...",
                            actionLabel = "dismiss",
                        )
                        scope.launch {
                            serverLauncher.launch(onCompletion = { success ->
                                if (success != null) {
                                    Timber.d("SUCCESSFULLNESS - $success")
                                }
                            })
                        }

//                        bluetoothEnableLauncher.launch {
//                            viewModel.startServer()
//                        }
                    } else if (viewModel.serverState.value == ServerState.ENABLED) {
                        // Begin disabling the server.
                        snackbarController.showSnackbar(
                            scaffoldState = scaffoldState,
                            message = "stopping server...",
                            actionLabel = "dismiss",
                        )
                        viewModel.stopServer()
                    }
                },
            ) {
                Text(
                    when (viewModel.serverState.value) {
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

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun RequestLocationPermission(
    alertController: AlertController,
    onCompletion: (Boolean) -> Unit,
) {
    val coarseLocationState = rememberPermissionState(
        permission = android.Manifest.permission.ACCESS_COARSE_LOCATION
    )

    PermissionRequired(
        permissionState = coarseLocationState,
        permissionNotGrantedContent = { alertController.show("locationRequest") },
        permissionNotAvailableContent = {
            alertController.show("locationSettingsInformation")
            onCompletion(false)
        },
    ) { onCompletion(true) }
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