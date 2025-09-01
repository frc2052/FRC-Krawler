package com.team2052.frckrawler.ui.server.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import com.team2052.frckrawler.ui.components.Card
import com.team2052.frckrawler.ui.components.CardHeader
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun NotificationPromptCard(
  modifier: Modifier = Modifier,
  onDismiss: () -> Unit = {},
  onAccept: () -> Unit = {},
) {
  Card(
    modifier = modifier,
    header = {
      CardHeader(
        title = { Text(stringResource(R.string.server_notification_prompt_title)) },
      )
    },
    ) {
    Column(
      horizontalAlignment = Alignment.End
    ) {
      Text(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(R.string.server_notification_prompt_body)
      )

      Row {
        TextButton(
          onClick = onDismiss
        ) {
          Text(stringResource(R.string.server_notification_prompt_no))
        }

        Button(
          onClick = onAccept
        ) {
          Text(stringResource(R.string.server_notification_prompt_allow))
        }
      }
    }
  }
}

@FrcKrawlerPreview
@Composable
private fun NotificationPromptPreview() {
  FrcKrawlerTheme {
    NotificationPromptCard()
  }
}