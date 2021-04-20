package com.team2052.frckrawler.modeSelect

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.team2052.frckrawler.base.LabeledDropdown
import com.team2052.frckrawler.base.Dropdown

class ComposeModeSelectFragment : Fragment() {}

@Composable
fun ModeSelector(padding: PaddingValues) {
    var expandedCard by remember { mutableStateOf(-1) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Welcome to FRC Krawler!", color = MaterialTheme.colors.onSurface, style = MaterialTheme.typography.h6)
            Text(text = "To get started, select from the options below", color = MaterialTheme.colors.onSurface, style = MaterialTheme.typography.h6)
        }
        ExpandableCardGroup {
            listOf<@Composable () -> Unit> {
                enumValues<DeviceModes>().forEach { deviceMode ->
                    ExpandableCard(
                        modifier = Modifier.fillMaxWidth(),
                        title = deviceMode.title,
                        description = deviceMode.description,
                        continueMessage = deviceMode.startMessage,
                        expanded = expandedCard == deviceMode.ordinal,
                        content = deviceMode.content,
                        onExpand = { state -> expandedCard = if(state) deviceMode.ordinal else -1 },
                        onContinue = {
                            // TODO: Link pages
                        }
                    )
                }
            }
        }
    }
}

private val modifier: Modifier = Modifier.padding(0.dp, 4.dp)
enum class DeviceModes (
    val title: String,
    val description: String,
    val startMessage: String,
    val content: @Composable ColumnScope.() -> Unit,
) {
    SCOUT(
        title = "Remote Scout",
        description = "I want to connect to a server and scout",
        startMessage = "start scouting",
        content = {
            LabeledDropdown(modifier = modifier, label = "Server") {
                Dropdown(dropdownList = listOf("Nexus 7", "Sam's iPad", "Bluetooth device 3", "Bluetooth device 4"))
            }
        }
    ),
    SERVER(
        title = "Server",
        description = "This device will be a server for remote scouts",
        startMessage = "start server",
        content = {
            LabeledDropdown(modifier = modifier, label = "Game") {
                Dropdown(dropdownList = listOf("Infinite Recharge", "Destination Deep Space Destination Deep Space", "First Power Up"))
            }
            LabeledDropdown(modifier = modifier, label = "Event") {
                Dropdown(dropdownList = listOf("Northern Lights"))
            }
        }
    ),
    REMOTE_SCOUT(
        title = "Solo Scout",
        description = "I want to scout without connecting to other devices",
        startMessage = "start scouting",
        content = {
            LabeledDropdown(modifier = modifier, label = "Game") {
                Dropdown(dropdownList = listOf("Infinite Recharge", "Destination Deep Space Destination Deep Space", "First Power Up"))
            }
            LabeledDropdown(modifier = modifier, label = "Event") {
                Dropdown(dropdownList = listOf("Northern Lights"))
            }
        }
    )
}