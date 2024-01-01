package com.team2052.frckrawler.ui.event.add

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.team2052.frckrawler.R
import com.team2052.frckrawler.data.remote.model.TbaSimpleEvent
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun AddEventSheetContent(
    gameId: Int,
    onClose: () -> Unit
) {
    val viewModel: AddEventViewModel = hiltViewModel()

    AddEventSheetLayout()
}

@Composable
private fun AddEventSheetLayout() {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Column {
        AddEventTabs(
            selectedTabIndex = selectedTabIndex,
            onTabSelected = { selectedTabIndex = it }
        )
        Divider(color = MaterialTheme.colors.primary)

        when(selectedTabIndex) {
            0 -> AutoEventEntry()
            1 -> ManualEventEntry()
        }
    }
}

@Composable
private fun AddEventTabs(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.primary
    ) {
        Tab(
            modifier = Modifier.padding(vertical = 12.dp),
            selected = selectedTabIndex == 0,
            onClick = { onTabSelected(0) },

            ) {
            Text(
                text = stringResource(R.string.add_event_tab_auto).uppercase(),
            )
        }
        Tab(
            modifier = Modifier.padding(vertical = 12.dp),
            selected = selectedTabIndex == 1,
            onClick = { onTabSelected(1) },
            ) {
            Text(
                text = stringResource(R.string.add_event_tab_manual).uppercase(),
            )
        }
    }
}

@Composable
private fun AutoEventEntry(
    years: List<Int>,
    onYearSelected: (Int) -> Unit,
    events: List<TbaSimpleEvent>,
    onEventSelected: (TbaSimpleEvent) -> Unit,
) {

}

@Composable
private fun ManualEventEntry(

) {

}

@Preview
@Composable
private fun AddEventPreview() {
    FrcKrawlerTheme {
        Surface {
            AddEventSheetLayout()
        }
    }
}

@Preview
@Composable
private fun ManualEventEntryPreview() {
    FrcKrawlerTheme {
        Surface {
            ManualEventEntry()
        }
    }
}
