package com.team2052.frckrawler.ui.server

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.nav.NavScreen
import com.team2052.frckrawler.ui.components.*
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
        refreshing = viewModel.isRefreshing,
        onRefresh = { viewModel.refresh() },
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
            FRCKrawlerTabBar(navController = navController, currentScreen = NavScreen.SERVER_HOME) { screen ->
                navController.navigate(screen.route) {
                    launchSingleTop = true
                }
            }
        },
        drawerContent = {
            FRCKrawlerDrawer()
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

@Composable
private fun ServerHomeScreenContent(
    modifier: Modifier = Modifier,
    viewModel: ServerViewModel,
    navController: NavController,
    scaffoldState: ScaffoldState,
    snackbarController: FRCKrawlerSnackbarController,
) {
    var alert by remember { mutableStateOf(false) }
    var alertWidth by remember { mutableStateOf(0) }
    if (alert) {
        AlertDialog(
            modifier = Modifier.onGloballyPositioned { alertWidth = it.size.width },
            onDismissRequest = { /*TODO*/ },
            title = { Text("Searching for devices") },
            text = {
                Column {
                    TextButton(modifier = Modifier.padding(bottom = 12.dp), onClick = { /*TODO*/ }) {
                        Text(text = "Sam's 2nd iPad")}
                    TextButton(modifier = Modifier.padding(bottom = 12.dp), onClick = { /*TODO*/ }) {
                        Text(text = "xs01412njfs")}
                    TextButton(modifier = Modifier, onClick = { /*TODO*/ }) {
                        Text(text = "Matt's phone")}
                }
            },
            buttons = {
                LinearProgressIndicator()
            }
        )
    }

    FRCKrawlerCard(
        modifier = modifier,
        header = {
            FRCKrawlerCardHeader(
                title = { Text("Server Properties") },
                description = { Text("Control server and configuration") },
            )
        },
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    navController.navigate(NavScreen.MODE_SELECT.route)
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
        Column {
            viewModel.scouts.forEach {
                Text(it.device.name)
            }
        }
    }

    val checkedStates = remember { mutableStateListOf(false, false, false, false, false) }

    FRCKrawlerCard(
        modifier = modifier,
        header = {
            FRCKrawlerCardHeader(title = { Text("Scouts") }, description = { Text("Connected Scouts") })
        },
        actions = mapOf(
            (if (checkedStates.contains(true))
                "remove scout${if (checkedStates.toList().count { it } > 1) "s" else ""}"
            else "") to {  },
            "add scouts" to {  },
        ),
    ) {
        FRCKrawlerDataTable(
            dataTableSource = TableSource(
                TableRow(
                    { Text("Status") },
                    { Text("Device Name") },
                    { Text("Alliance") },
                    { Text("Match") },
                    checked = checkedStates[0]
                ),
                TableRow(
                    { Text("Online") },
                    { Text("Scouter #1") },
                    { Text("Red") },
                    { Text("22") },
                    checked = checkedStates[1]
                ),
                TableRow(
                    { Text("Offline") },
                    { Text("Scouter #2") },
                    { Text("Red") },
                    { Text("18") },
                    checked = checkedStates[2]
                ),
                TableRow(
                    { Text("Online") },
                    { Text("Scouter #3") },
                    { Text("Blue") },
                    { Text("22") },
                    checked = checkedStates[3]
                ),
                TableRow(
                    { Text("Online") },
                    { Text("Scouter #4") },
                    { Text("Blue") },
                    { Text("22") },
                    checked = checkedStates[4]
                ),
            ),
            onCheckedChange = { index, checked ->
                checkedStates[index] = checked
            },
        )
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