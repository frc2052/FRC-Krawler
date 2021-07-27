package com.team2052.frckrawler.ui.components

import android.util.Log
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.team2052.frckrawler.ui.nav.NavScreen
import java.util.*

@Composable
fun FRCKrawlerTabBar(
    modifier: Modifier = Modifier,
    navController: NavController,
    currentScreen: NavScreen,
    onTabSelected: (NavScreen) -> Unit = { },
) {
    val parentScreen = navController.findDestination(currentScreen.route)?.parent
    val childScreens: MutableList<NavScreen> = mutableListOf()
    parentScreen?.let {
        NavScreen.values().forEach {
            val navDest = navController.findDestination(it.route)
            // Filter null, non-child, and navigation child navigation destinations
            if (navDest != null && navDest.parent == parentScreen && navDest.navigatorName != "navigation") {
                childScreens.add(it)
            }
        }
    }

    var selectedTabIndex = 0
    for (i in 0..childScreens.size) {
        if (childScreens[i] == currentScreen) {
            selectedTabIndex = i
            break
        }
    }

    ScrollableTabRow(
        modifier = modifier.height(48.dp),
        selectedTabIndex = 0,
        backgroundColor = MaterialTheme.colors.primary,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = MaterialTheme.colors.secondary,
            )
        },
    ) {
        childScreens.forEachIndexed { index, screen ->
            Tab(
                modifier = Modifier.fillMaxHeight(),
                selected = index == selectedTabIndex,
                onClick = { onTabSelected(screen) },
                selectedContentColor = MaterialTheme.colors.secondary,
                unselectedContentColor = MaterialTheme.colors.onPrimary,
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 48.dp),
                    text = screen.title.uppercase(Locale.getDefault()),
                )
            }
        }
    }
}