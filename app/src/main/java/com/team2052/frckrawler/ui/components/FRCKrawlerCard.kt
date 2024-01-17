package com.team2052.frckrawler.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalAbsoluteElevation
import androidx.compose.material.LocalElevationOverlay
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.theme.borderWidth
import com.team2052.frckrawler.ui.theme.spaceLarge

private val cardElevation = 4.dp

@Composable
fun ExpandableCardGroup(
    modifier: Modifier = Modifier,
    builder: CardGroupBuilder.() -> Unit,
) {
    val cardGroupBuilder = remember {
        CardGroupBuilder().apply(builder)
    }
    val cards = cardGroupBuilder.build()

    Column(modifier = modifier) {
        for (index in cards.indices) {
            cards[index](index)
            if (index != cards.size - 1) {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

open class CardGroupBuilder {
    private var cards: MutableList<@Composable (Int) -> Unit> = mutableListOf()

    var currentExpandedCardIndex = -1

    fun expandableCard(content: @Composable (Int) -> Unit) {
        cards += content
    }

    fun build() = cards
}

// TODO: Extract border color into material theme background color
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Card(
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit = { CardHeader() },
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
fun CardHeader(
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
fun ExpandableCard(
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit,
    actions: (@Composable RowScope.(Modifier) -> Unit)? = null,
    expanded: Boolean,
    onExpanded: (Boolean) -> Unit = { },
    content: (@Composable ColumnScope.() -> Unit)? = null,
) = Card(
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