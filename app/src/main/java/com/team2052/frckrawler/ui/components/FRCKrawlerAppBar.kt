package com.team2052.frckrawler.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@OptIn(ExperimentalMaterial3Api::class)
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
  colors = TopAppBarDefaults.topAppBarColors(
    containerColor = MaterialTheme.colorScheme.primary,
    scrolledContainerColor = MaterialTheme.colorScheme.primary,
    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
    titleContentColor = MaterialTheme.colorScheme.onPrimary,
    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
  ),
  navigationIcon = navigation,
  actions = actions,
  title = {
    ProvideTextStyle(MaterialTheme.typography.titleLarge) { Row { title() } }
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