package com.team2052.frckrawler.ui.metrics.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.local.MetricType
import com.team2052.frckrawler.data.model.Metric
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import com.team2052.frckrawler.ui.components.fields.FRCKrawlerTextField
import com.team2052.frckrawler.ui.metrics.MetricInput
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun AddEditMetricDialog(
  mode: AddEditMetricMode,
  metricSetId: Int,
  onClose: () -> Unit
) {
  val viewModel: AddMetricViewModel = hiltViewModel()
  val state = viewModel.state.collectAsState().value
  var showDeleteConfirmation by remember { mutableStateOf(false) }

  LaunchedEffect(mode) {
    when (mode) {
      is AddEditMetricMode.Edit -> viewModel.startEditingMetric(mode.metric, metricSetId)
      is AddEditMetricMode.New -> viewModel.startEditingNewMetric(mode.metricId, metricSetId)
    }
  }

  AddEditMetricContent(
    state = state,
    mode = mode,
    onNameChange = { viewModel.updateName(it) },
    onTypeChange = { viewModel.updateType(it) },
    onSaveClick = {
      viewModel.save()
      onClose()
    },
    onDeleteClick = { showDeleteConfirmation = true },
    onOptionsChange = { viewModel.updateOptions(it) },
    onCancelClick = onClose
  )


  if (showDeleteConfirmation) {
    AlertDialog(
      title = { Text(stringResource(R.string.delete_metric_dialog_title)) },
      text = { Text(stringResource(R.string.delete_metric_dialog_description)) },
      onDismissRequest = { showDeleteConfirmation = false },
      confirmButton = {
        TextButton(onClick = {
          viewModel.deleteMetric()
          showDeleteConfirmation = false
          onClose()
        }) {
          Text(stringResource(R.string.delete))
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteConfirmation = false }) {
          Text(stringResource(R.string.cancel))
        }
      },
    )
  }
}

