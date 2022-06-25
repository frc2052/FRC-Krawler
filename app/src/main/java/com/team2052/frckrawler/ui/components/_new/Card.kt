package com.team2052.frckrawler.ui.components._new

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.team2052.frckrawler.ui.theme.spaceLarge

@Composable
fun Example() {
    Card(
        header = {
            CardHeader(title = { Text("Hello") }, description = { Text("Description") }) { modifier ->
                Row(modifier) {
                    Text("Server Running:")
                    Switch(checked = true, onCheckedChange = {})
                }
            }
        },
        content = {

        },
        actions = {

        },
    )
}

@Composable
fun Card(
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit, // TODO: Direct call to CardHeader
    content: (@Composable ColumnScope.() -> Unit)? = null,
    actions: (@Composable RowScope.(Modifier) -> Unit)? = null,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
    ) {
        Column {
            header()
            if (content != null) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = spaceLarge)
                        .padding(bottom = if (actions == null) spaceLarge else 0.dp),
                ) { content() }
            }
            if (actions != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = spaceLarge,
                            top = if (content == null) 0.dp else spaceLarge,
                            end = spaceLarge,
                            bottom = spaceLarge
                        ),
                    horizontalArrangement = Arrangement.End,
                ) {
                    ProvideTextStyle(MaterialTheme.typography.button.copy(color = MaterialTheme.colors.primary)) {
                        actions(Modifier.padding(start = spaceLarge))
                    }
                }
            }
        }
    }
}

@Composable
fun CardHeader(
    modifier: Modifier = Modifier,
    title: (@Composable RowScope.() -> Unit),
    description: (@Composable RowScope.() -> Unit)? = null,
    controls: (@Composable RowScope.(Modifier) -> Unit)? = null,
) = Row(
    modifier = modifier
        .fillMaxWidth()
        .padding(spaceLarge),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
) {
    Column {
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Medium),
        ) { Row { title() } }
        AnimatedVisibility(visible = description != null) {
            description?.let { description ->
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.body2,
                    LocalContentAlpha provides ContentAlpha.medium,
                ) { Row { description() } }
            }
        }
    }
    AnimatedVisibility(visible = controls != null) {
        controls?.let { content ->
            CompositionLocalProvider(
                LocalTextStyle provides MaterialTheme.typography.body1,
                LocalContentAlpha provides ContentAlpha.medium,
            ) {
                Row(
                    modifier = Modifier.animateContentSize(animationSpec = tween(durationMillis = 200)),
                    verticalAlignment = Alignment.CenterVertically,
                ) { content(Modifier.padding(start = spaceLarge)) }
            }
        }
    }
}