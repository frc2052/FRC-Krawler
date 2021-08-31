package com.team2052.frckrawler.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import kotlinx.coroutines.launch

@Composable
fun FRCKrawlerAppBar(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navigation: @Composable () -> Unit = {
        val scope = rememberCoroutineScope()
        if (navController.previousBackStackEntry == null) {
            IconButton(onClick = {
                if (!scaffoldState.drawerState.isAnimationRunning) {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu",
                    tint = MaterialTheme.colors.onPrimary,
                )
            }
        } else {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowLeft,
                    contentDescription = "Backwards Navigation",
                    tint = MaterialTheme.colors.onPrimary,
                )
            }
        }
    },
    title: @Composable RowScope.() -> Unit,
    actions: @Composable RowScope.() -> Unit = { },
) = TopAppBar(
    modifier = modifier.zIndex(1f),
    backgroundColor = MaterialTheme.colors.primary,
    elevation = 4.dp,
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

@Preview
@Composable
private fun FRCKrawlerAppBarPreviewLight() {
    FrcKrawlerTheme(darkTheme = false) {
        FRCKrawlerAppBar(title = { Text("Preview") })
    }
}

@Preview
@Composable
private fun FRCKrawlerAppBarPreviewDark() {
    FrcKrawlerTheme(darkTheme = true) {
        FRCKrawlerAppBar(title = { Text("Preview") })
    }
}