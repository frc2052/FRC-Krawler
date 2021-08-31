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
import com.team2052.frckrawler.ui.nav.Screen.*
import com.team2052.frckrawler.ui.components.*
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun ServerHomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val viewModel: ServerViewModel = hiltViewModel()
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

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
            scaffoldState = scaffoldState,
            snackbarController = FRCKrawlerSnackbarController(scope),
        )
    }
}

@Composable
private fun ServerHomeScreenContent(
    modifier: Modifier = Modifier,
    viewModel: ServerViewModel,
    navController: NavController,
    scaffoldState: ScaffoldState,
    snackbarController: FRCKrawlerSnackbarController,
) {
    // Opens the settings when the locationSettingsInformation alert is confirmed
    var openSettings by remember { mutableStateOf(false) }
    if (openSettings) {
        openSettings = false

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
                onStateChange = { hide() },
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
                    openSettings = alertState == AlertState.CONFIRMED
                    hide()
                },
                confirm = { Text("Open Settings") },
                title = { Text("Re-enable Coarse Location") },
            ) {
                Text("Enabling coarse location is an essential step " +
                        "in running the FRCKrawler Bluetooth Server. " +
                        "Please re-enable this permission in the settings.")
            }
        }
    }

    var startServer by remember { mutableStateOf(false) }
    if (startServer) {
        startServer = false

        RequestLocationPermission(alertController) {

        }
    }

    ServerProperties(
        modifier = modifier,
        viewModel = viewModel,
        navController = navController,
        scaffoldState = scaffoldState,
        snackbarController = snackbarController,
    )

    ScoutsList(modifier = modifier)
}

@Composable
private fun ServerProperties(
    modifier: Modifier = Modifier,
    viewModel: ServerViewModel,
    navController: NavController,
    scaffoldState: ScaffoldState,
    snackbarController: FRCKrawlerSnackbarController,
) {
    FRCKrawlerCard(
        modifier = modifier,
        header = {
            FRCKrawlerCardHeader(
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

    FRCKrawlerCard(
        modifier = modifier,
        header = {
            FRCKrawlerCardHeader(
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