package com.team2052.frckrawler.ui.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import com.team2052.frckrawler.ui.components.fields.FRCKrawlerTextField
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun AddMetricSetDialog(
  onAddMetricSet: (String) -> Unit,
  onClose: () -> Unit
) {
  val focusRequester = remember { FocusRequester() }
  var setName by remember { mutableStateOf("") }
  Dialog(onDismissRequest = onClose) {
    LaunchedEffect(true) {
      focusRequester.requestFocus()
    }

    Surface(
      shape = MaterialTheme.shapes.medium,
      color = MaterialTheme.colorScheme.surface,
      contentColor = contentColorFor(MaterialTheme.colorScheme.surface)
    ) {
      Column {
        Text(
          modifier = Modifier
            .padding(horizontal = 24.dp)
            .paddingFromBaseline(top = 40.dp),
          text = stringResource(R.string.add_metric_set_title),
          style = MaterialTheme.typography.titleLarge
        )

        Box(modifier = Modifier.padding(horizontal = 24.dp)) {
          FRCKrawlerTextField(
            modifier = Modifier.focusRequester(focusRequester),
            value = setName,
            onValueChange = { setName = it },
            label = stringResource(R.string.add_metric_set_name)
          )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
          TextButton(
            modifier = Modifier.padding(12.dp),
            onClick = {
              onClose()
            }
          ) {
            Text(stringResource(R.string.cancel).uppercase())
          }
          TextButton(
            modifier = Modifier.padding(12.dp),
            onClick = {
              onAddMetricSet(setName)
              onClose()
            }
          ) {
            Text(stringResource(R.string.save).uppercase())
          }
        }
      }
    }
  }
}

@FrcKrawlerPreview
@Composable
private fun AddMetricSetDialogPreview() {
  FrcKrawlerTheme {
    AddMetricSetDialog(
      onAddMetricSet = {},
      onClose = {}
    )
  }
}