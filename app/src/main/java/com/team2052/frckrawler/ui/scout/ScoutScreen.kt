package com.team2052.frckrawler.ui.scout

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.NavScreen
import com.team2052.frckrawler.ui.components.FRCKrawlerScaffold
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun ScoutScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
) = FRCKrawlerScaffold(
    modifier = modifier,
    currentScreen = NavScreen.ScoutScreen
) {
    Text("Scouting Screen!")
}

@Preview
@Composable
private fun ScoutScreenPreviewLight() {
    FrcKrawlerTheme(darkTheme = false) {
        ScoutScreen(navController = rememberNavController())
    }
}

@Preview
@Composable
private fun ScoutScreenPreviewDark() {
    FrcKrawlerTheme(darkTheme = true) {
        ScoutScreen(navController = rememberNavController())
    }
}