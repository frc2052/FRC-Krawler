package com.team2052.frckrawler.ui.server.home

import android.bluetooth.BluetoothDevice
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*

@Composable
fun ServerConnectionRequestPopup(
    device: BluetoothDevice,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Join Request") },
        text = {
            Text("A scout has request to join this server: ${device.name}")
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Accept")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Decline")
            }
        }
    )
}