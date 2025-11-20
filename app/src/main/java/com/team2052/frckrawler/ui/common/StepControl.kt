package com.team2052.frckrawler.ui.common

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun StepControl(
  value: Int,
  onValueChanged: (Int) -> Unit,
  modifier: Modifier = Modifier,
  step: Int = 1,
  range: IntRange = Int.MIN_VALUE..Int.MAX_VALUE
) {
  var isFocused by remember { mutableStateOf(false) }
  var focusedValue by remember { mutableStateOf("") }

  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically
  ) {
    IconButton(
      onClick = { onValueChanged(value - step) },
      enabled = range.contains(value - step)
    ) {
      Icon(
        imageVector = Icons.Default.Remove,
        contentDescription = stringResource(R.string.step_control_decrease),
        tint = MaterialTheme.colorScheme.primary
      )
    }

    BasicTextField(
      modifier = Modifier
        .defaultMinSize(
          minWidth = 48.dp
        )
        .focusable()
        .onFocusChanged { focusState ->
          if (!isFocused && focusState.hasFocus) {
            // Gaining focus, copy current value to our temporary focused value
            focusedValue = value.toString()
          }
          if (isFocused && !focusState.hasFocus) {
            // Losing focus, use current value or minimum if the input is empty
            onValueChanged(focusedValue.toIntOrNull() ?: range.first)
          }

          isFocused = focusState.hasFocus
        },
      value = if (isFocused) focusedValue else value.toString(),
      onValueChange = {
        val newValue = it.filter { char -> char.isDigit() }.toIntOrNull()
        focusedValue = newValue?.let {
          when {
            newValue > range.last -> range.last
            newValue < range.first -> range.first
            else -> newValue
          }.toString()
        } ?: "" // Input isn't an int, use empty text
      },
      textStyle = MaterialTheme.typography.headlineSmall.copy(
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface
      ),
      singleLine = true,
      keyboardOptions = KeyboardOptions(
        autoCorrectEnabled = false,
        keyboardType = KeyboardType.Number,
      ),
      decorationBox = { innerTextField ->
        Box(
          modifier = Modifier.padding(horizontal = 2.dp)
            .width(IntrinsicSize.Min),
          contentAlignment = Alignment.Center,
          content =  { innerTextField () }
        )
      }
    )

    IconButton(
      onClick = { onValueChanged(value + step) },
      enabled = range.contains(value + step)
    ) {
      Icon(
        imageVector = Icons.Default.Add,
        contentDescription = stringResource(R.string.step_control_increase),
        tint = MaterialTheme.colorScheme.primary
      )
    }
  }
}


@FrcKrawlerPreview
@Composable
private fun CounterMetricPreview() {
  FrcKrawlerTheme {
    Surface {
      StepControl(
        value = 100,
        onValueChanged = {}
      )
    }
  }
}