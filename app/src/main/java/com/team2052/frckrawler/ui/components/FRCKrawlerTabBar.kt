package com.team2052.frckrawler.ui.components

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
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
) {
    parentNavScreen::class.nestedClasses.mapIndexed { index, it ->
        val screen = it.objectInstance as NavScreen
        val selected = (selectedTabIndex == index)
        val enabled = screen.route.isNotEmpty()
        Tab(
            modifier = Modifier
                .padding(horizontal = 48.dp)
                .fillMaxHeight(),
            selected = selected,
            onClick = { if (enabled) onTabSelected(screen) },
            enabled = enabled,
            selectedContentColor = MaterialTheme.colors.secondary,
            unselectedContentColor = MaterialTheme.colors.onPrimary,
        ) {
            Text(screen.title.toUpperCase(Locale.getDefault()))
//            ProvideTextStyle(
//                MaterialTheme.typography.button.copy(
//                    color = if (selected) {
//                        MaterialTheme.colors.secondary
//                    } else {
//                        MaterialTheme.colors.onPrimary
//                    }
//                )
//            ) {
//                Text(screen.title.toUpperCase(Locale.getDefault()))
//            }
        }
    }
}