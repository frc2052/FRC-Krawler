package com.team2052.frckrawler.ui.analyze

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.components.FRCKrawlerAppBar
import com.team2052.frckrawler.ui.navigation.Screen
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun AnalyzeDataScreen(
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
          Text(stringResource(R.string.analyze_screen_title))
        },
        actions = {
          IconButton(
            onClick = {
              navController.navigate(
                Screen.Export(
                  gameId = gameId,
                  eventId = eventId
                ).route
              )
            }
          ) {
            Icon(
              imageVector = Icons.Filled.Download,
              contentDescription = stringResource(R.string.analyze_export_label)
            )
          }
        }
      )
    },
  ) { contentPadding ->
    AnalyzeScreenContent(
      modifier = Modifier.fillMaxSize(),
      contentPadding = contentPadding
    )
  }
}

@Composable
private fun AnalyzeScreenContent(
  modifier: Modifier = Modifier,
  contentPadding: PaddingValues = PaddingValues(0.dp)
) {
  Column(
    modifier = modifier
      .verticalScroll(rememberScrollState())
      .padding(contentPadding)
      .consumeWindowInsets(contentPadding)
  ) {
    Text("Analyze Data")
  }
}

@Preview
@Composable
private fun AnalyzeScreenPreview() {
  FrcKrawlerTheme {
    Surface {
      AnalyzeScreenContent()
    }
  }
}