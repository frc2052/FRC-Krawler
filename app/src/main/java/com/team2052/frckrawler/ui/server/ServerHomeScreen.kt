package com.team2052.frckrawler.ui.server

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.model.Event
import com.team2052.frckrawler.ui.components.FRCKrawlerScaffold

@Composable
fun ServerHomeScreen(
    navController: NavController,
    event: Event
) = FRCKrawlerScaffold(modifier = Modifier, titleResourceId = R.string.mode_select_title) {

    //val event: Event = navController.previousBackStackEntry?.arguments?.getSerializable("event") as Event

    Text(text = event.name, color = Color.White, style = MaterialTheme.typography.h1)

}