package com.team2052.frckrawler.ui.initial

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.components.*
import com.team2052.frckrawler.ui.components.fields.FRCKrawlerTextField
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun InitialScreen(
    modifier: Modifier = Modifier,
    onScoutSelected: () -> Unit,
    onServerSelected: (Int) -> Unit,
) {
    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        ProvideTextStyle(MaterialTheme.typography.h6) {
            Text(stringResource(R.string.initial_screen_welcome_message))
            Text(stringResource(R.string.initial_screen_getting_started_message))
        }

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = stringResource(R.string.initial_screen_mode_change_message),
                modifier = modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.body1,
            )
        }

        FRCKrawlerCard(
            modifier = modifier,
            header = {
                FRCKrawlerCardHeader(
                    title = { Text("Scout") },
                    description = { Text("Connect to server and match or pit scout") },
                ) {
                    FRCKrawlerIconButton(
                        icon = Icons.Filled.ChevronRight,
                        onClick = onScoutSelected
                    )
                }
            },
        )

        FRCKrawlerCard(
            modifier = modifier,
            header = {
                FRCKrawlerCardHeader(
                    title = { Text("Server") },
                    description = { Text("Server for scouts to connect to a deposit data") },
                ) {
                    FRCKrawlerIconButton(
                        icon = Icons.Filled.ChevronRight,
                        onClick = { onServerSelected(0) }
                    )
                }
            },
        )
    }
}

@Preview
@Composable
private fun ScoutScreenPreviewLight() {
    FrcKrawlerTheme(darkTheme = false) {
        InitialScreen(onScoutSelected = {}, onServerSelected = {})
    }
}

@Preview
@Composable
private fun ScoutScreenPreviewDark() {
    FrcKrawlerTheme(darkTheme = true) {
        InitialScreen(onScoutSelected = {}, onServerSelected = {})
    }
}