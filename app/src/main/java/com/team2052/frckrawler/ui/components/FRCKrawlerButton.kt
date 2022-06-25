package com.team2052.frckrawler.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.ui.theme.spaceMedium
import com.team2052.frckrawler.ui.theme.spaceSmall
import java.util.*

private val buttonPadding = PaddingValues(horizontal = spaceMedium, vertical = spaceSmall)

@Composable
fun SolidTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable () -> Unit = { },
    text: String? = null,
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
) { BaseButtonContent(icon, text) }

@Composable
fun OutlineTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable () -> Unit = { },
    text: String? = null,
) = OutlinedButton (
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
    contentPadding = buttonPadding,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        icon()
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.button.copy(color = MaterialTheme.colors.secondary),
            LocalContentAlpha provides if (enabled) ContentAlpha.high else ContentAlpha.disabled,
        ) {
            if (text != null) {
                Text(text.uppercase(Locale.getDefault()))
            }
        }
    }
}

@Composable
private fun BaseButtonContent(
    icon: @Composable () -> Unit = { },
    text: String? = null,
) = Row(verticalAlignment = Alignment.CenterVertically) {
    icon()
    CompositionLocalProvider(
        LocalTextStyle provides MaterialTheme.typography.button.copy(color = MaterialTheme.colors.secondary)
    ) { if (text != null) { Text(text.uppercase(Locale.getDefault())) } }
}