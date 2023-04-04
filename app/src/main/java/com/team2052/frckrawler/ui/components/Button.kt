package com.team2052.frckrawler.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FRCKrawlerButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    border: BorderStroke? = null,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    content: (@Composable () -> Unit)? = null,
) {
    val contentColor by colors.contentColor(enabled)

    Surface(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(4.dp),
        color = colors.backgroundColor(enabled).value,
        contentColor = contentColor.copy(alpha = 1f),
        border = border,
        elevation = if (content == null && icon != null) 6.dp else 0.dp,
        interactionSource = remember { MutableInteractionSource() },
    ) {
        CompositionLocalProvider(LocalContentAlpha provides contentColor.alpha) {
            ProvideTextStyle(MaterialTheme.typography.button) {
                Row(
                    Modifier
                        .defaultMinSize(minWidth = 36.dp)
                        .height(36.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (icon != null) {
                        Box(
                            modifier = Modifier.requiredSize(36.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = "",
                            )
                        }
                    } else {
                        Spacer(modifier = modifier.width(12.dp))
                    }
                    if (content != null) {
                        Box(
                            modifier = Modifier
                                .requiredHeight(36.dp)
                                .padding(end = 12.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            content()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FRCKrawlerIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector,
    border: BorderStroke? = null,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
) = FRCKrawlerButton(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    icon = icon,
    border = border,
    colors = colors,
    content = null,
)

@Composable
fun FRCKrawlerOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    border: BorderStroke? = ButtonDefaults.outlinedBorder,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    content: (@Composable () -> Unit)? = null,
) = FRCKrawlerButton(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    icon = icon,
    border = border,
    colors = colors,
    content = content,
)