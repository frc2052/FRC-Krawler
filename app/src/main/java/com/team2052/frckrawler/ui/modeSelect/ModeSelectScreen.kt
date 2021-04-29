package com.team2052.frckrawler.ui.modeSelect

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.components.ExpandableCardGroup
import com.team2052.frckrawler.ui.components.ExpandableCard
import com.team2052.frckrawler.ui.components.FinalDropdown
import com.team2052.frckrawler.ui.components.ExpandableCardConfiguration
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun ModeSelectScreen(modifier: Modifier = Modifier) = Column(
    modifier = modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
) {

    //val viewModel: ModeSelectViewModel by viewModel()

    var expandedCard by remember { mutableStateOf(-1) }

    CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.h6) {
        Text(text = stringResource(id = R.string.welcome_message))
        Text(text = stringResource(id = R.string.getting_started_message))
    }

    //Button(onClick = { viewModel.doSomething() }) { Text("CLICK ME!") }

    ExpandableCardGroup(
        modifier = Modifier.padding(top = 24.dp),
        content = listOf({ modifier, id ->
            ExpandableCard(
                modifier = modifier,
                config = createExpandableCardConfig(
                    titleResourceId = R.string.mode_remote_scout,
                    descriptionResourceId = R.string.mode_remote_scout_description,
                    continueMessageResourceId = R.string.mode_remote_scout_continue_message
                ),
                onExpand = { expanding -> expandedCard = if(expanding) id else -1 },
                expanded = expandedCard == id
            ) {
                var expanded by remember { mutableStateOf(false) }
                FinalDropdown(
                    onClick = { expanded = it },
                    expanded = expanded,
                    content = listOf(
                        { Text("Nexus 7") },
                        { Text("Sam's iPad") },
                        { Text("Bluetooth device 3") },
                        { Text("Bluetooth device 4") }
                    )
                )
            }
        }, { modifier, id ->
            ExpandableCard(
                modifier = modifier,
                config = createExpandableCardConfig(
                    titleResourceId = R.string.mode_remote_scout,
                    descriptionResourceId = R.string.mode_remote_scout_description,
                    continueMessageResourceId = R.string.mode_remote_scout_continue_message
                ),
                onExpand = { expanding -> expandedCard = if(expanding) id else -1 },
                expanded = expandedCard == id
            ) { }
        }, { modifier, id ->
            ExpandableCard(
                modifier = modifier,
                config = createExpandableCardConfig(
                    titleResourceId = R.string.mode_remote_scout,
                    descriptionResourceId = R.string.mode_remote_scout_description,
                    continueMessageResourceId = R.string.mode_remote_scout_continue_message
                ),
                onExpand = { expanding -> expandedCard = if(expanding) id else -1 },
                expanded = expandedCard == id
            ) { }
        })
    )
}

@Composable
private fun createExpandableCardConfig(
    titleResourceId: Int,
    descriptionResourceId: Int,
    continueMessageResourceId: Int
): ExpandableCardConfiguration {
    val title = stringResource(id = titleResourceId)
    val description = stringResource(id = descriptionResourceId)
    val continueMessage = stringResource(id = continueMessageResourceId)
    return ExpandableCardConfiguration(title, description, continueMessage)
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