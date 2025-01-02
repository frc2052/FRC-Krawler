package com.team2052.frckrawler.ui.metrics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.data.model.Metric
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooserMetric(
  state: String,
  onStateChanged: (String) -> Unit,
  options: List<String>
) {
  var expanded by remember { mutableStateOf(false) }
  ExposedDropdownMenuBox(
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
      Text(state)
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
      options.forEach { option ->
        DropdownMenuItem(
          text = { Text(option) },
          onClick = {
            expanded = false
            onStateChanged(option)
          }
        )
      }
    }
  }
}

@FrcKrawlerPreview
@Composable
private fun ChooserMetricPreview() {
  var state by remember { mutableStateOf("two") }
  FrcKrawlerTheme {
    MetricInput(
      modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surface),
      metric = Metric.ChooserMetric(
        name = "Chooser metric",
        enabled = true,
        priority = 0,
        options = listOf("one", "two", "three")
      ),
      state = state,
      onStateChanged = { state = it }
    )
  }
}