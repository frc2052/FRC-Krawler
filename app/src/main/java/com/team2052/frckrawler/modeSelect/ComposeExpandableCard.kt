package com.team2052.frckrawler.modeSelect

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.core.os.ConfigurationCompat

/**
 * Optional container to automatically fill a lazy column with expandable cards
 */
@Composable
fun ExpandableCardGroup(
    content: () -> List<@Composable () -> Unit>
) {
    LazyColumn(
        modifier = Modifier.padding(12.dp)
    ) {
        items(content()) { expandableCard ->
            expandableCard()
        }
    }
}

@Composable
fun ExpandableCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    continueMessage: String = "start",
    expanded: Boolean = false,
    onExpand: (Boolean) -> Unit, // OnClick handler decides how to update the expanded variable
    onContinue: () -> Unit, // OnClick handler decides how to update the card when the continue button is clicked
    content: @Composable ColumnScope.() -> Unit
) = Card(
    modifier = modifier.padding(12.dp),
    shape = RoundedCornerShape(4.dp),
    backgroundColor = MaterialTheme.colors.surface,
    elevation = 1.dp
) {
    Column(modifier = Modifier.padding(24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = title, style = MaterialTheme.typography.h6)
                Text(text = description, style = MaterialTheme.typography.subtitle1)
            }
            IconButton(modifier = Modifier.size(48.dp), onClick = { onExpand(!expanded) }) {
                Icon(
                    modifier = modifier.size(40.dp),
                    imageVector = if(expanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Expand Card"
                )
            }
        }
        if(expanded) {
            Row (modifier = Modifier.fillMaxWidth().padding(0.dp, 32.dp)) {
                Column(content = content)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { onContinue() }) {
                    Text(
                        text = continueMessage.toUpperCase(ConfigurationCompat.getLocales(LocalConfiguration.current)[0]),
                        color = MaterialTheme.colors.secondary
                    )
                }
            }
        }
    }
}