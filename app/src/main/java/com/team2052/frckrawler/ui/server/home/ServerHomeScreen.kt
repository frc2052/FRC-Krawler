package com.team2052.frckrawler.ui.server.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.model.DeviceType
import com.team2052.frckrawler.data.model.TEMP_EVENTS
import com.team2052.frckrawler.ui.RequestEnableBluetooth
import com.team2052.frckrawler.ui.components.FRCKrawlerAppBar
import com.team2052.frckrawler.ui.components.FRCKrawlerDrawer
import com.team2052.frckrawler.ui.components.FRCKrawlerScaffold
import com.team2052.frckrawler.ui.components.FRCKrawlerTabBar
import com.team2052.frckrawler.ui.navigation.Screen.Server
import com.team2052.frckrawler.ui.navigation.Screen.ServerHome
import com.team2052.frckrawler.ui.permissions.BluetoothPermissionRequestDialogs
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun ServerHomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val viewModel: ServerHomeViewModel = hiltViewModel()
    val scaffoldState = rememberScaffoldState()

    Box {
        
        if (viewModel.showPermissionRequests) {
            BluetoothPermissionRequestDialogs(
                deviceType = DeviceType.Server,
                onAllPermissionsGranted = { viewModel.startServer() },
                onCanceled = { viewModel.showPermissionRequests = false }
            )
        }

        if (viewModel.requestEnableBluetooth) {
            RequestEnableBluetooth(
                deviceType = DeviceType.Server,
                onEnabled = { viewModel.startServer() },
                onCanceled = { viewModel.requestEnableBluetooth = false }
            )
        }
        
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
                FRCKrawlerTabBar(navigation = Server, currentScreen = ServerHome) { screen ->
                    navController.navigate(screen.route) {
                        popUpTo(ServerHome.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            },
            drawerContent = {
                FRCKrawlerDrawer()
            },
        ) { contentPadding ->
            Column(
                modifier = Modifier.padding(contentPadding)
            ) {
                ServerConfigCard(
                    modifier = modifier,
                    serverState = viewModel.serverState,
                    toggleServer = {
                        if (viewModel.serverState == ServerState.ENABLED) {
                            viewModel.stopServer()
                        } else {
                            viewModel.startServer()
                        }
                    },
                    availableMetricSets = listOf("2022 KnightKrawler Metrics"),
                    availableEvents = TEMP_EVENTS,
                    configuration = viewModel.serverConfiguration,
                    onConfigurationChanged = {
                        viewModel.serverConfiguration = it
                    }
                )

                ConnectedScoutsList(modifier = modifier)
            }
        }
    }
}

@Preview
@Composable
private fun ServerHomeScreenPreviewLight() {
    FrcKrawlerTheme(darkTheme = false) {
        ServerHomeScreen(navController = rememberNavController())
    }
}

@Preview
@Composable
private fun ServerHomeScreenPreviewDark() {
    FrcKrawlerTheme(darkTheme = true) {
        ServerHomeScreen(navController = rememberNavController())
    }
}