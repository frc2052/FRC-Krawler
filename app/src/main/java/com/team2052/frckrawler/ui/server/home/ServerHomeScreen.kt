package com.team2052.frckrawler.ui.server.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.NavScreen
import com.team2052.frckrawler.ui.components.*
import com.team2052.frckrawler.ui.server.ServerViewModel
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import kotlinx.coroutines.launch

@Composable
fun ServerHomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
) {
    val viewModel: ServerViewModel = hiltViewModel()
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    FRCKrawlerScaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        appBar = {
            FRCKrawlerAppBar(
                navController = navController,
                scaffoldState = scaffoldState,
                title = {
                    Text(stringResource(R.string.server_screen_title))
                }
            )
        },
        tabBar = {
            FRCKrawlerTabBar(parentNavScreen = NavScreen.Server) { screen ->
                navController.navigate(screen.route) {
                    launchSingleTop = true
                }
            }
        },
    ) { contentPadding ->
        ServerHomeScreenContent(
            modifier = Modifier.padding(contentPadding),
            viewModel = viewModel,
            navController = navController,
            scaffoldState = scaffoldState,
            snackbarController = FRCKrawlerSnackbarController(scope),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ServerHomeScreenContent(
    modifier: Modifier = Modifier,
    viewModel: ServerViewModel,
    navController: NavController,
    scaffoldState: ScaffoldState,
    snackbarController: FRCKrawlerSnackbarController,
) {
    FRCKrawlerCard(
        modifier = modifier,
        header = {
            FRCKrawlerCardHeader(
                title = { Text("Server Properties") },
                description = { Text("Control server and configuration") },
            )
        },
        actions = emptyMap(),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    navController.navigate(NavScreen.ModeSelect.route)
                },
            ) {
                Text(text = "Server Config")
            }
            Spacer(modifier = Modifier.width(24.dp))
            Button(
                modifier = Modifier.weight(0.5f),
                onClick = {
                    if (viewModel.serverRunning()) {
                        viewModel.stopServer()
                        snackbarController.scope.launch {
                            snackbarController.showSnackbar(
                                scaffoldState = scaffoldState,
                                message = "stopping server...",
                                actionLabel = "dismiss",
                            )
                        }
                    } else {
                        viewModel.startServer()
                        snackbarController.scope.launch {
                            snackbarController.showSnackbar(
                                scaffoldState = scaffoldState,
                                message = "starting server...",
                                actionLabel = "dismiss",
                            )
                        }
                    }
                },
            ) {
                if (viewModel.serverRunning()) {
                    Text("Stop Server")
                } else {
                    Text("Start Server")
                }
            }
        }
    }
    Table(modifier = Modifier) {
        Text(text = "Scouts", style = MaterialTheme.typography.h6)
        TableHeader(items = 4) {
            Text("Col 1")
            Text("Col 2")
            Text("Col 3")
            Text("Col 4")
        }
        repeat(4) {
            TableRow(items = 4) {
                Text("R- Col 1")
                Text("R- Col 2")
                Text("R- Col 3")
                Text("R- Col 4")
            }
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