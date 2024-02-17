package com.team2052.frckrawler.ui.migration

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.local.migration.LegacyDatabaseMigration
import com.team2052.frckrawler.ui.components.FRCKrawlerScaffold
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import com.team2052.frckrawler.ui.theme.spaceExtraLarge

@Composable
fun LegacyMigrationScreen(
  migration: LegacyDatabaseMigration,
  onMigrationCompleted: () -> Unit
) {
  LaunchedEffect(true) {
    migration.migrate()
    onMigrationCompleted()
  }

  FRCKrawlerScaffold {
    MigratingContent()
  }
}

@Composable
private fun MigratingContent() {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(spaceExtraLarge),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    Image(
      modifier = Modifier.fillMaxWidth(),
      colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.outline),
      painter = painterResource(R.drawable.ic_logo),
      contentDescription = null,
    )

    Spacer(Modifier.height(16.dp))
    LinearProgressIndicator(
      modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(16.dp))

    Text(
      text = stringResource(R.string.migrating_message),
      style = MaterialTheme.typography.headlineSmall,
      textAlign = TextAlign.Center
    )
  }
}

@Preview
@Composable
private fun MigrationScreenPreview() {
  FrcKrawlerTheme {
    Surface {
      MigratingContent()
    }
  }
}