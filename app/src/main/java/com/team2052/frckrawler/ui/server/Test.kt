package com.team2052.frckrawler.ui.server

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun TestCompose() {
    Row(
        verticalAlignment = Alignment.Bottom
    ){
        Bar(
            modifier = Modifier.width(128.dp)
                .height(200.dp),
            color = Color.Cyan
        )
        Bar(
            modifier = Modifier.width(128.dp)
                .height(IntrinsicSize.Max),
            color = Color.Green
        )
        Bar(
            modifier = Modifier.width(128.dp)
                .height(400.dp),
            color = Color.Magenta
        )
    }
}

@Composable
fun Bar(
    modifier: Modifier = Modifier,
    color: Color,
) {
    Box(
       modifier = modifier
           .background(color)
           .clip(RoundedCornerShape(
               topStart = 16.dp,
               topEnd = 16.dp
           ))
    )
}