package com.team2052.frckrawler.ui.components.fields

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> FRCKrawlerDropdown(
  modifier: Modifier = Modifier,
  value: T?,
  getLabel: (T?) -> String,
  onValueChange: (T?) -> Unit,
  onFocusChange: (Boolean) -> Unit = {},
  validity: Boolean = true,
  enabled: Boolean = true,
  label: String,
  dropdownItems: List<T>,
) {
  var expanded by remember { mutableStateOf(false) }

  ExposedDropdownMenuBox(
    modifier = modifier,
    expanded = expanded,
    onExpandedChange = {
      expanded = it
    }
  ) {
    FRCKrawlerTextField(
      modifier = Modifier.menuAnchor(),
      readOnly = true,
      value = getLabel(value),
      onValueChange = { },
      onFocusChange = onFocusChange,
      icon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
      validity = validity,
      isError = { validity -> !validity },
      enabled = enabled,
      label = label,
    )

    ExposedDropdownMenu(
      expanded = expanded,
      onDismissRequest = {
        expanded = false
      }
    ) {
      dropdownItems.forEach { item ->
        DropdownMenuItem(
          text = { Text(getLabel(item)) },
          onClick = {
            onValueChange(item)
            expanded = false
          },
        )
      }
    }
  }
}

@FrcKrawlerPreview
@Composable
private fun FrcKrawlerDropdownPreview() {
  var value: String? by remember { mutableStateOf("One") }
  FrcKrawlerTheme {
    Surface {
      FRCKrawlerDropdown(
        value = value,
        getLabel = { it ?: "select an option" },
        onValueChange = { value = it },
        label = "Number",
        dropdownItems = listOf("One", "Two", "Three")
      )
    }
  }
}