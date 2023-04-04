package com.team2052.frckrawler.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.ui.theme.spaceLarge

@Composable
fun FRCKrawlerCard(
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit = { FRCKrawlerCardHeader() },
    actions: (@Composable RowScope.(Modifier) -> Unit)? = null,
    showProgressIndicator: Boolean = false,
    content: (@Composable ColumnScope.() -> Unit)? = null,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        elevation = 2.dp,
    ) {
        Column(verticalArrangement = Arrangement.Center) {
            header()
            if (content != null) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(bottom = if (actions == null) 24.dp else 0.dp),
                ) { content() }
            }
            if (actions != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    ProvideTextStyle(MaterialTheme.typography.button.copy(color = MaterialTheme.colors.primary)) {
                        actions(Modifier.padding(start = spaceLarge))
                    }
                }
            }
        }

        if (showProgressIndicator) {
            LinearProgressIndicator()
        }
    }
}

@Composable
fun FRCKrawlerCardHeader(
    modifier: Modifier = Modifier,
    title: (@Composable RowScope.() -> Unit)? = null,
    description: (@Composable RowScope.() -> Unit)? = null,
    content: (@Composable RowScope.() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(verticalArrangement = Arrangement.Center) {
            title?.let { title ->
                ProvideTextStyle(MaterialTheme.typography.h6) {
                    Row { title() }
                }
            }
            description?.let { description ->
                ProvideTextStyle(MaterialTheme.typography.subtitle1) {

                    CompositionLocalProvider(
                        if (LocalContentAlpha.current == ContentAlpha.disabled) {
                            LocalContentAlpha provides ContentAlpha.disabled
                        } else {
                            LocalContentAlpha provides ContentAlpha.medium
                        }
                    ) {
                        Row { description() }
                    }
                }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (content != null) {
                content()
            }
        }
    }
}