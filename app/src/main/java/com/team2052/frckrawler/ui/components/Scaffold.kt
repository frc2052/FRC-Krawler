package com.team2052.frckrawler.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.theme.spaceExtraLarge
import com.team2052.frckrawler.ui.theme.spaceLarge

@Composable
fun FRCKrawlerScaffold(
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    innerPadding: PaddingValues = PaddingValues(bottom = spaceLarge),
    appBar: @Composable () -> Unit = { },
    tabBar: @Composable () -> Unit = { },
    snackbarHost: @Composable (SnackbarHostState) -> Unit = { SnackbarHost(it) },
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    drawerContent: @Composable ColumnScope.() -> Unit = { },
    background: @Composable () -> Unit = {
        Image(
            modifier = Modifier
                .fillMaxSize()
                .padding(spaceExtraLarge),
            painter = painterResource(R.drawable.ic_logo),
            contentDescription = stringResource(R.string.cd_background_logo),
            alpha = 0.2f,
        )
    },
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        topBar = {
            Column {
                appBar()
                tabBar()
            }
        },
        snackbarHost = snackbarHost,
        drawerContent = drawerContent,
        backgroundColor = MaterialTheme.colors.background,
    ) { screenPadding ->
        ModalDrawer(
            drawerState = drawerState,
            drawerContent = drawerContent,
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(screenPadding).padding(spaceLarge)) {
                background()
                content(innerPadding)
            }
        }
    }
}