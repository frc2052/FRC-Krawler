package com.team2052.frckrawler.ui.analyze

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.model.Metric
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import com.team2052.frckrawler.ui.theme.StaticColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyzeDataFilterSheet(
  sheetState: SheetState = rememberModalBottomSheetState(),
  onDismissRequest: () -> Unit,
  onFilter: (Metric, String?) -> Unit,
  filterState: SortFilterState,
) {
  ModalBottomSheet(
    sheetState = sheetState,
    onDismissRequest = onDismissRequest,
  ) {
    Column(
      modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
      filterState.availableMetrics.forEach { metric ->
        MetricRow(
          metric = metric,
          isSelected = filterState.selectedMetric == metric,
          selectedOption = filterState.selectedMetricOption,
          onSelect = onFilter
        )
      }
    }
  }
}

@Composable
private fun MetricRow(
  modifier: Modifier = Modifier,
  metric: Metric,
  isSelected: Boolean,
  selectedOption: String?,
  onSelect: (Metric, String?) -> Unit,
) {
  var expanded by remember { mutableStateOf(false) }
  Column(
    modifier = modifier.fillMaxWidth()
      .animateContentSize()
  ) {
    Row(
      modifier = Modifier.fillMaxWidth()
        .clickable {
          if (metric.hasOptions()) {
            expanded = !expanded
          } else {
            onSelect(metric, null)
          }
        }
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      val contentColor = if (isSelected) {
        StaticColors.successContainer
      } else {
        MaterialTheme.colorScheme.onSurface
      }
      CompositionLocalProvider(LocalContentColor provides contentColor) {
        if (isSelected) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = stringResource(R.string.filter_selected_description),
          )
          Spacer(Modifier.width(8.dp))
        }

        val text = if (selectedOption != null && isSelected) {
          "${metric.name} - $selectedOption"
        } else metric.name
        Text(
          text = text,
          style = MaterialTheme.typography.titleMedium,
        )

        if (metric.hasOptions()) {
          Spacer(Modifier.width(4.dp))
          Icon(
            modifier = Modifier.padding(start = 8.dp),
            imageVector = if (expanded) {
              Icons.Default.KeyboardArrowUp
            } else {
              Icons.Default.KeyboardArrowDown
            },
            contentDescription = if (expanded) {
              stringResource(R.string.cd_dropdown_expand)
            } else {
              stringResource(R.string.cd_dropdown_collapse)
            }
          )
        }
      }
    }

    if (expanded) {
      MetricOptions(
        modifier = Modifier.fillMaxWidth(),
        options = when (metric) {
          is Metric.ChooserMetric -> metric.options
          is Metric.CheckboxMetric -> metric.options
          else -> emptyList()
        },
        selectedOption = selectedOption,
        onOptionSelected = { onSelect(metric, it) }
      )
    }
  }
}

@Composable
private fun MetricOptions(
  modifier: Modifier = Modifier,
  options: List<String>,
  selectedOption: String?,
  onOptionSelected: (String) -> Unit
) {
  Column(
    modifier = modifier
  ) {
    options.forEach { option ->
      val isSelected = selectedOption == option
      val contentColor = if (isSelected) {
        StaticColors.successContainer
      } else {
        MaterialTheme.colorScheme.onSurface
      }
      CompositionLocalProvider(LocalContentColor provides contentColor) {
        Row(
          modifier = Modifier.clickable { onOptionSelected(option) }
            .fillMaxWidth()
            .padding(start = 36.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          if (isSelected) {
            Icon(
              modifier = Modifier.size(16.dp),
              imageVector = Icons.Default.Check,
              contentDescription = stringResource(R.string.filter_selected_description),
            )
            Spacer(Modifier.width(8.dp))
          }
          Text(
            text = option,
            style = MaterialTheme.typography.bodyMedium,
          )
        }
      }
    }
  }
}

fun Metric.hasOptions(): Boolean = this is Metric.ChooserMetric || this is Metric.CheckboxMetric

@OptIn(ExperimentalMaterial3Api::class)
@FrcKrawlerPreview
@Composable
private fun FilterSheetPreview() {
  val metric1 = Metric.ChooserMetric(
    id = "1",
    name = "Color",
    options = listOf("Red", "Blue", "Green"),
    priority = 0,
    enabled = true
  )
  val metric2 = Metric.BooleanMetric(
    id = "2",
    name = "Has wheels",
    priority = 0,
    enabled = true
  )
  FrcKrawlerTheme {
    val sheetState = rememberStandardBottomSheetState(
      initialValue = SheetValue.Expanded
    )
    LaunchedEffect(true) {
      sheetState.show()
    }
    AnalyzeDataFilterSheet(
      sheetState = sheetState,
      onDismissRequest = {},
      onFilter = { _, _ -> },
      filterState = SortFilterState(
        availableMetrics = listOf(metric1, metric2),
        selectedMetric = metric1,
        selectedMetricOption = "Red",
        sortMode = AnalyzeSortMode.Ascending,
      ),
    )
  }
}