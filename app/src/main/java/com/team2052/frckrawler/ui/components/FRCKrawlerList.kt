package com.team2052.frckrawler.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FRCKrawlerList(
    listBuilder: FRCKrawlerListBuilder.() -> Unit,
) {
    val listBuilder = remember {
        FRCKrawlerListBuilder().apply(listBuilder)
    }
    val list = remember {
        listBuilder.build()
    }

    list.forEachIndexed { index, item ->
        if (index != 0) {
            Divider(Modifier.padding(8.dp))
        }

        Row(modifier = Modifier.padding(8.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                ProvideTextStyle(MaterialTheme.typography.subtitle1) {
                    Row(verticalAlignment = Alignment.CenterVertically, content = item.title)
                }
                CompositionLocalProvider(
                    LocalContentAlpha provides ContentAlpha.medium,
                    LocalTextStyle provides MaterialTheme.typography.body2,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, content = item.description)
                }
            }
            Icon(imageVector = Icons.Filled.DragHandle, contentDescription = "Drag Handle")
        }
    }
}

data class ListItem(val title: @Composable RowScope.() -> Unit, val description: @Composable RowScope.() -> Unit)

class FRCKrawlerListBuilder {
    val list: MutableList<ListItem> = mutableListOf()

    fun item(title: @Composable RowScope.() -> Unit, description: @Composable RowScope.() -> Unit) {
        list += ListItem(title, description)
    }

    fun build(): List<ListItem> {
        return list.toList()
    }
}