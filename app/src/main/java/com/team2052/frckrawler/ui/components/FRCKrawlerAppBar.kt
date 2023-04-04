package com.team2052.frckrawler.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import com.team2052.frckrawler.ui.theme.maroon200

@Composable
fun FRCKrawlerAppBar(
    modifier: Modifier = Modifier,
    onNavigationClicked: () -> Unit = {},
    navigationIcon: ImageVector = Icons.Filled.Menu,
    title: @Composable RowScope.() -> Unit,
) {
    TopAppBar(
        modifier = modifier,
        elevation = 4.dp,
        navigationIcon = {
            IconButton(onClick = onNavigationClicked) {
                Icon(
                    imageVector = navigationIcon,
                    contentDescription = "navigation",
                )
            }
        },
        backgroundColor = maroon200,
        contentColor = Color.White,
        title = {
            ProvideTextStyle(
                MaterialTheme.typography.h6
            ) {
                Row {
                    title()
                }
            }
        }
    )
}

@Preview
@Composable
private fun FRCKrawlerAppBarPreviewLight() {
    FrcKrawlerTheme(darkTheme = false) {
        FRCKrawlerAppBar() {
            Text("Preview")
        }
    }
}

@Preview
@Composable
private fun FRCKrawlerAppBarPreviewDark() {
    FrcKrawlerTheme(darkTheme = true) {
        FRCKrawlerAppBar() {
            Text("Preview")
        }
    }
}