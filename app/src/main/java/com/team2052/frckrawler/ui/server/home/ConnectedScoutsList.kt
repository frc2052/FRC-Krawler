package com.team2052.frckrawler.ui.server.home

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import com.team2052.frckrawler.data.model.RemoteScout
import com.team2052.frckrawler.ui.components.Card
import com.team2052.frckrawler.ui.components.CardHeader
import java.time.format.DateTimeFormatter

@Composable
internal fun ConnectedScoutsList(
    modifier: Modifier = Modifier,
    scouts: List<RemoteScout>
) {
    Card(
        modifier = modifier,
        header = {
            CardHeader(
                title = { Text("Connected Scouts") }
            )
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

        Column {
            scouts.forEach { scout ->
                SyncedScout(scout)
            }
        }
    }
}

@Composable
private fun SyncedScout(
    scout: RemoteScout,
    modifier: Modifier = Modifier,
) {
    val lastSyncText = scout.lastSync.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    Column(modifier = modifier) {
        Text(
            text = scout.name,
            style = MaterialTheme.typography.subtitle1
        )
        Text(
            text = "Last sync: $lastSyncText",
            style = MaterialTheme.typography.body2,
            fontStyle = FontStyle.Italic
        )
    }
}