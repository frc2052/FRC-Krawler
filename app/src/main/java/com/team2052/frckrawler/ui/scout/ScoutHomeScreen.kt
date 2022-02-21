package com.team2052.frckrawler.ui.scout

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.team2052.frckrawler.ui.components.*
import com.team2052.frckrawler.ui.navigation.Screen.*
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun ScoutHomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val viewModel: ScoutViewModel = hiltViewModel()
    val scaffoldState = rememberScaffoldState()

    FRCKrawlerScaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        refreshing = false,
        onRefresh = { },
        appBar = {
            FRCKrawlerAppBar(
                navController = navController,
                scaffoldState = scaffoldState,
                title = {
                    Text("Scout")
                }
            )
        },
        tabBar = {
            FRCKrawlerTabBar(navigation = Scout, currentScreen = ScoutHome) { screen ->
                navController.navigate(screen.route) {
                    launchSingleTop = true
                }
            }
        },
        drawerContent = {
            FRCKrawlerDrawer()
        },
    ) { contentPadding ->
        ScoutHomeScreenContent(modifier.padding(contentPadding), navController)
    }
}

@Composable
private fun ScoutHomeScreenContent(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    Card(
        modifier = modifier,
        header = {
            CardHeader(
                title = { Text("Scout Properties") },
                description = { Text("Connect to a server and manage scout properties") },
            )
        },
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                modifier = Modifier.weight(0.5f),
                onClick = {

                },
            ) {
                Text("Find Server")
            }
        }
    }
//    Button(onClick = { }) {
//        Text("Find Server")
//    }
}

@Preview
@Composable
private fun ScoutScreenPreviewLight() {
    FrcKrawlerTheme(darkTheme = false) {
        ScoutHomeScreen(navController = rememberNavController())
    }
}

@Preview
@Composable
private fun ScoutScreenPreviewDark() {
    FrcKrawlerTheme(darkTheme = true) {
        ScoutHomeScreen(navController = rememberNavController())
    }
}