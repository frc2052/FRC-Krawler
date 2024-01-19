package com.team2052.frckrawler.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun FRCKrawlerSnackbar(
  snackbarHostState: SnackbarHostState,
  modifier: Modifier = Modifier,
  onDismiss: () -> Unit,
) {
  SnackbarHost(
    hostState = snackbarHostState,
    modifier = modifier,
    snackbar = { snackbarData ->
      Snackbar(
        modifier = Modifier.padding(24.dp),
        action = {
          TextButton(onClick = { onDismiss() }) {
            snackbarData.actionLabel?.let { actionLabel ->
              Text(text = actionLabel.toUpperCase(Locale.getDefault()), color = Color.White)
            }
          }
        },
      ) { Text(snackbarData.message) }
    }
  )
}

class FRCKrawlerSnackbarController(val scope: CoroutineScope) {

  private var snackbarJob: Job? = null

  init {
    cancelCurrentJob()
  }

  fun showSnackbar(
    scaffoldState: ScaffoldState,
    message: String,
    actionLabel: String?,
  ) {
    snackbarJob?.let { cancelCurrentJob() }
    snackbarJob = scope.launch {
      scaffoldState.snackbarHostState.showSnackbar(
        message = message,
        actionLabel = actionLabel
      )
      cancelCurrentJob()
    }
  }

  private fun cancelCurrentJob() {
    snackbarJob?.let { job ->
      job.cancel()
      snackbarJob = job
    }
  }
}