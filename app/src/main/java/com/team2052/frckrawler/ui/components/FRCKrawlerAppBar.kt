package com.team2052.frckrawler.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun FRCKrawlerAppbar(
    modifier: Modifier = Modifier,
    backwardsNavigation: Boolean,
    onNavigationPressed: () -> Unit = { },
    title: @Composable RowScope.() -> Unit,
    actions: @Composable RowScope.() -> Unit = { },
) {
    TopAppBar(
        modifier = modifier,
        backgroundColor = MaterialTheme.colors.primary,
        elevation = 0.dp,
        contentColor = MaterialTheme.colors.onSurface,
        navigationIcon = {
            Icon(
                modifier = Modifier.fillMaxSize().padding(12.dp).also {
                    if (backwardsNavigation) it.clickable { onNavigationPressed() } else it
                },
                painter = painterResource(if (backwardsNavigation) R.drawable.ic_baseline_keyboard_arrow_left_24 else R.drawable.ic_bg_logo),
                contentDescription = "",
                tint = Color(0xFFFFFFFF)
            )
        },
        actions = actions,
        title = {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                content = title
            )
        }
    )
}

@Preview
@Composable
private fun FRCKrawlerAppBarPreviewLight() {
    FrcKrawlerTheme(darkTheme = false) {
        FRCKrawlerAppbar(backwardsNavigation = false, title = { Text("Preview") })
    }
}

@Preview
@Composable
private fun FRCKrawlerAppBarPreviewDark() {
    FrcKrawlerTheme(darkTheme = true) {
        FRCKrawlerAppbar(backwardsNavigation = false, title = { Text("Preview") })
    }
}