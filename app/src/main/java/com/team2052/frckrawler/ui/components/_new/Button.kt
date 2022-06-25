package com.team2052.frckrawler.ui.components._new

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.ui.theme.spaceMedium
import com.team2052.frckrawler.ui.theme.spaceSmall

private val buttonPadding = PaddingValues(horizontal = spaceMedium, vertical = spaceSmall)

@Composable
fun SolidTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable () -> Unit = { },
    text: (@Composable () -> Unit)? = null,
) = Button(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    colors = ButtonDefaults.buttonColors(
        backgroundColor = MaterialTheme.colors.secondary.copy(alpha = ContentAlpha.high),
        contentColor = MaterialTheme.colors.onSecondary,
        disabledBackgroundColor = MaterialTheme.colors.secondary.copy(alpha = ContentAlpha.disabled),
        disabledContentColor = MaterialTheme.colors.onSecondary,
    ),
    contentPadding = buttonPadding,
) {
    BaseButtonContent(icon = icon, text = text)
}


@Composable
fun OutlineTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable () -> Unit = { },
    text: (@Composable () -> Unit)? = null,
) = OutlinedButton (
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
    contentPadding = buttonPadding,
) {
    CompositionLocalProvider(
        LocalTextStyle provides MaterialTheme.typography.button.copy(color = MaterialTheme.colors.secondary),
        LocalContentAlpha provides if (enabled) ContentAlpha.high else ContentAlpha.disabled,
    ) {
        BaseButtonContent(icon = icon, text = text)
    }
}

@Composable
private fun BaseButtonContent(
    icon: @Composable () -> Unit,
    text: (@Composable () -> Unit)?,
) = Row(verticalAlignment = Alignment.CenterVertically) {
    icon()
    if (text != null) {
        text()
    }
}