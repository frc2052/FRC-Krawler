package com.team2052.frckrawler.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme

@Composable
fun StepControl(
    value: Int,
    onValueChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    step: Int = 1,
    range: IntRange = Int.MIN_VALUE..Int.MAX_VALUE
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { onValueChanged(value - step) },
            enabled = range.contains(value - step)
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = stringResource(R.string.step_control_decrease),
                tint = MaterialTheme.colors.primary
            )
        }

        Spacer(Modifier.width(8.dp))

        Text(
            text = value.toString(),
            style = MaterialTheme.typography.h5
        )

        Spacer(Modifier.width(8.dp))

        IconButton(
            onClick = { onValueChanged(value + range.step) },
            enabled = range.contains(value + range.step)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.step_control_increase),
                tint = MaterialTheme.colors.primary
            )
        }
    }
}


@FrcKrawlerPreview
@Composable
private fun CounterMetricPreview() {
    FrcKrawlerTheme {
        Surface {
            StepControl(
                value = 5,
                onValueChanged = {}
            )
        }
    }
}