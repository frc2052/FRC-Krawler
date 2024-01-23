package com.team2052.frckrawler.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.FabPosition
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.theme.darkGray
import com.team2052.frckrawler.ui.theme.lightGray
import com.team2052.frckrawler.ui.theme.spaceExtraLarge

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FRCKrawlerScaffold(
  modifier: Modifier = Modifier,
  scaffoldState: ScaffoldState = rememberScaffoldState(),
  appBar: @Composable () -> Unit = { },
  floatingActionButton: @Composable () -> Unit = { },
  background: @Composable () -> Unit = {
    Image(
      modifier = Modifier
        .fillMaxSize()
        .padding(spaceExtraLarge),
      painter = painterResource(R.drawable.ic_logo),
      contentDescription = stringResource(R.string.cd_background_logo),
      colorFilter = ColorFilter.tint(
        if (MaterialTheme.colors.isLight) {
          darkGray.copy(alpha = 0.05f)
        } else {
          lightGray.copy(alpha = 0.05f)
        }
      ),
    )
  },
  content: @Composable ColumnScope.() -> Unit,
) = Scaffold(
  modifier = modifier,
  scaffoldState = scaffoldState,
  topBar = {
    Column {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(MaterialTheme.colors.primaryVariant)
          .statusBarsPadding()
      )
      appBar()
    }
  },
  snackbarHost = { scaffoldState.snackbarHostState },
  floatingActionButton = floatingActionButton,
  floatingActionButtonPosition = FabPosition.End,
  backgroundColor = MaterialTheme.colors.background,
) { contentPadding ->
  Box(
    modifier = Modifier
      .fillMaxSize()
      .imePadding()
      .padding(contentPadding)
      .consumeWindowInsets(contentPadding)
  ) {
    background()
    Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      content()
    }
    FRCKrawlerSnackbar(
      snackbarHostState = scaffoldState.snackbarHostState,
      modifier = Modifier.align(Alignment.BottomCenter),
      onDismiss = {
        scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
      },
    )
  }
}