package com.team2052.frckrawler.ui.export

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.local.Event
import com.team2052.frckrawler.data.local.Game
import com.team2052.frckrawler.ui.components.FRCKrawlerAppBar
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun ExportDataScreen(
  gameId: Int,
  eventId: Int,
  navController: NavController,
  modifier: Modifier = Modifier,
) {
  val viewModel: ExportViewModel = hiltViewModel()

  LaunchedEffect(gameId, eventId) {
    viewModel.loadGameAndEvent(gameId, eventId)
  }

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
    val includeTeamNames by viewModel.includeTeamNames.collectAsState(true)
    val includeMatchMetrics by viewModel.includeMatchMetrics.collectAsState(true)
    val includePitMetrics by viewModel.includePitMetrics.collectAsState(true)

    ExportScreenContent(
      game = viewModel.game,
      event = viewModel.event,
      onExportSummary = {},
      onExportRaw = {},
      includeTeamNames = includeTeamNames,
      onIncludeTeamNamesChanged = viewModel::setIncludeTeamNames,
      includeMatchMetrics = includeMatchMetrics,
      onIncludeMatchMetricsChanged = viewModel::setIncludeMatchMetrics,
      includePitMetrics = includePitMetrics,
      onIncludePitMetricsChanged = viewModel::setIncludePitMetrics,
      modifier = Modifier.fillMaxSize(),
      contentPadding = contentPadding
    )
  }
}

@Composable
private fun ExportScreenContent(
  game: Game?,
  event: Event?,
  includeTeamNames: Boolean,
  onIncludeTeamNamesChanged: (Boolean) -> Unit,
  includeMatchMetrics: Boolean,
  onIncludeMatchMetricsChanged: (Boolean) -> Unit,
  includePitMetrics: Boolean,
  onIncludePitMetricsChanged: (Boolean) -> Unit,
  onExportSummary: () -> Unit,
  onExportRaw: () -> Unit,
  modifier: Modifier = Modifier,
  contentPadding: PaddingValues = PaddingValues(0.dp)
) {
  Column(
    modifier = modifier
      .verticalScroll(rememberScrollState())
      .padding(contentPadding)
      .consumeWindowInsets(contentPadding)
  ) {
    Surface(
      color = MaterialTheme.colorScheme.surfaceContainer,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Text(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        text = if (game != null && event != null) {
          "${game.name} - ${event.name}"
        } else ""
      )
    }
    Spacer(Modifier.height(4.dp))
    SectionHeader(stringResource(R.string.export_section_label))
    ExportAction(
      title = stringResource(R.string.export_summary_label),
      description = stringResource(R.string.export_summary_description),
      onClick = onExportSummary,
    )
    ExportAction(
      title = stringResource(R.string.export_raw_label),
      description = stringResource(R.string.export_raw_description),
      onClick = onExportRaw,
    )
    HorizontalDivider()
    SectionHeader(stringResource(R.string.export_config_section_label))
    ExportToggleOption(
      title = stringResource(R.string.export_include_names),
      toggled = includeTeamNames,
      onToggle = onIncludeTeamNamesChanged
    )
    ExportToggleOption(
      title = stringResource(R.string.export_include_match_metrics),
      toggled = includeMatchMetrics,
      onToggle = onIncludeMatchMetricsChanged
    )
    ExportToggleOption(
      title = stringResource(R.string.export_include_pit_metrics),
      toggled = includePitMetrics,
      onToggle = onIncludePitMetricsChanged
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
      ExportScreenContent(
        game = Game(
          name = "Infinite Recharge"
        ),
        event = Event(
          name = "Nothern Lights Regional",
          gameId = 1,
        ),
        includeTeamNames = true,
        onIncludeTeamNamesChanged = {},
        includeMatchMetrics = true,
        onIncludeMatchMetricsChanged = {},
        includePitMetrics = true,
        onIncludePitMetricsChanged = {},
        onExportSummary = {},
        onExportRaw = {}
      )
    }
  }
}