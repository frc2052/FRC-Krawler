package com.team2052.frckrawler.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import com.team2052.frckrawler.ui.navigation.Screen
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import kotlin.collections.removeLastOrNull

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FRCKrawlerAppBar(
  modifier: Modifier = Modifier,
  backStack: NavBackStack<NavKey>,
  navigation: @Composable () -> Unit = {
    DefaultNavigationButton(backStack)
  },
  darkTheme: Boolean = isSystemInDarkTheme(),
  title: @Composable RowScope.() -> Unit,
  actions: @Composable RowScope.() -> Unit = { },
) = TopAppBar(
  modifier = modifier,
  colors = if (darkTheme) {
    TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.surfaceContainer,
      scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,)
  } else {
    TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.primary,
      scrolledContainerColor = MaterialTheme.colorScheme.primary,
      navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
      titleContentColor = MaterialTheme.colorScheme.onPrimary,
      actionIconContentColor = MaterialTheme.colorScheme.onPrimary
    )
 },
  navigationIcon = navigation,
  actions = actions,
  title = {
    ProvideTextStyle(MaterialTheme.typography.titleLarge) { Row { title() } }
  },
)

@Composable
private fun DefaultNavigationButton(
  backStack: NavBackStack<NavKey>
) {
  if (backStack.size > 1) {
    IconButton(onClick = { backStack.removeLastOrNull()}) {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = stringResource(R.string.navigate_up),
      )
    }
  }
}

@FrcKrawlerPreview
@Composable
private fun FRCKrawlerAppBarPreviewLight() {
  FrcKrawlerTheme {
    FRCKrawlerAppBar(
      title = { Text("preview") },
      backStack = rememberNavBackStack(Screen.ModeSelect),
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