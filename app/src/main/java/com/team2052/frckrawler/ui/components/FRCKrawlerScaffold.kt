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

@Composable
fun FRCKrawlerScaffold(
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
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
        !scaffoldState.drawerState.isClosed
    ) drawerContent else null,
    backgroundColor = MaterialTheme.colors.background,
) { contentPadding ->
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(contentPadding)
    ) {
        background()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            content(PaddingValues(bottom = spaceLarge))
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