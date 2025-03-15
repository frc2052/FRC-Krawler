package com.team2052.frckrawler.ui.export

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.components.FRCKrawlerAppBar
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun ExportDataScreen(
  gameId: Int,
  eventId: Int,
  navController: NavController,
  modifier: Modifier = Modifier,
) {

  Scaffold(
    modifier = modifier,
    topBar = {
      FRCKrawlerAppBar(
        navController = navController,
        title = {
          Text(stringResource(R.string.export_screen_title))
        }
      )
    },
  ) { contentPadding ->
    ExportScreenContent(
      modifier = Modifier.fillMaxSize(),
      contentPadding = contentPadding
    )
  }
}

@Composable
private fun ExportScreenContent(
  modifier: Modifier = Modifier,
  contentPadding: PaddingValues = PaddingValues(0.dp)
) {
  Column(
    modifier = modifier
      .verticalScroll(rememberScrollState())
      .padding(contentPadding)
      .consumeWindowInsets(contentPadding)
  ) {
    SectionHeader(stringResource(R.string.export_section_label))
    ExportAction(
      title = stringResource(R.string.export_summary_label),
      description = stringResource(R.string.export_summary_description),
      onClick = { }
    )
    ExportAction(
      title = stringResource(R.string.export_raw_label),
      description = stringResource(R.string.export_raw_description),
      onClick = { }
    )
    HorizontalDivider()
    SectionHeader(stringResource(R.string.export_config_section_label))
    ExportToggleOption(
      title = stringResource(R.string.export_include_names),
      toggled = true,
      onToggle = {}
    )
    ExportToggleOption(
      title = stringResource(R.string.export_include_match_metrics),
      toggled = true,
      onToggle = {}
    )
    ExportToggleOption(
      title = stringResource(R.string.export_include_pit_metrics),
      toggled = true,
      onToggle = {}
    )
  }
}

@Composable
private fun SectionHeader(
  title: String
) {
  Text(
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    text = title,
    style = MaterialTheme.typography.labelLarge,
  )
}

@Composable
private fun ExportAction(
  title: String,
  description: String,
  onClick: () -> Unit,
) {
  Column(
    modifier = Modifier
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .clickable(onClick = onClick)
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.titleSmall
    )
    Text(
      text = description,
      style = MaterialTheme.typography.bodyMedium
    )
  }
}

@Composable
private fun ExportToggleOption(
  title: String,
  toggled: Boolean,
  onToggle: (Boolean) -> Unit,
) {
  Row(
    modifier = Modifier
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .fillMaxWidth()
      .clickable(onClick = { onToggle(!toggled) }),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.bodyMedium
    )

    Switch(
      checked = toggled,
      onCheckedChange = onToggle
    )
  }
}

@Preview
@Composable
private fun ExportScreenPreview() {
  FrcKrawlerTheme {
    Surface {
      ExportScreenContent()
    }
  }
}