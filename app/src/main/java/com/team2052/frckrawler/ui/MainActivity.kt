package com.team2052.frckrawler.ui

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.DrawerValue
import androidx.compose.material.SnackbarHost
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.DataStoreManager
import com.team2052.frckrawler.ui.components.*
import com.team2052.frckrawler.ui.initial.InitialScreen
import com.team2052.frckrawler.ui.scout.ScoutSections
import com.team2052.frckrawler.ui.scout.scoutGraph
import com.team2052.frckrawler.ui.server.ServerSections
import com.team2052.frckrawler.ui.server.serverGraph
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MainSections {
    object Initial : Section("initial", R.string.app_name)
}

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject lateinit var dataStoreManager: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // dataStoreManager.getStartingScreen().collectAsState(initial = null)
            val startingRoute = remember { mutableStateOf(MainSections.Initial.route) }

            // Wait for starting route to load content
            val appState = rememberFRCKrawlerAppState()

            // Reset the theme after the splash screen finishes
            setTheme(R.style.Theme_FRCKrawler)

            val isDarkTheme = remember { mutableStateOf(true) }

            val scope = rememberCoroutineScope()
            var drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

            FrcKrawlerTheme(darkTheme = isDarkTheme.value) {
                FRCKrawlerScaffold(
                    scaffoldState = appState.scaffoldState,
                    appBar = {
                        FRCKrawlerAppBar(
                            navigationIcon = if (drawerState.isOpen) {
                                Icons.Filled.Close
                            } else if (appState.navController.previousBackStackEntry != null) {
                                Icons.Filled.ChevronLeft
                            } else {
                                Icons.Filled.Menu
                            },
                            onNavigationClicked = {
                                scope.launch {
                                    if (drawerState.isClosed) {
                                        drawerState.open()
                                    } else {
                                        drawerState.close()
                                    }
                                }
                            }
                        ) {
                            Text(text = stringResource(appState.title))
                        }
                    },
                    tabBar = {
                        var currentTabIndex by remember { mutableStateOf(0) }

                        if (appState.shouldShowTabBar) {
                            FRCKrawlerTabBar(
                                tabs = appState.topBarTabs.map { it.title },
                                selectedTabIndex = currentTabIndex,
                                onTabSelected = { index ->
                                    if (currentTabIndex != index) {
                                        currentTabIndex = index
                                        appState.navigateToTab(currentTabIndex)
                                    }
                                }
                            )
                        }
                    },
                    snackbarHost = { hostState ->
                        SnackbarHost(
                            hostState = hostState,
                            modifier = Modifier.systemBarsPadding(),
                        ) { snackbarData ->
                            FRCKrawlerSnackbar(
                                snackbarData = snackbarData,
                                onDismiss = {
                                    snackbarData.dismiss()
                                }
                            )
                        }
                    },
                    drawerState = drawerState,
                    drawerContent = {
                        FRCKrawlerDrawer()
                    },
                ) { innerPadding ->
                    val modifier = Modifier.padding(innerPadding)

                    NavHost(
                        navController = appState.navController,
                        startDestination = startingRoute.value,
                    ) {
                        composable(route = MainSections.Initial.route) {
                            appState.title = R.string.app_name
                            appState.topBarTabs = emptyList()

                            val coroutineScope = rememberCoroutineScope()

                            InitialScreen(
                                modifier = modifier,
                                onScoutSelected = {
                                    coroutineScope.launch {
                                        dataStoreManager.setStartingScreen(ScoutSections.Scout.route)
                                    }
                                    appState.navController.navigate(ScoutSections.Scout.route) {
                                        launchSingleTop = true
                                    }
                                },
                                onServerSelected = { teamNumber ->
                                    coroutineScope.launch {
                                        dataStoreManager.setStartingScreen(ServerSections.Server.route)
                                    }
                                    appState.navController.navigate(ServerSections.Server.route) {
                                        launchSingleTop = true
                                    }
                                },
                            )
                        }

                        scoutGraph(appState, modifier)

                        serverGraph(appState, modifier)
                    }
                }
            }
        }
    }

    // Provides focus clearing when tapping outside the keyboard to close the keyboard automatically
    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action != null && event.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (view is ViewGroup) {
                view.clearFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
        return super.dispatchTouchEvent(event)
    }
}
