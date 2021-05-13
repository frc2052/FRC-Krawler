package com.team2052.frckrawler.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.R
import java.util.*

@Composable
fun FRCKrawlerExpandableCardGroup(
    modifier: Modifier = Modifier,
    content: List<@Composable (Modifier, Int) -> Unit>,
) {
    var internalCardCount = 0
    for(composable in content) { composable(modifier, internalCardCount++) }
}

@Composable
fun FRCKrawlerCard(
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit,
    actions: Map<String, () -> Unit> = emptyMap(),
    content: (@Composable ColumnScope.() -> Unit)? = null,
) = Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(4.dp),
    elevation = LocalCardElevation.current,
) {
    Column(
        modifier = Modifier
            .padding(24.dp)
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing,
                )
            )
    ) {
        header()
        content?.let { content ->
            Column(
                modifier = Modifier.padding(top = 24.dp).also {
                    if (actions.isNotEmpty()) it.padding(bottom = 24.dp)
                }
            ) { content() }
        }
        if (actions.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                actions.forEach { action ->
                    TextButton(
                        modifier = Modifier.padding(start = 24.dp),
                        onClick = { action.value() },
                    ) {
                        Text(
                            text = action.key.toUpperCase(Locale.getDefault()),
                            color = MaterialTheme.colors.secondary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FRCKrawlerCardHeader(
    modifier: Modifier = Modifier,
    title: @Composable RowScope.() -> Unit,
    description: (@Composable RowScope.() -> Unit)? = null,
) = Column(modifier = modifier.fillMaxWidth()) {
    CompositionLocalProvider(
        LocalTextStyle provides if (description != null)
            MaterialTheme.typography.h6 else MaterialTheme.typography.h5
    ) { Row { title() } }
    description?.let { description ->
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.subtitle1
        ) { Row { description() } }
    }
}

@Composable
fun FRCKrawlerExpandableCard(
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit,
    actions: Map<String, () -> Unit> = emptyMap(),
    expanded: Boolean,
    onExpanded: (Boolean) -> Unit = { },
    content: (@Composable ColumnScope.() -> Unit)? = null,
) = FRCKrawlerCard(
    modifier = modifier,
    header = {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Row(modifier = Modifier.weight(0.5f)) { header() }
            IconButton(
                modifier = Modifier.size(48.dp),
                onClick = { onExpanded(!expanded) },
            ) {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    imageVector = if (content != null || actions.isNotEmpty()) {
                        when (expanded) {
                            true -> Icons.Filled.KeyboardArrowDown
                            false -> Icons.Filled.KeyboardArrowUp
                        }
                    } else { Icons.Filled.KeyboardArrowRight },
                    contentDescription = stringResource(R.string.cd_expandable_card),
                )
            }
        }
    },
    actions = if (expanded) actions else emptyMap(),
    content = if (expanded) content else null,
)