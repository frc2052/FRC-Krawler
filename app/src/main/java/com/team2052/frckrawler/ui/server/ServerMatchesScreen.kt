package com.team2052.frckrawler.ui.server

import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.components.FRCKrawlerAppBar
import com.team2052.frckrawler.ui.components.FRCKrawlerScaffold
import com.team2052.frckrawler.ui.components.FRCKrawlerTabBar
import com.team2052.frckrawler.ui.navigation.Screen

@Composable
fun ServerMatchesScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val scaffoldState = rememberScaffoldState()
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
            FRCKrawlerTabBar(navigation = Screen.Server, currentScreen = Screen.ServerMatches) { screen ->
                navController.navigate(screen.route) {
                    popUpTo(Screen.ServerMatches.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
        },
        drawerContent = {
            //FRCKrawlerDrawer()
        },
    ) { contentPadding ->

    }
}