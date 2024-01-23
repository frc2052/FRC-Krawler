package com.team2052.frckrawler.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun FRCKrawlerAppBar(
  modifier: Modifier = Modifier,
  navController: NavController = rememberNavController(),
  navigation: @Composable () -> Unit = {
    DefaultNavigationButton(navController)
  },
  title: @Composable RowScope.() -> Unit,
  actions: @Composable RowScope.() -> Unit = { },
) = TopAppBar(
  modifier = modifier.zIndex(1f),
  backgroundColor = MaterialTheme.colors.primary,
  contentColor = MaterialTheme.colors.onPrimary,
  navigationIcon = navigation,
  actions = actions,
  title = {
    ProvideTextStyle(MaterialTheme.typography.h6) { Row { title() } }
  },
)

@Composable
private fun DefaultNavigationButton(
  navController: NavController
) {
  if (navController.previousBackStackEntry != null) {
    IconButton(onClick = { navController.popBackStack() }) {
      Icon(
        imageVector = Icons.Filled.ArrowBack,
        contentDescription = stringResource(R.string.navigate_up),
        tint = MaterialTheme.colors.onPrimary,
      )
    }
  }
}

@FrcKrawlerPreview
@Composable
private fun FRCKrawlerAppBarPreviewLight() {
  FrcKrawlerTheme(darkTheme = false) {
    FRCKrawlerAppBar(
      title = { Text("preview") },
    )
  }
}

@FrcKrawlerPreview
@Composable
private fun FRCKrawlerAppBarPreviewDark() {
  FrcKrawlerTheme(darkTheme = true) {
    FRCKrawlerAppBar(
      title = { Text("preview") },
      actions = {
        IconButton(
          onClick = { }
        ) {
          Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = stringResource(R.string.delete)
          )
        }
      },
    )
  }
}