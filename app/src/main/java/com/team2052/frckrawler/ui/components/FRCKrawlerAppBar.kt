package com.team2052.frckrawler.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun FRCKrawlerAppbar(
    modifier: Modifier = Modifier,
    onNavigationPressed: () -> Unit = { },
    title: @Composable RowScope.() -> Unit,
    actions: @Composable RowScope.() -> Unit = { },
) {
    TopAppBar(
        modifier = modifier,
        backgroundColor = MaterialTheme.colors.primary,
        elevation = 0.dp,
        contentColor = MaterialTheme.colors.onSurface,
        actions = actions,
        title = { Row(
            modifier = Modifier
                .clickable(onClick = onNavigationPressed)
                .padding(horizontal = 16.dp),
            content = title
        ) }
    )
}

@Preview
@Composable
fun FRCKrawlerAppBarPreviewLight() {
    FrcKrawlerTheme(darkTheme = false) {
        FRCKrawlerAppbar(title = { Text("Preview") })
    }
}

@Preview
@Composable
fun FRCKrawlerAppBarPreviewDark() {
    FrcKrawlerTheme(darkTheme = true) {
        FRCKrawlerAppbar(title = { Text("Preview") })
    }
}