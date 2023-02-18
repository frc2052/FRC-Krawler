package com.team2052.frckrawler.ui.server

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.data.model.Event
import com.team2052.frckrawler.data.model.TEMP_EVENTS
import com.team2052.frckrawler.ui.components.Card
import com.team2052.frckrawler.ui.components.CardHeader
import com.team2052.frckrawler.ui.components.fields.FRCKrawlerDropdown
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import com.team2052.frckrawler.ui.theme.spaceMedium

@Composable
internal fun ServerConfigCard(
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
        EventDropdown(
            configuration = configuration,
            onConfigurationChanged = onConfigurationChanged,
            availableEvents = availableEvents
        )
        MetricsDropdown(
            configuration = configuration,
            onConfigurationChanged = onConfigurationChanged,
            availableMetricSets = availableMetricSets
        )
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomEnd
        ) {
            // TODO disable if event and metrics are not selected
            ServerToggleButton(
                modifier = modifier,
                serverState = serverState,
                toggleServer = toggleServer
            )
        }
    }
}

@Composable
private fun EventDropdown(
    configuration: ServerConfiguration,
    onConfigurationChanged: (ServerConfiguration) -> Unit,
    availableEvents: List<Event>
) {
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
}

@Composable
private fun MetricsDropdown(
    configuration: ServerConfiguration,
    onConfigurationChanged: (ServerConfiguration) -> Unit,
    availableMetricSets: List<String>
) {
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
}

@Composable
private fun ServerToggleButton(
    modifier: Modifier,
    serverState: ServerState,
    toggleServer: () -> Unit
) {
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

