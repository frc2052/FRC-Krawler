package com.team2052.frckrawler.ui

import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.components.SnackbarManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun rememberFRCKrawlerAppState(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navController: NavHostController = rememberNavController(),
    snackbarManager: SnackbarManager = SnackbarManager,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) = remember(scaffoldState, navController, snackbarManager, coroutineScope) {
    FRCKrawlerAppState(scaffoldState, navController, snackbarManager, coroutineScope)
}

class FRCKrawlerAppState(
    val scaffoldState: ScaffoldState,
    val navController: NavHostController,
    private val snackbarManager: SnackbarManager,
    coroutineScope: CoroutineScope,
) {
    init {
        coroutineScope.launch {
            snackbarManager.snacks.collect { snacks ->
                if (snacks.isNotEmpty()) {
                    val currentSnack = snacks[0]

                    scaffoldState.snackbarHostState.showSnackbar(currentSnack.message)
                    snackbarManager.dismissSnackbar(currentSnack.id)
                }
            }
        }
    }

    var title by mutableStateOf(R.string.app_name)

    var topBarTabs: List<Section> = mutableListOf()

    fun navigateToTab(index: Int) {
        val route = topBarTabs[index].route
        if (route != navController.currentDestination?.route) {
            navController.navigate(route) {
                launchSingleTop = true
            }
        }
    }

    val shouldShowTabBar: Boolean
        get() = topBarTabs.isNotEmpty()
}