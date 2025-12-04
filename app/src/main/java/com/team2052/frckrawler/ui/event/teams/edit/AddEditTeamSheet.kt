package com.team2052.frckrawler.ui.event.teams.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.local.TeamAtEvent
import dev.zacsweers.metrox.viewmodel.metroViewModel
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import com.team2052.frckrawler.ui.components.fields.FRCKrawlerTextField
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun AddEditTeamSheet(
  eventId: Int,
  team: TeamAtEvent?,
  onClose: () -> Unit,
) {
  val viewModel: AddEditTeamViewModel = metroViewModel()

  var teamNumber: String? by remember { mutableStateOf(team?.number) }
  var teamName: String by remember { mutableStateOf(team?.name ?: "") }

  Column {
    Text(
      modifier = Modifier.padding(horizontal = 16.dp),
      text = if (team != null) {
        stringResource(R.string.edit_team_sheet_title)
      } else {
        stringResource(R.string.add_team_sheet_title)
      },
      style = MaterialTheme.typography.titleLarge
    )

    Spacer(Modifier.height(12.dp))

    AddEditTeamSheetContent(
      teamNumber = teamNumber,
      onTeamNumberChanged = { teamNumber = it },
      teamName = teamName,
      onTeamNameChanged = { teamName = it }
    )

    ActionButtons(
      saveEnabled = teamNumber != null,
      onSaveClicked = {
        teamNumber?.let { number ->
          viewModel.saveTeam(
            eventId = eventId,
            teamNumber = number,
            teamName = teamName
          )
        }
        onClose()
      },
      onCancelClicked = onClose,
      showDelete = team != null,
      onDeleteClicked = {
        team?.let { viewModel.deleteTeam(it) }
        onClose()
      }
    )
  }
}

@Composable
private fun AddEditTeamSheetContent(
  teamNumber: String?,
  onTeamNumberChanged: (String?) -> Unit,
  teamName: String,
  onTeamNameChanged: (String) -> Unit,
) {
  var hasNumberHadFocus by remember { mutableStateOf(false) }
  Column(
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
  ) {
    FRCKrawlerTextField(
      value = teamNumber ?: "",
      onValueChange = { newText ->
        val newNumber = newText.filter { it.isDigit() }
        onTeamNumberChanged(newNumber)
      },
      isError = { hasNumberHadFocus && teamNumber == null },
      onFocusChange = { focused -> if (!hasNumberHadFocus && focused) hasNumberHadFocus = true },
      keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Number
      ),
      label = stringResource(R.string.add_team_number_label)
    )
    Spacer(Modifier.height(16.dp))
    FRCKrawlerTextField(
      value = teamName,
      onValueChange = onTeamNameChanged,
      label = stringResource(R.string.add_team_name_label)
    )
  }

}

@Composable
private fun ActionButtons(
  saveEnabled: Boolean,
  onSaveClicked: () -> Unit,
  onCancelClicked: () -> Unit,
  showDelete: Boolean,
  onDeleteClicked: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    if (showDelete) {
      TextButton(
        modifier = Modifier.padding(12.dp),
        onClick = onDeleteClicked,
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
        onClick = onCancelClicked
      ) {
        Text(stringResource(R.string.cancel).uppercase())
      }
      TextButton(
        modifier = Modifier.padding(12.dp),
        enabled = saveEnabled,
        onClick = onSaveClicked
      ) {
        Text(stringResource(R.string.save).uppercase())
      }
    }
  }
}

@FrcKrawlerPreview
@Composable
private fun EditTeamPreview() {
  FrcKrawlerTheme {
    Surface {
      AddEditTeamSheetContent(
        teamNumber = "2052",
        onTeamNumberChanged = {},
        teamName = "KnightKrawler",
        onTeamNameChanged = {}
      )
    }
  }
}