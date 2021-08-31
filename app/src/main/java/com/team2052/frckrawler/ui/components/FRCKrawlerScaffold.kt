package com.team2052.frckrawler.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.theme.*

data class FRCKrawlerScaffold(
    val thing: String
)

@Composable
fun FRCKrawlerScaffold(
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    contentPadding: PaddingValues = PaddingValues(spaceLarge),
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
                .padding(spaceExtraLarge),
            painter = painterResource(R.drawable.ic_logo),
            contentDescription = stringResource(R.string.cd_background_logo),
            colorFilter = ColorFilter.tint(
                if (MaterialTheme.colors.isLight) {
                    darkGray.copy(alpha = 0.05f)
                } else {
                    lightGray.copy(alpha = 0.05f)
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
    drawerContent = if (
        scaffoldState.drawerState.isClosed && !scaffoldState.drawerState.isAnimationRunning
    ) drawerContent else null,
    backgroundColor = MaterialTheme.colors.background,
) {
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
                    .then(
                        if (scrollable)
                            Modifier.verticalScroll(rememberScrollState())
                        else
                            Modifier
                    )
                    .padding(contentPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                content(PaddingValues(bottom = spaceLarge))
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