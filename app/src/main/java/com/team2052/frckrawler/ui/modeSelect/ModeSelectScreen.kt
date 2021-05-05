package com.team2052.frckrawler.ui.modeSelect

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.model.Event
import com.team2052.frckrawler.nbluetooth.BluetoothController
import com.team2052.frckrawler.ui.components.*
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import java.io.Serializable

@Composable
fun ModeSelectScreen(
    modifier: Modifier = Modifier,
    // Values are nullable to ensure they can be previewed
    viewModel: ModeSelectViewModel? = null,
    navController: NavController? = null,
) = FRCKrawlerScaffold(modifier = modifier, titleResourceId = R.string.mode_select_title) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.h6) {
            Text(text = stringResource(id = R.string.welcome_message))
            Text(text = stringResource(id = R.string.getting_started_message))
        }

        var expandedCardId by remember { mutableStateOf(-1) }
        ExpandableCardGroup(
            modifier = modifier.padding(top = 24.dp),
            content = listOf(
                { modifier, id ->
                    ExpandableCard(
                        modifier = modifier,
                        titleResourceId = R.string.mode_remote_scout,
                        descriptionResourceId = R.string.mode_remote_scout_description,
                        continueResourceId = R.string.mode_remote_scout_continue,
                        onExpand = { expanding -> expandedCardId = if(expanding) id else -1 },
                        expanded = expandedCardId == id,
                        onContinue = {
                            Log.d("TEST_NAV", "navBtnClikc")
                            navController?.navigate("testScreen")
                        }
                        ) { }
                }, { modifier, id ->
                    ExpandableCard(
                        modifier = modifier,
                        titleResourceId = R.string.mode_server,
                        descriptionResourceId = R.string.mode_server_description,
                        continueResourceId = R.string.mode_server_continue,
                        onExpand = { expanding -> expandedCardId = if(expanding) id else -1 },
                        expanded = expandedCardId == id,
                        onContinue = {
                            // TODO: without passing the value <id = 0L> into the constructor the init method is unrecognized
                            val event = Event(id = 0L, name = "NAME_TEST", game = "GAME_TEST")
                            // Push the arguments for the next composable onto the stack before navigating to it
                            // This fixes the problem of not being able to pass objects into the nav controller url
                            navController?.currentBackStackEntry?.arguments?.putSerializable("event", event)
                            navController?.navigate("serverHomeScreen")}
                    ) { }
                }, { modifier, id ->
                    ExpandableCard(
                        modifier = modifier,
                        titleResourceId = R.string.mode_solo_scout,
                        descriptionResourceId = R.string.mode_solo_scout_description,
                        continueResourceId = R.string.mode_solo_scout_continue,
                        onExpand = { expanding -> expandedCardId = if(expanding) id else -1 },
                        expanded = expandedCardId == id
                    ) { }
                }
            )
        )
    }
}

@Preview
@Composable
fun ModeSelectorPreviewLight() {
    FrcKrawlerTheme(darkTheme = false) {
        ModeSelectScreen()
    }
}

@Preview
@Composable
fun ModeSelectorPreviewDark() {
    FrcKrawlerTheme(darkTheme = true) {
        ModeSelectScreen()
    }
}