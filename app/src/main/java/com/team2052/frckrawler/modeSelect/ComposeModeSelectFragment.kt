package com.team2052.frckrawler.modeSelect

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.team2052.frckrawler.base.Dropdown2
import com.team2052.frckrawler.base.Dropdown3
import com.team2052.frckrawler.base.Dropdown4

class ComposeModeSelectFragment : Fragment() {}

@Composable
fun ModeSelector(padding: PaddingValues) {
    var expandedCard by remember { mutableStateOf(-1) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        Column(
//            modifier = Modifier.padding(8.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(text = "Welcome to FRC Krawler!", color = Color.White, style = MaterialTheme.typography.h6)
//            Text(text = "To get started, select from the options below", color = Color.White, style = MaterialTheme.typography.h6)
//        }
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
            Dropdown3(
                dropdownList = listOf("Infinite Recharge", "Destination Deep Space", "First Power Up")
//                dropdownList = listOf {
//                    Text(text = "Hello world! 1")
//                    Text(text = "Hello world! 2")
//                    Text(text = "Hello world! 3")
//                    Text(text = "Hello world! 4")
//                    Text(text = "Hello world! 5")
//                    Text(text = "Hello world! 6")
//                }
            )
            //LabeledDropdown(modifier = modifier, label = "Server", dropdownList = listOf("Nexus 7", "Sam's iPad", "Bluetooth device 3", "Bluetooth device 4"))
        }
    ),
    SERVER(
        title = "Server",
        description = "This device will be a server for remote scouts",
        startMessage = "start server",
        content = {
            Dropdown4(dropdownList = listOf("Infinite Recharge", "Destination Deep Space----------------------", "First Power Up"))
            //LabeledDropdown(modifier = modifier, label = "Game", dropdownList = listOf("Infinite Recharge", "Destination Deep Space", "First Power Up"))
            //LabeledDropdown(modifier = modifier, label = "Event", dropdownList = listOf("Northern Lights"))
        }
    ),
    REMOTE_SCOUT(
        title = "Solo Scout",
        description = "I want to scout without connecting to other devices",
        startMessage = "start scouting",
        content = {
            Dropdown2(
                selectedIndex = 0
            ) { modifier ->
                Text(modifier = modifier, text = "Hello, this is a test to see if long text will work!")
                Text(modifier = modifier, text = "Hello")
                Text(modifier = modifier, text = "Hello")
            }
        }
    )
}