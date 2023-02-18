package com.team2052.frckrawler.ui.server

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.team2052.frckrawler.ui.components.Card
import com.team2052.frckrawler.ui.components.CardHeader
import com.team2052.frckrawler.ui.components.DataTable

@Composable
internal fun ConnectedScoutsList(
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