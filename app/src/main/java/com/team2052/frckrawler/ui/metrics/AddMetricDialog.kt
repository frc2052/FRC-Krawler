package com.team2052.frckrawler.ui.metrics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Maximize
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Minimize
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.team2052.frckrawler.data.local.MetricCategory
import com.team2052.frckrawler.data.local.MetricType
import com.team2052.frckrawler.ui.components.fields.FRCKrawlerTextField

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddMetricDialog(
    category: MetricCategory,
    gameId: Int,
    onClose: () -> Unit
) {
    val viewModel: AddMetricViewModel = hiltViewModel()

    var metricName by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val options = MetricType.values()
    var selectedMetricType : MetricType? by remember { mutableStateOf(null) }
    var radioButtonState by remember { mutableStateOf(true) }
    val radioOptions = listOf("Yes", "No")
    var numberText by rememberSaveable { mutableStateOf("") }
    var sliderPosition by remember { mutableStateOf(0f) }
    var sliderText by rememberSaveable { mutableStateOf("") }

    val isStateValid by remember {
        derivedStateOf {
            selectedMetricType != null && metricName.isNotBlank()
        }
    }

    Column(
        modifier = Modifier.padding(24.dp)
    ) {
        when (selectedMetricType) {
            MetricType.Boolean -> {
                Column(
                    Modifier.selectableGroup()
                ) {
                    radioOptions.forEach { text ->
                        Row(
                        ) {
                            RadioButton(
                                selected = radioButtonState,
                                onClick = { radioButtonState = true }
                            )
                            RadioButton(
                                selected = !radioButtonState,
                                onClick = { radioButtonState = false }
                            )
                        }
                    }
                }
            }
            MetricType.Counter -> {
                Column(
                    modifier = Modifier.padding(4.dp)
                ) {
                    Row() {
                        IconButton(
                            onClick = {}
                        ) {
                            Icon(Icons.Rounded.Minimize, contentDescription = "Minus")
                        }
                        TextField(
                            value = numberText,
                            onValueChange = { numberText = it },
                            label = { Text("Number") }
                        )
                        IconButton(
                            onClick = {}
                        ) {
                            Icon(Icons.Rounded.Maximize, contentDescription = "Plus")
                        }
                    }
                }
            }
            MetricType.Slider -> {
                Column(
                    modifier = Modifier.padding(4.dp)
                ) {
                    Row() {
                        TextField(
                            value = sliderText,
                            onValueChange = { sliderText = it },
                            label = { Text("Value") }
                        )
                        Slider(
                            value = sliderPosition,
                            onValueChange = {sliderPosition = it },
                            valueRange = 0f..100f,
                            steps = 5
                        )
                    }
                }
            }
            MetricType.Chooser -> {}
            MetricType.Checkbox -> {}
            MetricType.Stopwatch -> {}
            MetricType.TextField -> {}
            null -> {}
        }
        FRCKrawlerTextField(
            modifier = Modifier.padding(top = 24.dp),
            value = metricName,
            onValueChange = { metricName = it },
            label = "Metric name"
        )
        Row(
            horizontalArrangement = Arrangement.End
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {
                Button(
                    modifier = Modifier.padding(8.dp),
                    onClick = { expanded },
                ) {
                    val buttonText = selectedMetricType?.name ?: "Type "
                    Text(buttonText)
                    Icon(Icons.Rounded.Menu, contentDescription = "Open Menu")
                }
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    }
                ) {
                    options.forEach { typeField ->
                        DropdownMenuItem(
                            onClick = {
                                expanded = false
                                selectedMetricType = typeField
                            }
                        ) {
                            Text(text = typeField.name)
                        }
                    }
                }
            }
        }
        ProvideTextStyle(
            LocalTextStyle.current.copy(
                color = MaterialTheme.colors.primary
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    modifier = Modifier.padding(12.dp),
                    onClick = {
                        onClose()
                    }
                ) {
                    Text("CANCEL")
                }
                TextButton(
                    modifier = Modifier.padding(12.dp),
                    enabled = isStateValid,
                    onClick = {
                        viewModel.saveMetric(
                            name = metricName,
                            category = category,
                            gameId = gameId,
                            type = selectedMetricType ?: MetricType.Boolean
                        )
                        onClose()
                    }
                ) {
                    Text("SAVE")
                }
            }
        }
    }
}