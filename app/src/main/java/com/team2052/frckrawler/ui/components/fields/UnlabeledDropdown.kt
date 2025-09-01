package com.team2052.frckrawler.ui.components.fields

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> UnlabeledDropdown(
  modifier: Modifier = Modifier,
  value: T,
  getValueLabel: @Composable (T) -> String,
  onValueChange: (T) -> Unit,
  dropdownItems: List<T>,
) {

  var expanded by remember { mutableStateOf(false) }
  ExposedDropdownMenuBox(
    modifier = modifier,
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
      val buttonText = getValueLabel(value)
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
      dropdownItems.forEach { item ->
        DropdownMenuItem(
          text = { Text(getValueLabel(item)) },
          onClick = {
            expanded = false
            onValueChange(item)
          }
        )
      }
    }
  }
}