package com.team2052.frckrawler.ui.server

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.team2052.frckrawler.data.model.DeviceType
import com.team2052.frckrawler.ui.bluetooth.RequestEnableBluetooth
import com.team2052.frckrawler.ui.components.*
import com.team2052.frckrawler.ui.permissions.BluetoothPermissionRequestDialogs
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun ServerHomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToServerLogs: () -> Unit = { },
) {
    val viewModel: ServerViewModel = hiltViewModel()

    // Don't love this, but it is what we need
    val context = LocalContext.current as ComponentActivity

    Box {
        if (viewModel.showPermissionRequests) {
            BluetoothPermissionRequestDialogs(
                deviceType = DeviceType.CLIENT,
                onAllPermissionsGranted = { viewModel.startServer() },
                onCanceled = { viewModel.showPermissionRequests = false }
            )
        }

        if (viewModel.requestEnableBluetooth) {
            RequestEnableBluetooth(
                deviceType = DeviceType.CLIENT,
                onEnabled = { viewModel.startServer() },
                onCanceled = { viewModel.requestEnableBluetooth = false }
            )
        }

        Column {
            ServerProperties(modifier, viewModel, onNavigateToServerLogs)
            ScoutsList(modifier)
        }
    }
}

@Composable
private fun ServerProperties(
    modifier: Modifier = Modifier,
    viewModel: ServerViewModel,
    onNavigateToServerLogs: () -> Unit,
) {
    FRCKrawlerCard(
        modifier = modifier,
        header = {
            FRCKrawlerCardHeader(
                title = { Text("Server Controls") },
                description = { Text("Server control and configurations") }
            ) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(modifier = Modifier.padding(end = 8.dp), text = "Server Running:")
                }
                FRCKrawlerSwitch(
                    checked = viewModel.serverState == ServerState.Running,
                    onCheckedChange = { checked ->
                        if (checked) {
                            viewModel.startServer()
                        } else {
                            viewModel.stopServer()
                        }
                    },
                )
            }
        },
    ) {
        OutlinedButton(onClick = onNavigateToServerLogs) {
            Text("VIEW LOGS")
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
                description = { Text("Run the server to start connecting scouts") },
            ) {
                val context = LocalContext.current
                FRCKrawlerButton(
                    onClick = {
                        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 60)
                        }
                        context.startActivity(discoverableIntent)
                    },
                    icon = Icons.Filled.Link,
                ) {
                    Text("CONNECT")
                }
            }
        },
    ) {
        FRCKrawlerList() {
            item({ Text("") }, { Text("") })
        }
    }
}

@Preview
@Composable
private fun ServerHomeScreenPreviewLight() {
    FrcKrawlerTheme(darkTheme = false) {
        ServerHomeScreen()
    }
}

@Preview
@Composable
private fun ServerHomeScreenPreviewDark() {
    FrcKrawlerTheme(darkTheme = true) {
        ServerHomeScreen()
    }
}