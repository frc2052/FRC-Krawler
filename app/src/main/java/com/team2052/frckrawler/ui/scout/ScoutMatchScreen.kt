package com.team2052.frckrawler.ui.scout

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Sync
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.team2052.frckrawler.data.model.DeviceType
import com.team2052.frckrawler.ui.bluetooth.RequestEnableBluetooth
import com.team2052.frckrawler.ui.components.*
import com.team2052.frckrawler.ui.permissions.BluetoothPermissionRequestDialogs
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun ScoutMatchScreen(
    modifier: Modifier = Modifier,
) {
    val viewModel: ScoutViewModel = hiltViewModel()

    // Don't love this, but it is what we need
    val context = LocalContext.current as ComponentActivity

    Box {
        if (viewModel.showPermissionRequests) {
            BluetoothPermissionRequestDialogs(
                deviceType = DeviceType.CLIENT,
                onAllPermissionsGranted = { viewModel.connectToServer(context) },
                onCanceled = { viewModel.showPermissionRequests = false }
            )
        }

        if (viewModel.requestEnableBluetooth) {
            RequestEnableBluetooth(
                deviceType = DeviceType.CLIENT,
                onEnabled = { viewModel.connectToServer(context) },
                onCanceled = { viewModel.requestEnableBluetooth = false }
            )
        }

        if (viewModel.serverConnectionState == ServerConnectionState.Connecting) {
            Dialog(onDismissRequest = { viewModel.cancelConnection() }) {
                FRCKrawlerCard(
                    header = {
                        FRCKrawlerCardHeader(
                            title = {
                                Text("Connecting to server...")
                            },
                        ) {
                            FRCKrawlerOutlinedButton(onClick = { viewModel.cancelConnection() }) {
                                Text("CANCEL")
                            }
                        }
                    },
                    showProgressIndicator = true,
                )
            }
        }

        Column {
            FRCKrawlerCard(
                modifier = modifier,
                header = {
                    FRCKrawlerCardHeader(
                        title = { Text("Server") },
                        description = if (viewModel.server != null) {
                            {
                                if (ActivityCompat.checkSelfPermission(
                                        LocalContext.current,
                                        Manifest.permission.BLUETOOTH_CONNECT
                                    ) == PackageManager.PERMISSION_GRANTED) {
                                    viewModel.server?.let { Text(it.name) }
                                }
                            }
                        } else null
                    ) {
                        if (viewModel.server != null) {
                            FRCKrawlerOutlinedButton(onClick = { viewModel.disconnectFromServer() }) {
                                Text("DISCONNECT")
                            }
                            
                            Spacer(modifier = Modifier.width(32.dp))
                            
                            FRCKrawlerButton(
                                onClick = { },
                                enabled = true,
                                icon = Icons.Filled.Sync
                            ) {
                                Text("SYNC")
                            }
                        } else {
                            FRCKrawlerButton(
                                onClick = { viewModel.connectToServer(context) },
                                enabled = true,
                                icon = Icons.Filled.Link
                            ) {
                                Text("CONNECT")
                            }
                        }
                    }
                },
            )

            if (viewModel.server == null) {
                Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Text(text = "To begin scouting connect to a server to get a metric set.")
                    }
                }
            }

            CompositionLocalProvider(LocalContentAlpha provides if (viewModel.serverConnected()) ContentAlpha.disabled else ContentAlpha.high) {
                FRCKrawlerCard(
                    modifier = modifier,
                    header = {
                        FRCKrawlerCardHeader(
                            title = { Text("Match Scout") },
                            description = { Text("Connect with server to scout") }
                        ) {
                            FRCKrawlerButton(
                                onClick = {  },
                                enabled = viewModel.serverConnected(),
                                icon = Icons.Filled.Add
                            ) {
                                Text("ADD")
                            }
                        }
                    },
                ) {
                    if (viewModel.serverConnected()) {
                        FRCKrawlerList {
                            item(title = { Text("Match 1") }, description = { Text("Synced") })
                            item(title = { Text("Match 2") }, description = { Text("Synced") })
                            item(title = { Text("Match 3") }, description = { Text("Local") })
                        }
                    }
                }
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
        CircularProgressIndicator(modifier = Modifier.size(28.dp))
        Text(modifier = Modifier.padding(start = 16.dp), text = "Connecting...")
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
            else -> { } // No error, so no text needed
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomEnd
        ) {
            FRCKrawlerButton(
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
        ScoutMatchScreen()
    }
}

@Preview
@Composable
private fun ScoutScreenPreviewDark() {
    FrcKrawlerTheme(darkTheme = true) {
        ScoutMatchScreen()
    }
}