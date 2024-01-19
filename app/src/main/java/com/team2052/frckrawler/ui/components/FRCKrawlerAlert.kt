package com.team2052.frckrawler.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.TextButton
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

@Composable
fun rememberAlertController(): AlertController {
  return rememberSaveable(saver = alertSaver()) {
    AlertController()
  }
}

open class AlertController() {
  lateinit var alerts: Set<String>
  var showingAlerts: MutableState<List<String>> = mutableStateOf(emptyList())

  fun show(name: String) {
    if (alerts.contains(name) && !showingAlerts.value.contains(name)) showingAlerts.value =
      showingAlerts.value + name
  }

  fun hide(name: String) {
    if (alerts.contains(name) && showingAlerts.value.contains(name)) showingAlerts.value =
      showingAlerts.value - name
  }

  fun saveState(): Map<String, Any?> {
    Timber.i("Saving Alert Controller state")
    return mapOf(showingAlertsKey to showingAlerts)
  }

  @Suppress("unchecked_cast")
  fun restoreState(savedState: Map<String, Any?>) {
    Timber.i("Restoring Alert Controller state")
    showingAlerts = savedState[showingAlertsKey] as MutableState<List<String>>
  }

  private companion object {
    const val showingAlertsKey = "ShowingAlerts"
  }
}

private fun alertSaver() = mapSaver(
  save = { it.saveState() },
  restore = { AlertController().apply { restoreState(it) } },
)

@Composable
fun AlertManager(
  alertController: AlertController,
  builder: AlertManagerBuilder.() -> Unit,
) {
  val alertManagerBuilder = remember {
    AlertManagerBuilder().apply(builder)
  }

  val alerts = alertManagerBuilder.build()
  alertController.alerts = alerts.keys.toSet()

  val showingAlerts = remember { alertController.showingAlerts }

  for (alert in alerts) {
    if (showingAlerts.value.contains(alert.key)) {
      val alertScope = AlertScope(alert.key, alertController)
      alert.value(alertScope)
    }
  }
}

open class AlertManagerBuilder {
  private val alerts: MutableMap<String, @Composable AlertScope.() -> Unit> = mutableMapOf()

  fun alert(
    name: String,
    alert: @Composable AlertScope.() -> Unit
  ) {
    alerts += name to alert
  }

  fun build() = alerts
}

open class AlertScope(
  private val name: String,
  private val alertController: AlertController,
) {
  fun hide() = alertController.hide(name)
}

enum class AlertState {
  CONFIRMED,
  DISMISSED,
}