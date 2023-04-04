package com.team2052.frckrawler.ui.server

import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.team2052.frckrawler.ui.components.FRCKrawlerScaffold

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

        },
        tabBar = {

        },
        drawerContent = {
            //FRCKrawlerDrawer()
        },
    ) { contentPadding ->

    }
}