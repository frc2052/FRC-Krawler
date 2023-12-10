package com.team2052.frckrawler.ui.metrics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.data.model.Metric

@Composable
fun MetricInput(
    modifier: Modifier = Modifier,
    metric: Metric,
    state: String,
    onStateChanged: (String) -> Unit
) {
    Row(
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = metric.name,
            style = MaterialTheme.typography.h5
        )
        
        when (metric) {
            is Metric.BooleanMetric -> {
                BooleanMetric(state = state, onStateChanged = onStateChanged)
            }
            is Metric.CounterMetric -> {
                Column(
                    modifier = Modifier.padding(4.dp)
                ) {
                    Row() {
//                    IconButton(
//                        onClick = {}
//                    ) {
//                        Icon(Icons.Rounded.Minimize, contentDescription = "Minus")
//                    }
//                    TextField(
//                        value = numberText,
//                        onValueChange = { numberText = it },
//                        label = { Text("Number") }
//                    )
//                    IconButton(
//                        onClick = {}
//                    ) {
//                        Icon(Icons.Rounded.Maximize, contentDescription = "Plus")
//                    }
                    }
                }
            }
            is Metric.SliderMetric -> {
                Column(
                    modifier = Modifier.padding(4.dp)
                ) {
                    Row() {
//                    TextField(
//                        value = sliderText,
//                        onValueChange = { sliderText = it },
//                        label = { Text("Value") }
//                    )
//                    Slider(
//                        value = sliderPosition,
//                        onValueChange = {sliderPosition = it },
//                        valueRange = 0f..100f,
//                        steps = 5
//                    )
                    }
                }
            }

            is Metric.CheckboxMetric -> TODO()
            is Metric.ChooserMetric -> TODO()
            is Metric.StopwatchMetric -> TODO()
            is Metric.TextFieldMetric -> TODO()
        }
    }
}