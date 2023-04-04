package com.team2052.frckrawler.ui.components

import android.app.ActionBar
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import timber.log.Timber

@Composable
fun FRCKrawlerTabBar(
    modifier: Modifier = Modifier,
    tabs: List<Int>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
) {
    Timber.d("WE GOT HERE!")
    Text(text = "HELLO WORLD!", color = Color.White)
    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier.height(48.dp),
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier.tabIndicatorOffset(
                    tabPositions[selectedTabIndex]
                ),
                color = MaterialTheme.colors.secondary,
            )
        }
    ) {
        tabs.forEachIndexed { index, tabTitle ->
            Tab(selected = index == selectedTabIndex, onClick = { onTabSelected(index) }) {
                Text(
                    modifier = Modifier.padding(horizontal = 48.dp),
                    text = stringResource(tabTitle),
                )
            }
        }
    }
}
