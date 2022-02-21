package com.team2052.frckrawler.ui.server

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.components.*
import com.team2052.frckrawler.ui.navigation.Screen.*
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun ServerGamesScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val viewModel: ServerViewModel = hiltViewModel()
    val scaffoldState = rememberScaffoldState()

    var addGameDialogOpen by remember { mutableStateOf(false) }
    var fabExpanded by remember { mutableStateOf(false) }

    FRCKrawlerScaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        appBar = {
            FRCKrawlerAppBar(
                navController = navController,
                scaffoldState = scaffoldState,
                title = {
                    Text(stringResource(R.string.server_screen_title))
                }
            )
        },
        tabBar = {
            FRCKrawlerTabBar(navigation = Server, currentScreen = ServerGames) { screen ->
                navController.navigate(screen.route) {
                    popUpTo(ServerGames.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
        },
        floatingActionButton = {
            Column {
                if (fabExpanded) {
                    val fabModifier = Modifier.padding(bottom = 24.dp)
                    FloatingActionButton(
                        modifier = fabModifier,
                        onClick = { addGameDialogOpen = true }
                    ) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "Manual Season Add")
                    }
                    FloatingActionButton(
                        modifier = fabModifier,
                        onClick = {}
                    ) {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = "Manual Season Add")
                    }
                }
                FloatingActionButton(onClick = {
                    fabExpanded = !fabExpanded
                }) {
                    if (!fabExpanded) {
                        Icon(imageVector = Icons.Filled.Menu, contentDescription = "Manual Season Add")
                    } else {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Manual Season Add")
                    }
                }
            }
        },
        drawerContent = {
            FRCKrawlerDrawer()
        },
        background = {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    modifier = Modifier.size(128.dp),
                    imageVector = Icons.Filled.Analytics,
                    contentDescription = "Background",
                    tint = MaterialTheme.colors.secondary,
                )
                Text(text = "No Games", style = MaterialTheme.typography.h4)
            }
        }
    ) { contentPadding ->
        ServerSeasonsScreenContent(
            modifier = Modifier.padding(contentPadding),
            viewModel = viewModel,
            navController = navController,
        )
        if (addGameDialogOpen) {
            var dialogWidth by remember { mutableStateOf(0) }
            var gameName by remember { mutableStateOf("") }
//            AlertDialog(
//                modifier = Modifier.fillMaxWidth(0.5f),
//                onDismissRequest = { addGameDialogOpen = false },
//                title = { Text("Add New Game") },
//                text = { FRCKrawlerTextField(modifier = Modifier.padding(top = 24.dp), value = gameName, onValueChange = { gameName = it }, label = "Name") },
//                buttons = {
//                    ProvideTextStyle(LocalTextStyle.current.copy(color = MaterialTheme.colors.secondary)) {
//                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
//                            TextButton(modifier = Modifier.padding(12.dp), onClick = { /*TODO*/ }) {
//                                Text("CANCEL")
//                            }
//                            TextButton(modifier = Modifier.padding(12.dp), onClick = { /*TODO*/ }) {
//                                Text("SAVE")
//                            }
//                        }
//                    }
//                }
//            )
        }
    }
}

@Composable
fun ServerSeasonsScreenContent(
    modifier: Modifier = Modifier,
    viewModel: ServerViewModel,
    navController: NavController,
) {
    Column(modifier = Modifier.background(color = MaterialTheme.colors.background.copy(alpha = 0.5f))) {
        var highlighted by remember { mutableStateOf(false) }
        val txtMod = Modifier.padding(12.dp)

        val checkedStates = remember { mutableStateListOf(false, false, false, false, false) }
//        FRCKrawlerDataTable(
//            dataTableSource = TableSource(
//                TableRow({ Text("Year") }, { Text("Season") }, { Text("Metrics") }, checked = checkedStates[0]),
//                TableRow({ Text("2020") }, { Text("Infinite Recharge") }, { Text("true") }, checked = checkedStates[1]),
//                TableRow({ Text("2019") }, { Text("Power Up") }, { Text("false") }, checked = checkedStates[2]),
//                TableRow({ Text("2018") }, { Text("Rover Ruckus") }, { Text("false") }, checked = checkedStates[3]),
//                TableRow({ Text("2020") }, { Text("Infinite Recharge") }, { Text("true") }, checked = checkedStates[1]),
//                TableRow({ Text("2019") }, { Text("Power Up") }, { Text("false") }, checked = checkedStates[2]),
//                TableRow({ Text("2018") }, { Text("Rover Ruckus") }, { Text("false") }, checked = checkedStates[3]),
//                TableRow({ Text("2020") }, { Text("Infinite Recharge") }, { Text("true") }, checked = checkedStates[1]),
//                TableRow({ Text("2019") }, { Text("Power Up") }, { Text("false") }, checked = checkedStates[2]),
//                TableRow({ Text("2018") }, { Text("Rover Ruckus") }, { Text("false") }, checked = checkedStates[3]),
//                TableRow({ Text("2020") }, { Text("Infinite Recharge") }, { Text("true") }, checked = checkedStates[1]),
//                TableRow({ Text("2019") }, { Text("Power Up") }, { Text("false") }, checked = checkedStates[2]),
//                TableRow({ Text("2018") }, { Text("Rover Ruckus") }, { Text("false") }, checked = checkedStates[3]),
//            ),
//            onCheckedChange = { index, checked ->
//                checkedStates[index] = checked
//            }
//        )
    }
}

@Preview
@Composable
private fun ServerSeasonsScreenPreviewLight() {
    FrcKrawlerTheme(darkTheme = false) {
        ServerGamesScreen(navController = rememberNavController())
    }
}

@Preview
@Composable
private fun ServerSeasonsScreenPreviewDark() {
    FrcKrawlerTheme(darkTheme = true) {
        ServerGamesScreen(navController = rememberNavController())
    }
}