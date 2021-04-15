package com.team2052.frckrawler.modeSelect

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun LabeledDropdown2(
    modifier: Modifier = Modifier,
    dropdownLabel: String,
    dropdownList: List<Any>
) {
    var dropdownOpen by remember { mutableStateOf(false) }
    var selectedValue by remember { mutableStateOf(dropdownList[0]) }

    Row {

    }
}

@Composable
fun Dropdown(
    modifier: Modifier = Modifier,
    dropdownList: List<Any>,
    defaultSelectedIndex: Int = 0
) {
    var dropdownOpen by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf(dropdownList[defaultSelectedIndex]) }

    Column(
        modifier = modifier
            .padding(12.dp)
            .clip(if (dropdownOpen) RoundedCornerShape(4.dp, 4.dp) else RoundedCornerShape(4.dp))
            .background(MaterialTheme.colors.surface)
            .clickable { dropdownOpen = true },
    ) {
        Row(
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
           Text(text = selectedItem.toString())
            Icon(imageVector = Icons.Filled.KeyboardArrowDown, contentDescription = "")
        }
        Column(
            modifier = Modifier.background(MaterialTheme.colors.onSurface)
        ) {
            dropdownList.forEach { item ->
                if(item != selectedItem) {
                    Text(
                        text = item.toString(),
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                dropdownOpen = false
                                selectedItem = item
                            }
                    )
                }
            }
        }
    }
    /*
    * modifier = modifier
            .padding(12.dp)
            .clip(if (dropdownOpen) RoundedCornerShape(4.dp, 4.dp) else RoundedCornerShape(4.dp))
            .background(Color.Red)
            .clickable { dropdownOpen = true },
    * */
//        Text(modifier = Modifier.padding(12.dp), text = selectedItem.toString())
//        DropdownMenu(expanded = dropdownOpen, onDismissRequest = { dropdownOpen = false }) {
//            dropdownList.forEach() { item ->
//                if(item != selectedItem) {
//                    DropdownMenuItem(
//                        modifier = Modifier.fillMaxWidth(),
//                        onClick = {
//                            dropdownOpen = false
//                            selectedItem = item
//                        }
//                    ) {
//
//                        Text(item.toString(), modifier = Modifier.wrapContentWidth())
//                    }
//                }
//            }
//        }
}

@Composable
fun LabeledDropdown(
    modifier: Modifier = Modifier,
    label: String,
    dropdownList: List<String>
) {
    var dropdownOpen by remember { mutableStateOf(false) }
    var selectedValue by remember { mutableStateOf(dropdownList[0]) }

    Row(
        modifier = modifier
            .clickable { dropdownOpen = dropdownList.size > 1 }
            .height(36.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "${label}: ", style = MaterialTheme.typography.body1)

            Column(modifier = Modifier.padding(8.dp, 0.dp)) {
                Text(text = selectedValue, style = MaterialTheme.typography.body2)
                DropdownMenu(
                    expanded = dropdownOpen,
                    onDismissRequest = { dropdownOpen = false },
                ) {
                    dropdownList.forEach { item ->
                        if(item != selectedValue) {
                            DropdownMenuItem(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    dropdownOpen = false
                                    selectedValue = item
                                }
                            ) {
                                Text(item, modifier = Modifier.wrapContentWidth())
                            }
                        }
                    }
                }

        }
        if(dropdownList.size > 1) {
            Icon(
                modifier = Modifier.height(24.dp),
                imageVector = if(dropdownOpen) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowDropUp,
                contentDescription = "Card expansion toggle"
            )
        }
    }
}