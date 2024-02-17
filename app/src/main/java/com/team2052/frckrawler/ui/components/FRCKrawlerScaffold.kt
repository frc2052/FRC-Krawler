package com.team2052.frckrawler.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun FRCKrawlerScaffold(
  modifier: Modifier = Modifier,
  appBar: @Composable () -> Unit = { },
  snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
  floatingActionButton: @Composable () -> Unit = { },
  content: @Composable ColumnScope.() -> Unit,
) {
  Scaffold(
    modifier = modifier,
    topBar = {
      appBar()
    },
    snackbarHost = { SnackbarHost(snackbarHostState) },
    floatingActionButton = floatingActionButton,
    floatingActionButtonPosition = FabPosition.End,
  ) { contentPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .imePadding()
        .padding(contentPadding)
        .consumeWindowInsets(contentPadding)
    ) {
      Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        content()
      }
    }
  }
}