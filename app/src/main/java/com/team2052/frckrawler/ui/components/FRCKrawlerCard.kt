package com.team2052.frckrawler.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.R
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
@Composable
fun Card(
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  onClick: (() -> Unit)? = null,
  header: (@Composable () -> Unit)? = null,
  actions: (@Composable RowScope.(Modifier) -> Unit)? = null,
  content: (@Composable ColumnScope.() -> Unit)? = null,
) {
  if (onClick != null) {
    ElevatedCard(
      modifier = modifier
        .fillMaxWidth(),
      colors = CardDefaults.elevatedCardColors(),
      onClick = onClick,
      enabled = enabled
    ) {
      CardContent(
        modifier = Modifier
          .animateContentSize(
            animationSpec = tween(
              durationMillis = 300,
              easing = FastOutSlowInEasing,
            )
          ),
        header = header,
        actions = actions,
        content = content,
      )
    }
  } else {
    Card(
      modifier = modifier
        .fillMaxWidth(),
    ) {
      CardContent(
        modifier = Modifier
          .animateContentSize(
            animationSpec = tween(
              durationMillis = 300,
              easing = FastOutSlowInEasing,
            )
          ),
        header = header,
        actions = actions,
        content = content,
      )
    }
  }
}

@Composable
private fun CardContent(
  modifier: Modifier = Modifier,
  header: (@Composable () -> Unit)? = null,
  actions: (@Composable RowScope.(Modifier) -> Unit)? = null,
  content: (@Composable ColumnScope.() -> Unit)? = null,
) {
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.Center,
  ) {
    if (header != null) {
      header()
    }
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
        ProvideTextStyle(MaterialTheme.typography.labelLarge) {
          actions(Modifier.padding(start = spaceLarge))
        }
      }
    }
  }
}

@Composable
fun CardHeader(
  modifier: Modifier = Modifier,
  icon: (@Composable () -> Unit)? = null,
  title: (@Composable RowScope.() -> Unit)? = null,
  description: (@Composable RowScope.() -> Unit)? = null,
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 24.dp),
  ) {
    Row {
      icon?.let {
        Box(
          modifier = modifier.padding(vertical = 6.dp)
        ) {
            icon()
        }
        Spacer(Modifier.width(12.dp))
      }
      Column {
        title?.let { title ->
          CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
          ) { Row { title() } }
        }
        description?.let { description ->
          CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.bodyLarge
          ) { Row { description() } }
        }
      }
    }
  }
}

@Composable
fun ExpandableCard(
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  header: @Composable () -> Unit,
  actions: (@Composable RowScope.(Modifier) -> Unit)? = null,
  expanded: Boolean,
  onExpanded: (Boolean) -> Unit = { },
  content: (@Composable ColumnScope.() -> Unit)? = null,
) {
  Card(
    modifier = modifier,
    onClick = { onExpanded(!expanded) },
    enabled = enabled,
    header = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Row(modifier = Modifier.weight(0.5f)) { header() }

        if (enabled) {
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
              } else {
                Icons.AutoMirrored.Filled.KeyboardArrowRight
              },
              contentDescription = stringResource(R.string.cd_expandable_card),
            )
          }
        }
      }
    },
    actions = if (expanded) actions else null,
    content = if (expanded) content else null,
  )
}