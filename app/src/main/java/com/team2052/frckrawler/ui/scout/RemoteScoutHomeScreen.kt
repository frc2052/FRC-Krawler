package com.team2052.frckrawler.ui.scout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Link
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.team2052.frckrawler.ui.components.*
import com.team2052.frckrawler.ui.components._new.Dropdown
import com.team2052.frckrawler.ui.components._new.InputBlock
import com.team2052.frckrawler.ui.components._new.TableExample
import com.team2052.frckrawler.ui.components.fields.FRCKrawlerDropdown
import com.team2052.frckrawler.ui.components.refactored.SwipeListTest
import com.team2052.frckrawler.ui.navigation.Screen
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun RemoteScoutHomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val viewModel: ScoutViewModel = hiltViewModel()
    val scaffoldState = rememberScaffoldState()

    FRCKrawlerScaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        scrollable = false,
        appBar = {
            FRCKrawlerAppBar(
                navController = navController,
                scaffoldState = scaffoldState,
                title = { Text(Screen.RemoteScout.title) }
            )
        },
        drawerContent = {
            FRCKrawlerDrawer()
        },
    ) { contentPadding ->


        com.team2052.frckrawler.ui.components._new.Card(
            modifier = modifier.padding(contentPadding),
            header = {
                com.team2052.frckrawler.ui.components._new.CardHeader(
                    title = { Text("Server") },
                ) {
                    com.team2052.frckrawler.ui.components._new.SolidTextButton(
                        text = { Text("CONNECT") },
                        onClick = { /*TODO*/ },
                    )
                }
            }
        )

        com.team2052.frckrawler.ui.components._new.Card(
            modifier = modifier.padding(contentPadding),
            header = {
                com.team2052.frckrawler.ui.components._new.CardHeader(
                    title = { Text("Server Controls") },
                    description = { Text("Server control and configurations") },
                ) {
                    Text("Server Running:")
                    Switch(checked = true, onCheckedChange = {}, colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colors.secondary))
                }
            },
            content = {
                InputBlock {
                    Dropdown(label = "Hello1") {
                        dropdownItem("Item1 is the best item, and all the other items aren't as good!")
                        dropdownItem("Item2")
                        dropdownItem("Item3")
                    }
                    Dropdown(label = "Hello2", enabled = false) {
                        dropdownItem("Item1")
                        dropdownItem("Item2")
                        dropdownItem("Item3")
                    }
                    Dropdown(label = "Hello3", enabled = false) {
                        dropdownItem("Item1")
                        dropdownItem("Item2")
                        dropdownItem("Item3")
                    }
                }
            },
            actions = {
                Row(modifier = Modifier.fillMaxWidth()) {
                    com.team2052.frckrawler.ui.components._new.OutlineTextButton(onClick = { /*TODO*/ }) {
                        Text(text = "VIEW LOGS")
                    }
                }
            },
        )

        com.team2052.frckrawler.ui.components._new.Card(
            modifier = modifier.padding(contentPadding),
            header = {
                com.team2052.frckrawler.ui.components._new.CardHeader(
                    title = { Text("Connected Scouts") },
                    description = { Text("Scouts Connected: 1") },
                ) {
                    com.team2052.frckrawler.ui.components._new.SolidTextButton(
                        onClick = { /*TODO*/ },
                        icon = {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                        }
                    ) {
                        Text(text = "ADD")
                    }
                }
            },
            content = {
                TableExample()
            }
        )
    }
}

@Composable
fun RemoteScoutHomeScreenContent(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    var connected by remember { mutableStateOf(false) }

    Card(
        modifier = modifier,
        header = {
            CardHeader(title = { Text("Events") })
        }
    ) {
        SwipeListTest()
    }

    Card(
        modifier = modifier,
        header = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                CardHeader(
                    title = { Text(modifier = Modifier.background(Color.Yellow), text = "Server") },
                    description = if (connected) { { Text(text = "team2052 server") } } else null,
                ) {
                    if (connected) {
                        OutlineTextButton(
                            onClick = { connected = false },
                            text = "DISCONNECT"
                        )
                        SolidTextButton(
                            onClick = { /*TODO*/ },
                            // Enabled when the scout has any data that hasn't already been synced
                            enabled = false,
                            icon = {
                                Icon(
                                    modifier = Modifier.padding(end = ButtonDefaults.ContentPadding.calculateEndPadding(
                                        LayoutDirection.Ltr) / 2
                                    ),
                                    imageVector = Icons.Default.Cached,
                                    contentDescription = "connect to server",
                                )
                            },
                            text = "sync"
                        )
                    } else {
                        SolidTextButton(
                            onClick = { connected = true },
                            icon = {
                                Icon(
                                    modifier = Modifier.padding(end = ButtonDefaults.ContentPadding.calculateEndPadding(
                                        LayoutDirection.Ltr) / 2
                                    ),
                                    imageVector = Icons.Default.Link,
                                    contentDescription = "connect to server",
                                )
                            },
                            text = "connect"
                        )
                    }
                }
            }
        }
    )
    Card(
        modifier = modifier,
        header = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                CardHeader(
                    modifier = Modifier.weight(0.5f),
                    title = { Text(text = "Match Scout") },
                    description = { Text(text = "Connect with server to scout") }
                ) {
                    SolidTextButton(
                        onClick = { /*TODO*/ },
                        enabled = connected,
                        icon = {
                            Icon(
                                modifier = Modifier.padding(end = ButtonDefaults.ContentPadding.calculateEndPadding(
                                    LayoutDirection.Ltr) / 2
                                ),
                                imageVector = Icons.Default.Add,
                                contentDescription = "add match",
                            )
                        },
                        text = "ADD"
                    )
                }
            }
        }
    )
}

@Preview
@Composable
private fun RemoteScoutScreenPreviewLight() {
    FrcKrawlerTheme(darkTheme = false) {
        RemoteScoutHomeScreen(navController = rememberNavController())
    }
}

@Preview
@Composable
private fun RemoteScoutScreenPreviewDark() {
    FrcKrawlerTheme(darkTheme = true) {
        RemoteScoutHomeScreen(navController = rememberNavController())
    }
}