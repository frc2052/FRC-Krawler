package com.team2052.frckrawler.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun FRCKrawlerAppBar(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    navigation: @Composable () -> Unit = {
        DefaultNavigationButton(navController)
    },
    title: @Composable RowScope.() -> Unit,
    actions: @Composable RowScope.() -> Unit = { },
) = TopAppBar(
    modifier = modifier.zIndex(1f),
    backgroundColor = MaterialTheme.colors.primary,
    elevation = 0.dp,
    contentColor = MaterialTheme.colors.onSurface,
    navigationIcon = navigation,
    actions = {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colors.onPrimary) {
            actions()
        }
    },
    title = {
        ProvideTextStyle(MaterialTheme.typography.h6.copy(
            color = MaterialTheme.colors.onPrimary
        )) { Row { title() } }
    },
)

@Composable
private fun DefaultNavigationButton(
    navController: NavController
) {
    if (navController.previousBackStackEntry != null) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Backwards Navigation",
                tint = MaterialTheme.colors.onPrimary,
            )
        }
    }
}

@Preview
@Composable
private fun FRCKrawlerAppBarPreviewLight() {
    FrcKrawlerTheme(darkTheme = false) {
        FRCKrawlerAppBar(
            title = { Text("preview") },
        )
    }
}

@Preview
@Composable
private fun FRCKrawlerAppBarPreviewDark() {
    FrcKrawlerTheme(darkTheme = true) {
        FRCKrawlerAppBar(
            title = { Text("preview") },
        )
    }
}