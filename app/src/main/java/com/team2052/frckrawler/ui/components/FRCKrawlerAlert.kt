package com.team2052.frckrawler.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import timber.log.Timber

@Composable
fun Alert(
  modifier: Modifier = Modifier,
  onStateChange: (AlertState) -> Unit,
  confirm: (@Composable () -> Unit)? = { },
  dismiss: (@Composable () -> Unit)? = { },
  title: @Composable () -> Unit = { },
  description: @Composable () -> Unit,
) = AlertDialog(
  modifier = modifier.fillMaxWidth(0.8f),
  confirmButton = {
    if (confirm != null) {
      TextButton(onClick = {
        onStateChange(AlertState.CONFIRMED)
      }) { confirm() }
    }
  },
  dismissButton = {
    if (dismiss != null) {
      TextButton(onClick = {
        onStateChange(AlertState.DISMISSED)
      }) { dismiss() }
    }
  },
  onDismissRequest = {
    onStateChange(AlertState.DISMISSED)
  },
  title = { title() },
  text = { description() },
)

enum class AlertState {
  CONFIRMED,
  DISMISSED,
}