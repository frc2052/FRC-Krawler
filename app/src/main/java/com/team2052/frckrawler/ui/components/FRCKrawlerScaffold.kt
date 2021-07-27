package com.team2052.frckrawler.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.theme.FRCKrawlerColor

val LocalCardElevation = compositionLocalOf { 2.dp }

@Composable
fun FRCKrawlerScaffold(
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    contentPadding: PaddingValues = PaddingValues(24.dp),
    scrollable: Boolean = true,
    refreshing: Boolean = false,
    onRefresh: (() -> Unit)? = null,
    appBar: @Composable () -> Unit = { },
    tabBar: @Composable () -> Unit = { },
    floatingActionButton: @Composable () -> Unit = { },
    drawerContent: @Composable ColumnScope.() -> Unit = { },
    background: @Composable () -> Unit = {
        Image(
            modifier = Modifier
                .fillMaxSize()
                .padding(48.dp),
            painter = painterResource(id = R.drawable.ic_bg_logo),
            contentDescription = "Background",
            colorFilter = ColorFilter.tint(
                if (MaterialTheme.colors.isLight) {
                    FRCKrawlerColor.logoTintLight
                } else {
                    FRCKrawlerColor.logoTintDark
                }
            ),
        )
    },
    content: @Composable ColumnScope.(PaddingValues) -> Unit,
) = Scaffold(
    modifier = modifier,
    scaffoldState = scaffoldState,
    topBar = {
        Column {
            appBar()
            tabBar()
        }
     },
    snackbarHost = { scaffoldState.snackbarHostState },
    floatingActionButton = floatingActionButton,
    floatingActionButtonPosition = FabPosition.End,
    drawerContent = drawerContent,
    backgroundColor = MaterialTheme.colors.background,
) {
    // TODO: Replace with constant 4.dp
    CompositionLocalProvider(LocalCardElevation provides if (isSystemInDarkTheme()) 1.dp else 4.dp) {
        Box(modifier = Modifier.fillMaxSize()) {
            background()
            SwipeRefresh(
                modifier = Modifier.fillMaxSize(),
                state = rememberSwipeRefreshState(refreshing),
                onRefresh = { if (onRefresh != null) onRefresh() },
                swipeEnabled = onRefresh != null,
                refreshTriggerDistance = 96.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (scrollable) Modifier.verticalScroll(state = rememberScrollState()) else Modifier)
                        .padding(contentPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    content(PaddingValues(bottom = 24.dp))
                }
            }
            FRCKrawlerSnackbar(
                snackbarHostState = scaffoldState.snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter),
                onDismiss = {
                    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                },
            )
        }
    }
}