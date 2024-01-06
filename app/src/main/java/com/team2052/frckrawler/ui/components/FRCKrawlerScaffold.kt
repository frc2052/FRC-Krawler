package com.team2052.frckrawler.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FabPosition
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.theme.darkGray
import com.team2052.frckrawler.ui.theme.lightGray
import com.team2052.frckrawler.ui.theme.spaceExtraLarge
import com.team2052.frckrawler.ui.theme.spaceLarge

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
        Surface(
            elevation = 4.dp
        ) {
            Column {
                appBar()
                tabBar()
            }
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