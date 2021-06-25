package com.team2052.frckrawler.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.R
import java.util.*

@Composable
fun FRCKrawlerExpandableCardGroup(
    modifier: Modifier = Modifier,
    content: () -> List<@Composable (Modifier, Int) -> Unit>,
) {
    var internalCardCount = 0
    for(composable in content()) { composable(modifier, internalCardCount++) }
}

// TODO: Extract border color into material theme background color
@Composable
fun FRCKrawlerCard(
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit,
    actions: Map<String, () -> Unit> = emptyMap(),
    content: (@Composable ColumnScope.() -> Unit)? = null,
) {
    val elevationOverlay = LocalElevationOverlay.current
    val absoluteElevation = LocalAbsoluteElevation.current + LocalCardElevation.current / 2
    val borderColor = elevationOverlay?.apply(MaterialTheme.colors.surface, absoluteElevation)
        ?: MaterialTheme.colors.surface

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(2.dp, borderColor),
        elevation = LocalCardElevation.current,
    ) {
        Column(
            modifier = Modifier
                .animateContentSize(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing,
                    )
                ),
            verticalArrangement = Arrangement.Center,
        ) {
            header()
            if (content != null) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp).also {
                        if (actions.isNotEmpty()) it.padding(bottom = 24.dp)
                    }
                ) { content() }
            }
            if (actions.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
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
            } else if (content != null) {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun FRCKrawlerCardHeader(
    modifier: Modifier = Modifier,
    title: @Composable RowScope.() -> Unit,
    description: (@Composable RowScope.() -> Unit)? = null,
) = Column(modifier = modifier
    .fillMaxWidth()
    .padding(24.dp)) {
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(modifier = Modifier.weight(0.5f)) { header() }
            IconButton(
                modifier = Modifier
                    .padding(end = 24.dp)
                    .size(36.dp),
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