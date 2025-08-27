package com.team2052.frckrawler.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme


@Composable
fun StartScoutingCard(
  icon: ImageVector,
  label: String,
  onClick: () -> Unit,
  enabled: Boolean,
  modifier: Modifier = Modifier,
) {
  androidx.compose.material3.Card(
    modifier = modifier,
    enabled = enabled,
    onClick = onClick,
  ) {
    StartScoutingCardContent(
      icon = icon,
      label = label
    )
  }
}

@Composable
private fun StartScoutingCardContent(
  icon: ImageVector,
  label: String,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      modifier = Modifier.size(36.dp),
      imageVector = icon,
      contentDescription = null
    )

    Spacer(Modifier.width(24.dp))

    Text(
      text = label,
      style = MaterialTheme.typography.headlineSmall
    )
  }
}

@Preview
@Composable
private fun StartScoutingCardPreview() {
  FrcKrawlerTheme {
    Surface {
      StartScoutingCard(
        icon = Icons.Default.EmojiEvents,
        label = "Start match scouting",
        onClick = { },
        enabled = true,
      )
    }
  }
}

@Preview
@Composable
private fun StartScoutingCardDisabledPreview() {
  FrcKrawlerTheme {
    Surface {
      StartScoutingCard(
        icon = Icons.Default.EmojiEvents,
        label = "Start match scouting",
        onClick = { },
        enabled = false,
      )
    }
  }
}