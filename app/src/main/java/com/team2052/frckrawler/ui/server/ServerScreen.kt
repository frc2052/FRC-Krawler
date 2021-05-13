package com.team2052.frckrawler.ui.server

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.team2052.frckrawler.model.Event
import com.team2052.frckrawler.ui.NavScreen
import com.team2052.frckrawler.ui.components.*
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun ServerScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    event: Event
) = FRCKrawlerScaffold(
    modifier = modifier,
    currentScreen = NavScreen.ServerScreen,
) {
    //val event: Event = navController.previousBackStackEntry?.arguments?.getSerializable(NavScreen.ServerScreen.event) as Event
    val viewModel: ServerViewModel = hiltNavGraphViewModel()

    FRCKrawlerCard(
        modifier = Modifier,
        header = {
            FRCKrawlerCardHeader(
                title = { Text("Server Properties") },
                //description = { Text("Control server and configuration") },
            )
        },
        actions = mapOf(
            Pair("cancel", { }),
            Pair("continue", { }),
        ),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 24.dp),
                onClick = { /*TODO*/ },
            ) {
                Text(text = "Server Config")
            }
            Button(
                modifier = Modifier.weight(0.5f),
                onClick = { viewModel.toggleServer() },
            ) {
                Text(if (viewModel.serverState.value) "Stop Server" else "Start Server")
            }
        }
    }

    var expandableCardState by remember { mutableStateOf(false) }
    FRCKrawlerExpandableCard(
        modifier = Modifier.padding(top = 24.dp),
        header = {
            FRCKrawlerCardHeader(
                title = { Text("Server Properties") },
                //description = { Text("Control server and configuration") },
            )
        },
        actions = mapOf(
            Pair("cancel", { }),
            Pair("continue", { }),
        ),
        expanded = expandableCardState,
        onExpanded = { state -> expandableCardState = state },
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 24.dp),
                onClick = { /*TODO*/ },
            ) {
                Text(text = "Server Config")
            }
            Button(
                modifier = Modifier.weight(0.5f),
                onClick = { viewModel.toggleServer() },
            ) {
                Text(if (viewModel.serverState.value) "Stop Server" else "Start Server")
            }
        }
    }
}

@Preview
@Composable
private fun ServerScreenPreviewLight() {
    FrcKrawlerTheme(darkTheme = false) {
        ServerScreen(event = Event.fake())
    }
}

@Preview
@Composable
private fun ServerScreenPreviewDark() {
    FrcKrawlerTheme(darkTheme = true) {
        ServerScreen(event = Event.fake())
    }
}