package com.team2052.frckrawler.base

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp

@Composable
fun LabeledDropdown(
    modifier: Modifier,
    label: String,
    content: @Composable () -> Unit
) = Row(verticalAlignment = Alignment.CenterVertically) {
    Text(modifier = Modifier.sizeIn(minWidth = 128.dp), text = "${label}:")
    content()
}

@Composable
fun Dropdown(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(16.dp),
    defaultSelectedIndex: Int = 0,
    dropdownList: List<String>
) = Column {
    var selectedIndex by remember { mutableStateOf(if(defaultSelectedIndex in dropdownList.indices) defaultSelectedIndex else dropdownList.size - 1) }
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .sizeIn(minWidth = 256.dp)
            .graphicsLayer { shadowElevation = if (expanded) 8f else 0f }
            .clickable { expanded = true }
            .clip(RoundedCornerShape(4.dp))
            .background(if(expanded) MaterialTheme.colors.surface else Color.Transparent)
            .padding(paddingValues),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = dropdownList[selectedIndex])
        Spacer(modifier = Modifier.width(32.dp))
        Icon(
            imageVector = if (expanded) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowDropUp,
            contentDescription = "Dropdown Expand Toggle"
        )
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        dropdownList.forEachIndexed { index, s ->
            DropdownMenuItem(
                modifier = Modifier.sizeIn(), // sets size constraints to undefined
                contentPadding = paddingValues,
                onClick = {
                    if(selectedIndex == index) {
                        expanded = false
                    } else {
                        selectedIndex = index
                    }
                }
            ) { Text(text = s, softWrap = false) }
        }
    }
}