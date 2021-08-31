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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.theme.borderWidth
import com.team2052.frckrawler.ui.theme.spaceLarge
import com.team2052.frckrawler.ui.theme.spaceMedium
import java.util.*

private val cardElevation = 4.dp

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
    header: @Composable () -> Unit = { FRCKrawlerCardHeader() },
    actions: (@Composable RowScope.(Modifier) -> Unit)? = null,
    content: (@Composable ColumnScope.() -> Unit)? = null,
) {
    val elevationOverlay = LocalElevationOverlay.current
    val absoluteElevation = LocalAbsoluteElevation.current + cardElevation / 2
    val borderColor = elevationOverlay?.apply(MaterialTheme.colors.surface, absoluteElevation)
        ?: MaterialTheme.colors.surface

    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(borderWidth, borderColor),
        elevation = cardElevation,
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
    }
}

@Composable
fun FRCKrawlerCardHeader(
    modifier: Modifier = Modifier,
    title: (@Composable RowScope.() -> Unit)? = null,
    description: (@Composable RowScope.() -> Unit)? = null,
) = Column(modifier = modifier
    .fillMaxWidth()
    .padding(horizontal = 24.dp)
    .padding(bottom = 24.dp)) {
    title?.let { title ->
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.h6.copy(fontWeight = FontWeight.SemiBold)
        ) { Row(modifier = Modifier.padding(top = 24.dp)) { title() } }
    }
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
    actions: (@Composable RowScope.(Modifier) -> Unit)? = null,
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
                    imageVector = if (content != null || actions != null) {
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
    actions = if (expanded) actions else null,
    content = if (expanded) content else null,
)