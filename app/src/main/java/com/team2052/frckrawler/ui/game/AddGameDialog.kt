package com.team2052.frckrawler.ui.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.material.AlertDialog
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.team2052.frckrawler.ui.components.fields.FRCKrawlerTextField
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun AddGameDialog(
  onAddGame: (String) -> Unit,
  onClose: () -> Unit
) {

//  shape: Shape = MaterialTheme.shapes.medium,
//  backgroundColor: Color = MaterialTheme.colors.surface,
//  contentColor: Color = contentColorFor(backgroundColor),
//  properties: DialogProperties = DialogProperties()
  var gameName by remember { mutableStateOf("") }
  Dialog(onDismissRequest = onClose) {
    Surface(
      shape = MaterialTheme.shapes.medium,
      color = MaterialTheme.colors.surface,
      contentColor = contentColorFor(MaterialTheme.colors.surface)
    ) {
      Column {
        Text(
          modifier = Modifier.padding(horizontal = 24.dp)
            .paddingFromBaseline(top = 40.dp),
          text = "Add New Game",
          style = MaterialTheme.typography.h6
        )

        Box (modifier = Modifier.padding(horizontal = 24.dp)) {
          FRCKrawlerTextField(
            value = gameName,
            onValueChange = { gameName = it },
            label = "Name"
          )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
          TextButton(
            modifier = Modifier.padding(12.dp),
            onClick = {
              onClose()
            }
          ) {
            Text("CANCEL")
          }
          TextButton(
            modifier = Modifier.padding(12.dp),
            onClick = {
              onAddGame(gameName)
              onClose()
            }
          ) {
            Text("SAVE")
          }
        }
      }
    }
  }
}

@Preview
@Composable
private fun AddGameDialogPreview() {
  FrcKrawlerTheme {
    AddGameDialog(
      onAddGame = {},
      onClose = {}
    )
  }
}