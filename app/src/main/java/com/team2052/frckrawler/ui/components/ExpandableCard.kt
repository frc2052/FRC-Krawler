package com.team2052.frckrawler.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.ConfigurationCompat
import com.team2052.frckrawler.R

@Composable
fun ExpandableCardGroup(
    modifier: Modifier = Modifier,
    content: List<@Composable (Modifier, Int) -> Unit>
) {
    var internalCardCount = 0
    for (expandableCard in content) { expandableCard(modifier, internalCardCount++) }
}

data class ExpandableCardConfiguration(
    val title: String,
    val description: String,
    val startMessage: String = "start"
)

@Composable
fun ExpandableCard(
    modifier: Modifier = Modifier,
    config: ExpandableCardConfiguration,
    expanded: Boolean = false,
    onExpand: (Boolean) -> Unit,
    onContinue: () -> Unit = { },
    content: @Composable ColumnScope.() -> Unit
) = Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(4.dp), elevation = 1.dp) {
    Column(modifier = Modifier.padding(24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = config.title, style = MaterialTheme.typography.h6)
                Text(text = config.description, style = MaterialTheme.typography.subtitle1)
            }
            IconButton(modifier = Modifier.size(48.dp), onClick = { onExpand(!expanded) }) {
                Icon(
                    modifier = Modifier.size(48.dp),
                    imageVector = when (expanded) {
                        true -> Icons.Filled.KeyboardArrowDown
                        false -> Icons.Filled.KeyboardArrowUp
                    },
                    contentDescription = stringResource(id = R.string.cd_expandable_card)
                )
            }
        }
        if (expanded) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 24.dp),
            ) { Column(content = content) }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { onContinue() }) {
                    Text(
                        text = config.startMessage.toUpperCase(ConfigurationCompat.getLocales(LocalConfiguration.current)[0]),
                        color = MaterialTheme.colors.secondary
                    )
                }
            }
        }
    }
}