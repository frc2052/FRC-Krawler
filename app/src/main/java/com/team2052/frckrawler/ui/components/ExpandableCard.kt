package com.team2052.frckrawler.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.R
import com.team2052.frckrawler.util.*
import java.util.*

@Composable
fun ExpandableCardGroup(
    modifier: Modifier = Modifier,
    content: List<@Composable (Modifier, Int) -> Unit>,
) {
    var internalCardCount = 0
    for(composable in content) { composable(modifier, internalCardCount++) }
}

@Composable
fun ExpandableCard(
    modifier: Modifier = Modifier,
    @StringRes titleResourceId: Int,
    @StringRes descriptionResourceId: Int,
    @StringRes continueResourceId: Int,
    expanded: Boolean = false,
    onExpand: (Boolean) -> Unit,
    onContinue: () -> Unit = { },
    content: @Composable ColumnScope.() -> Unit
) = Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(4.dp),
    elevation = LocalCardElevation.current
) {
    Column(modifier = Modifier.padding(24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = stringResource(titleResourceId).camelcase(), style = MaterialTheme.typography.h6)
                Text(text = stringResource(descriptionResourceId).camelcase(), style = MaterialTheme.typography.subtitle1)
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
                        text = stringResource(continueResourceId).toUpperCase(Locale.getDefault()),
                        color = MaterialTheme.colors.secondary
                    )
                }
            }
        }
    }
}