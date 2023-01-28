package com.team2052.frckrawler.ui.server

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.model.DeviceType
import com.team2052.frckrawler.data.model.Event
import com.team2052.frckrawler.data.model.TEMP_EVENTS
import com.team2052.frckrawler.ui.RequestEnableBluetooth
import com.team2052.frckrawler.ui.components.*
import com.team2052.frckrawler.ui.components.fields.FRCKrawlerDropdown
import com.team2052.frckrawler.ui.navigation.Screen.Server
import com.team2052.frckrawler.ui.navigation.Screen.ServerHome
import com.team2052.frckrawler.ui.permissions.BluetoothPermissionRequestDialogs
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import com.team2052.frckrawler.ui.theme.spaceMedium

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
                ServerConfigCard(
                    modifier = modifier,
                    serverState = viewModel.serverState,
                    toggleServer = {
                        if (viewModel.serverState == ServerState.ENABLED) {
                            viewModel.stopServer()
                        } else {
                            viewModel.startServer()
                        }
                    },
                    availableMetricSets = listOf("2022 KnightKrawler Metrics"),
                    availableEvents = TEMP_EVENTS,
                    configuration = viewModel.serverConfiguration,
                    onConfigurationChanged = {
                        viewModel.serverConfiguration = it
                    }
                )

                ScoutsList(modifier = modifier)
            }
        }
    }
}


@Composable
private fun ServerConfigCard(
    modifier: Modifier = Modifier,
    availableEvents: List<Event>,
    availableMetricSets: List<String>,
    configuration: ServerConfiguration,
    onConfigurationChanged: (ServerConfiguration) -> Unit,
    serverState: ServerState,
    toggleServer: () -> Unit,
) {
    Card(
        modifier = modifier,
        header = {
            CardHeader(
                title = { Text("Server Controls") },
                description = { Text("Control server and configuration") },
            )
        },
    ) {
        // Event selection dropdown
        var eventValid by remember { mutableStateOf(true) }
        FRCKrawlerDropdown(
            modifier = Modifier.padding(bottom = spaceMedium),
            value = configuration.event,
            getLabel = { it?.name ?: "" },
            onValueChange = {
                onConfigurationChanged(
                    configuration.copy(event = it)
                )
                if (it != null) {
                    eventValid = true
                }
            },
            validity = eventValid,
            onFocusChange = { focused ->
                if (!focused) {
                    eventValid = (configuration.event != null)
                }
            },
            label = "Event",
            dropdownItems = availableEvents
        )

        // Event selection dropdown
        var metricsValid by remember { mutableStateOf(true) }
        FRCKrawlerDropdown(
            value = configuration.metricSetName,
            onValueChange = {
                onConfigurationChanged(
                    configuration.copy(metricSetName = it)
                )
            },
            getLabel = { it ?: "" },
            validity = metricsValid,
            onFocusChange = { focused ->
                if (!focused) {
                    metricsValid = (configuration.metricSetName != null)
                }
            },
            label = "Metrics",
            dropdownItems = availableMetricSets
        )

        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomEnd
        ) {
            // TODO disable if event and metrics are not selected
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
        val context = LocalContext.current
        Button(
            onClick = {

                val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                    putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 60)
                }
                context.startActivity(discoverableIntent)
            }
        ) {
            Text("Connect new scouts")
        }

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
private fun ServerPropsPreview() {
    FrcKrawlerTheme(darkTheme = false) {
        ServerConfigCard(
            serverState = ServerState.ENABLED,
            toggleServer = {},
            availableEvents = TEMP_EVENTS,
            availableMetricSets = listOf("2022 KnightKrawler Metrics"),
            configuration = ServerConfiguration(null, null),
            onConfigurationChanged = {}
        )
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