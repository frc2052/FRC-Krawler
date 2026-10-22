package com.team2052.frckrawler.ui.components.fields

import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.team2052.frckrawler.R
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
  searchable: Boolean = false,
) {
  val focusManager = LocalFocusManager.current
  var expanded by remember { mutableStateOf(value = false) }
  var isEditing by remember(value) { mutableStateOf(value = false) }
  val textFieldState = remember(value) { TextFieldState(getLabel(value)) }

  val filterQuery = if (searchable && isEditing) textFieldState.text.toString() else ""

  val filteredItems = remember(dropdownItems, filterQuery, getLabel) {
    val trimmed = filterQuery.trim()
    if (trimmed.isEmpty()) {
      dropdownItems
    } else {
      dropdownItems.filter { item ->
        getLabel(item).contains(trimmed, ignoreCase = true)
      }
    }
  }

  ExposedDropdownMenuBox(
    modifier = modifier,
    expanded = expanded && enabled,
    onExpandedChange = {
      if (enabled) {
        expanded = it
        if (!expanded) {
          isEditing = false
          textFieldState.setTextAndPlaceCursorAtEnd(getLabel(value))
        }
      }
    },
  ) {
    FRCKrawlerTextField(
      modifier = Modifier.menuAnchor(
        type = if (searchable) ExposedDropdownMenuAnchorType.PrimaryEditable else ExposedDropdownMenuAnchorType.PrimaryNotEditable,
        enabled = enabled,
      ),
      readOnly = !searchable,
      state = textFieldState,
      inputTransformation = if (searchable) {
        InputTransformation {
          if (!asCharSequence().contentEquals(textFieldState.text)) {
            isEditing = true
            expanded = true
          }
        }
      } else {
        null
      },
      onFocusChange = { focused ->
        onFocusChange(focused)
        if (!focused) {
          isEditing = false
          textFieldState.setTextAndPlaceCursorAtEnd(getLabel(value))
          expanded = false
        }
      },
      icon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
      validity = validity,
      isError = { validity -> !validity },
      enabled = enabled,
      label = label,
    )

    ExposedDropdownMenu(
      expanded = expanded && enabled,
      onDismissRequest = {
        expanded = false
        isEditing = false
        textFieldState.setTextAndPlaceCursorAtEnd(getLabel(value))
      },
    ) {
      if (filteredItems.isEmpty()) {
        DropdownMenuItem(
          text = {
            Text(
              text = stringResource(R.string.no_matching_events),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          },
          enabled = false,
          onClick = {},
        )
      } else {
        filteredItems.forEach { item ->
          val itemLabel = getLabel(item)
          DropdownMenuItem(
            text = {
              if (searchable && filterQuery.trim().isNotEmpty()) {
                Text(text = buildHighlightedText(itemLabel, filterQuery))
              } else {
                Text(itemLabel)
              }
            },
            onClick = {
              onValueChange(item)
              textFieldState.setTextAndPlaceCursorAtEnd(itemLabel)
              isEditing = false
              expanded = false
              focusManager.clearFocus()
            },
          )
        }
      }
    }
  }
}

/**
 * Builds an [AnnotatedString] that applies [highlightStyle] (default: bold) to all occurrences
 * of [query] within [text], ignoring case.
 */
fun buildHighlightedText(
  text: String,
  query: String,
  highlightStyle: SpanStyle = SpanStyle(fontWeight = FontWeight.Bold),
): AnnotatedString {
  val trimmedQuery = query.trim()
  if (trimmedQuery.isEmpty()) {
    return AnnotatedString(text)
  }
  return buildAnnotatedString {
    val lowercaseText = text.lowercase()
    val lowercaseQuery = trimmedQuery.lowercase()
    var startIndex = 0
    while (startIndex < text.length) {
      val matchIndex = lowercaseText.indexOf(lowercaseQuery, startIndex)
      if (matchIndex == -1) {
        append(text.substring(startIndex))
        break
      }
      if (matchIndex > startIndex) {
        append(text.substring(startIndex, matchIndex))
      }
      val endIndex = matchIndex + trimmedQuery.length
      withStyle(style = highlightStyle) {
        append(text.substring(matchIndex, endIndex))
      }
      startIndex = endIndex
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
        onValueChange = { },
        label = "Number",
        dropdownItems = listOf("One", "Two", "Three"),
      )
    }
  }
}

@FrcKrawlerPreview
@Composable
private fun FrcKrawlerSearchableDropdownPreview() {
  val events = listOf("10,000 Lakes Regional", "Great Lakes Regional", "Seven Rivers Regional")
  var selected: String? by remember { mutableStateOf(null) }
  FrcKrawlerTheme {
    Surface {
      FRCKrawlerDropdown(
        value = selected,
        getLabel = { it ?: "" },
        onValueChange = { selected = it },
        label = "Event",
        dropdownItems = events,
        searchable = true,
      )
    }
  }
}
