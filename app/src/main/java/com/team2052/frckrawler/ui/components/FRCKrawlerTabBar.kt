package com.team2052.frckrawler.ui.components

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.ui.NavScreen
import java.util.*

@Composable
fun FRCKrawlerTabBar(
    modifier: Modifier = Modifier,
    parentNavScreen: NavScreen,
    selectedTabIndex: Int = 0,
    onTabSelected: (NavScreen) -> Unit = { },
) = ScrollableTabRow(
    selectedTabIndex = selectedTabIndex,
    modifier = modifier.height(48.dp),
    backgroundColor = MaterialTheme.colors.primary,
    indicator = { tabPositions ->
        TabRowDefaults.Indicator(
            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
            color = MaterialTheme.colors.secondary,
        )
    },
) {
    parentNavScreen::class.nestedClasses.mapIndexed { index, it ->
        val screen = it.objectInstance as NavScreen
        val selected = (selectedTabIndex == index)
        val enabled = screen.route.isNotEmpty()
        Tab(
            modifier = Modifier.fillMaxHeight(),
            selected = selected,
            onClick = { if (enabled) onTabSelected(screen) },
            enabled = enabled,
            selectedContentColor = MaterialTheme.colors.secondary,
            unselectedContentColor = MaterialTheme.colors.onPrimary,
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 48.dp),
                text = screen.title.toUpperCase(Locale.getDefault()),
            )
        }
    }
}