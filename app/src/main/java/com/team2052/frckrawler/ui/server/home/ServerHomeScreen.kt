package com.team2052.frckrawler.ui.server.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.NavScreen
import com.team2052.frckrawler.ui.components.*

@Composable
fun ServerHomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val scaffoldState = rememberScaffoldState()

    FRCKrawlerScaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        appBar = {
            FRCKrawlerAppBar(navController = navController, title = {
                Text(stringResource(R.string.server_screen_title))
            })
        },
        tabBar = {
            FRCKrawlerTabBar(parentNavScreen = NavScreen.Server) { screen ->
                navController.navigate(screen.route) {
                    launchSingleTop = true
                }
            }
        }
    ) { contentPadding ->
        ServerHomeScreenContent(
            modifier = Modifier.padding(contentPadding),
            scaffoldState = scaffoldState,
        )
    }
}

@Composable
fun ServerHomeScreenContent(
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
) {

    val snackbarController = FRCKrawlerSnackbarController(rememberCoroutineScope())

//    val viewModel: ServerViewModel = hiltViewModel()

    Text("Server Home Screen", modifier)

    FRCKrawlerCard(
        modifier = modifier,
        header = {
            FRCKrawlerCardHeader(
                title = { Text("Server Properties") },
                //description = { Text("Control server and configuration") },
            )
        },
        actions = emptyMap(),
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
                onClick = {
//                    when (viewModel.serverState.value) {
//                        ServerState.OFF -> {
//                            snackbarController.scope.launch {
//                                snackbarController.showSnackbar(
//                                    scaffoldState = scaffoldState,
//                                    message = "starting server...",
//                                    actionLabel = "dismiss",
//                                )
//                            }
//                        }
//                        ServerState.ON -> {
//                            snackbarController.scope.launch {
//                                snackbarController.showSnackbar(
//                                    scaffoldState = scaffoldState,
//                                    message = "stopping server...",
//                                    actionLabel = "dismiss",
//                                )
//                            }
//                        }
//                        else -> { }
//                    }
//                    viewModel.toggleServer()
                },
                modifier = Modifier.weight(0.5f),
            ) {
//                when (viewModel.serverState.value) {
//                    ServerState.ON -> {
//                        Text("Stop Server")
//                    }
//                    ServerState.TURNING_ON -> {
//                        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
//                            Text("Starting Server")
//                            Spacer(modifier = Modifier.width(16.dp))
//                            CircularProgressIndicator(
//                                modifier = Modifier.size(16.dp),
//                                color = Color.White.copy(alpha = LocalContentAlpha.current),
//                                strokeWidth = 2.dp,
//                            )
//                        }
//                    }
//                    ServerState.OFF -> {
//                        Text("Start Server")
//                    }
//                    ServerState.TURNING_OFF -> {
//                        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
//                            Text("Stopping Server")
//                            Spacer(modifier = Modifier.width(16.dp))
//                            CircularProgressIndicator(
//                                modifier = Modifier.size(16.dp),
//                                color = Color.White.copy(alpha = LocalContentAlpha.current),
//                                strokeWidth = 2.dp,
//                            )
//                        }
//                    }
//                }
            }
        }
    }
}