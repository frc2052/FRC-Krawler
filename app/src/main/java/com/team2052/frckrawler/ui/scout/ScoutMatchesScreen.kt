package com.team2052.frckrawler.ui.scout

import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.team2052.frckrawler.ui.components.*
import com.team2052.frckrawler.ui.navigation.Screen.*
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun ScoutMatchesScreen(
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
            FRCKrawlerTabBar(navigation = Scout, currentScreen = ScoutMatches) { screen ->
                navController.navigate(screen.route) {
                    launchSingleTop = true
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "new match")
            }
        },
        drawerContent = {
            FRCKrawlerDrawer()
        },
    ) { contentPadding ->
        ScoutMatchesScreenContent(modifier.padding(contentPadding), navController)
    }
}

@Composable
private fun ScoutMatchesScreenContent(
    modifier: Modifier = Modifier,
    navController: NavController,
) {

}

@Preview
@Composable
private fun ScoutScreenPreviewLight() {
    FrcKrawlerTheme(darkTheme = false) {
        ScoutMatchesScreen(navController = rememberNavController())
    }
}

@Preview
@Composable
private fun ScoutScreenPreviewDark() {
    FrcKrawlerTheme(darkTheme = true) {
        ScoutMatchesScreen(navController = rememberNavController())
    }
}