@Composable
private fun AddEditMetricContent(
  state: AddEditMetricScreenState,
  mode: AddEditMetricMode,
  onNameChange: (String) -> Unit,
  onTypeChange: (MetricType) -> Unit,
  onOptionsChange: (MetricOptions) -> Unit,
  onSaveClick: () -> Unit,
  onDeleteClick: () -> Unit,
  onCancelClick: () -> Unit,
) {
  Column {
    val title = remember(mode) {
      when (mode) {
        is AddEditMetricMode.Edit -> "Edit metric"
        is AddEditMetricMode.New -> "Add new metric"
      }
    }
    Text(
      modifier = Modifier.padding(horizontal = 16.dp),
      text = title,
      style = MaterialTheme.typography.titleLarge
    )

    Spacer(modifier = Modifier.height(12.dp))

    Column(
      modifier = Modifier.padding(horizontal = 16.dp)
    ) {
      FRCKrawlerTextField(
        value = state.name,
        onValueChange = onNameChange,
        label = "Metric name"
      )

      MetricTypeSelector(
        metricType = state.type,
        onMetricTypeSelected = onTypeChange
      )

      when (state.options) {
        MetricOptions.None -> {}
        is MetricOptions.IntRange -> {
          IntRangeOptions(
            options = state.options,
            onOptionsChanged = onOptionsChange
          )
        }

        is MetricOptions.SteppedIntRange -> {
          SteppedIntRangeOptions(
            options = state.options,
            onOptionsChanged = onOptionsChange
          )
        }

        is MetricOptions.StringList -> {
          StringListOptions(
            options = state.options,
            onOptionsChanged = onOptionsChange
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    MetricPreview(
      metric = state.previewMetric
    )

    DialogButtons(
      modifier = Modifier.fillMaxWidth(),
      showDelete = mode is AddEditMetricMode.Edit,
      saveEnabled = state.saveEnabled,
      onSaveClick = onSaveClick,
      onDeleteClick = onDeleteClick,
      onCancelClick = onCancelClick
    )
  }
}

@Composable
private fun MetricPreview(
  metric: Metric
) {
  var metricState by remember(metric.javaClass) { mutableStateOf(metric.defaultValueForPreview()) }
  Column(Modifier.padding(horizontal = 16.dp)) {
    Text(
      text = stringResource(R.string.edit_metric_preview_label),
      style = MaterialTheme.typography.titleLarge
    )
    Spacer(Modifier.height(12.dp))
    Card {
      MetricInput(
        modifier = Modifier.fillMaxWidth(),
        metric = metric,
        state = metricState,
        onStateChanged = { metricState = it }
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MetricTypeSelector(
  metricType: MetricType,
  onMetricTypeSelected: (MetricType) -> Unit
) {
  Row(
    modifier = Modifier.padding(top = 4.dp)
  ) {
    Text(
      modifier = Modifier.alignByBaseline(),
      text = stringResource(R.string.edit_metric_type_label)
    )

    Spacer(Modifier.width(12.dp))

    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
      modifier = Modifier.alignByBaseline(),
      expanded = expanded,
      onExpandedChange = {
        expanded = !expanded
      }
    ) {
      OutlinedButton(
        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
          .padding(8.dp),
        onClick = { expanded = true },
      ) {
        val buttonText = metricType.name
        Text(buttonText)
        Icon(
          modifier = Modifier.padding(start = 8.dp),
          imageVector = Icons.Default.KeyboardArrowDown,
          contentDescription = null
        )
      }
      ExposedDropdownMenu(
        expanded = expanded,
        onDismissRequest = {
          expanded = false
        }
      ) {
        MetricType.entries.forEach { type ->
          DropdownMenuItem(
            text = { Text(type.name) },
            onClick = {
              expanded = false
              onMetricTypeSelected(type)
            }
          )
        }
      }
    }
  }
}

@Composable
private fun StringListOptions(
  modifier: Modifier = Modifier,
  options: MetricOptions.StringList,
  onOptionsChanged: (MetricOptions.StringList) -> Unit
) {
  FRCKrawlerTextField(
    modifier = modifier,
    value = options.options.joinToString(","),
    onValueChange = {
      val newOptions = MetricOptions.StringList(it.split(","))
      onOptionsChanged(newOptions)
    },
    label = "Comma separated list of options"
  )
}

@Composable
private fun IntRangeOptions(
  modifier: Modifier = Modifier,
  options: MetricOptions.IntRange,
  onOptionsChanged: (MetricOptions.IntRange) -> Unit
) {
  Column(modifier = modifier) {
    FRCKrawlerTextField(
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      value = options.range.first.toString(),
      onValueChange = {
        val newFirst = it.toIntOrNull()
        newFirst?.let {
          val newOptions = MetricOptions.IntRange(newFirst..options.range.last)
          onOptionsChanged(newOptions)
        }
        // TODO validation messaging?
      },
      label = "Minimum"
    )
    FRCKrawlerTextField(
      modifier = Modifier.padding(top = 12.dp),
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      value = options.range.last.toString(),
      onValueChange = {
        val newLast = it.toIntOrNull()
        newLast?.let {
          val newOptions = MetricOptions.IntRange(options.range.first..newLast)
          onOptionsChanged(newOptions)
        }
        // TODO validation messaging?
      },
      label = "Maximum"
    )
  }
}

@Composable
private fun SteppedIntRangeOptions(
  modifier: Modifier = Modifier,
  options: MetricOptions.SteppedIntRange,
  onOptionsChanged: (MetricOptions.SteppedIntRange) -> Unit
) {
  Column(modifier = modifier) {
    FRCKrawlerTextField(
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      value = options.range.first.toString(),
      onValueChange = {
        val newFirst = it.toIntOrNull()
        newFirst?.let {
          val newOptions = options.copy(
            range =
            options.range
          )
          onOptionsChanged(newOptions)
        }
        // TODO validation messaging?
      },
      label = "Minimum"
    )

    FRCKrawlerTextField(
      modifier = Modifier.padding(top = 12.dp),
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      value = options.range.last.toString(),
      onValueChange = {
        val newLast = it.toIntOrNull()
        newLast?.let {
          val newOptions = options.copy(range = options.range.first..newLast)
          onOptionsChanged(newOptions)
        }
        // TODO validation messaging?
      },
      label = "Maximum"
    )

    FRCKrawlerTextField(
      modifier = Modifier.padding(top = 12.dp),
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      value = options.step.toString(),
      onValueChange = {
        val newStep = it.toIntOrNull()
        newStep?.let {
          val newOptions = options.copy(step = newStep)
          onOptionsChanged(newOptions)
        }
        // TODO validation messaging?
      },
      label = "Step size"
    )
  }
}

@Composable
private fun DialogButtons(
  modifier: Modifier = Modifier,
  showDelete: Boolean,
  saveEnabled: Boolean,
  onSaveClick: () -> Unit,
  onDeleteClick: () -> Unit,
  onCancelClick: () -> Unit,
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    if (showDelete) {
      TextButton(
        modifier = Modifier.padding(12.dp),
        onClick = onDeleteClick,
        colors = ButtonDefaults.textButtonColors(
          contentColor = MaterialTheme.colorScheme.error
        )
      ) {
        Text(stringResource(R.string.delete).uppercase())
      }
    } else {
      // Empty box so SpaceBetween still works
      Box {}
    }

    Row(
      horizontalArrangement = Arrangement.End
    ) {
      TextButton(
        modifier = Modifier.padding(12.dp),
        onClick = onCancelClick
      ) {
        Text(stringResource(R.string.cancel).uppercase())
      }
      TextButton(
        modifier = Modifier.padding(12.dp),
        enabled = saveEnabled,
        onClick = onSaveClick
      ) {
        Text(stringResource(R.string.save).uppercase())
      }
    }
  }
}

private fun Metric.defaultValueForPreview(): String {
  return when (this) {
    is Metric.BooleanMetric -> "true"
    is Metric.CheckboxMetric -> ""
    is Metric.ChooserMetric -> options.firstOrNull() ?: ""
    is Metric.CounterMetric -> range.first.toString()
    is Metric.SliderMetric -> range.first.toString()
    is Metric.StopwatchMetric -> ""
    is Metric.TextFieldMetric -> ""
  }
}

@FrcKrawlerPreview
@Composable
private fun BooleanMetricPreview() {
  val state = AddEditMetricScreenState(
    name = "Sample boolean",
    type = MetricType.Boolean,
  )
  FrcKrawlerTheme {
    Surface {
      AddEditMetricContent(
        state = state,
        mode = AddEditMetricMode.New(),
        onNameChange = {},
        onTypeChange = {},
        onSaveClick = {},
        onCancelClick = {},
        onDeleteClick = {},
        onOptionsChange = {}
      )
    }
  }
}

@FrcKrawlerPreview
@Composable
private fun StringListOptionsPreview() {
  FrcKrawlerTheme {
    Surface {
      StringListOptions(
        options = MetricOptions.StringList(
          listOf("One", "Two", "Three")
        ),
        onOptionsChanged = {}
      )
    }
  }
}

@FrcKrawlerPreview
@Composable
private fun IntRangeOptionsPreview() {
  FrcKrawlerTheme {
    Surface {
      IntRangeOptions(
        options = MetricOptions.IntRange(
          range = 1..10
        ),
        onOptionsChanged = {}
      )
    }
  }
}

@FrcKrawlerPreview
@Composable
private fun SteppedIntRangeOptionsPreview() {
  FrcKrawlerTheme {
    Surface {
      SteppedIntRangeOptions(
        options = MetricOptions.SteppedIntRange(
          range = 1..10,
          step = 2
        ),
        onOptionsChanged = {}
      )
    }
  }
}