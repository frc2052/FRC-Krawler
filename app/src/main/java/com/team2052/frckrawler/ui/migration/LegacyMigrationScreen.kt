package com.team2052.frckrawler.ui.migration

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.local.migration.LegacyDatabaseMigration
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import com.team2052.frckrawler.ui.theme.spaceExtraLarge
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

@Composable
fun LegacyMigrationScreen(
  migration: LegacyDatabaseMigration,
  onMigrationCompleted: () -> Unit
) {
  var failed by remember { mutableStateOf(false) }
  LaunchedEffect(true) {
    try {
      migration.migrate()
      onMigrationCompleted()
    } catch (e: Exception) {
      FirebaseCrashlytics.getInstance().recordException(e)
      failed = true
    }
  }

  Scaffold { contentPadding ->
    MigratingContent(
      modifier = Modifier.padding(contentPadding)
        .consumeWindowInsets(contentPadding)
    )

    if (failed) {
      val coroutineScope = rememberCoroutineScope()
      FailedDialog(
        onCloseApp = { exitProcess(0) },
        onDeleteData = {
          coroutineScope.launch {
            migration.deleteDatabase()
            onMigrationCompleted()
          }
        }
      )
    }
  }
}

@Composable
private fun MigratingContent(
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
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

@Composable
private fun FailedDialog(
  onCloseApp: () -> Unit,
  onDeleteData: () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onCloseApp,
    dismissButton = {
      TextButton(
        onClick = onDeleteData
      ) {
        Text(
          text = stringResource(R.string.migration_failed_delete_data),
          color = MaterialTheme.colorScheme.error
        )
      }
    },
    confirmButton = {
      TextButton(
        onClick = onCloseApp
      ) {
        Text(stringResource(R.string.migration_failed_close))
      }
    },
    iconContentColor = MaterialTheme.colorScheme.error,
    icon = { Icon(Icons.Default.Error, contentDescription = null) },
    title = { Text(stringResource(R.string.migration_failed_title)) },
    text = { Text(stringResource(R.string.migration_failed_body)) },
    properties = DialogProperties(
      dismissOnBackPress = false,
      dismissOnClickOutside = false
    )
  )
}

@FrcKrawlerPreview
@Composable
private fun MigrationScreenPreview() {
  FrcKrawlerTheme {
    Surface {
      MigratingContent()
    }
  }
}