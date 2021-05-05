package com.team2052.frckrawler.ui

import android.app.Activity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.team2052.frckrawler.data.model.Event
import com.team2052.frckrawler.nbluetooth.BluetoothController
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.modeSelect.ModeSelectScreen
import com.team2052.frckrawler.ui.modeSelect.ModeSelectViewModel
import com.team2052.frckrawler.ui.modeSelect.ModeSelectViewModelFactory
import com.team2052.frckrawler.ui.server.ServerHomeScreen

@Composable
fun FRCKrawlerApp(modifier: Modifier = Modifier, activity: Activity, startDestination: String) {

    val navController = rememberNavController()

    val bluetoothController by remember { mutableStateOf(BluetoothController(activity)) }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("modeSelectScreen") {
            ModeSelectScreen(
                navController = navController,
                viewModel = viewModel(factory = ModeSelectViewModelFactory(bluetoothController = bluetoothController))
            )
        }
        composable("testScreen") { TestScreen() }
        composable(
            "serverHomeScreen",
            arguments = listOf(navArgument("event") { type = NavType.SerializableType(Event::class.java)})
        ) { backStackEntry ->
            ServerHomeScreen(
                navController = navController,
                event = navController.previousBackStackEntry?.arguments?.getSerializable("event") as Event
            )
        }
    }
}

@Composable
fun TestScreen() {
    Text(text = "TESTING", color = Color.White, style = MaterialTheme.typography.h1)
}