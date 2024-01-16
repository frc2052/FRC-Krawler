package com.team2052.frckrawler.ui.metrics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.data.model.Metric
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@OptIn(ExperimentalMaterialApi::class)
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
            modifier = Modifier.padding(8.dp),
            onClick = { },
        ) {
            Text(state)
            Icon(
                modifier = Modifier.padding(start = 8.dp),
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Open Menu"
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
                    onClick = {
                        expanded = false
                        onStateChanged(option)
                    }
                ) {
                    Text(text = option)
                }
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
                .background(MaterialTheme.colors.surface),
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