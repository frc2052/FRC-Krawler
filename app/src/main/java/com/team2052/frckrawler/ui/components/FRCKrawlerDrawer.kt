package com.team2052.frckrawler.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.team2052.frckrawler.R

@Composable
fun FRCKrawlerDrawer(
    modifier: Modifier = Modifier,
) = Column(modifier = modifier) {
    Surface(elevation = 8.dp) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier
                    .size(128.dp)
                    .padding(24.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.primary)
                    .padding(24.dp),
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "",
                tint = MaterialTheme.colors.secondary
            )
            Column(modifier = Modifier, verticalArrangement = Arrangement.Center) {
                Text("FRC Krawler")
                Text("By Team 2052 Knight Krawler")
            }
        }
    }
}