package com.team2052.frckrawler.ui.components.fields

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun FRCKrawlerColorField(
  modifier: Modifier = Modifier,
  value: Color?,
  onValueChange: (Color) -> Unit,
  icon: @Composable () -> Unit = { },
  validity: Boolean = true,
  validityCheck: (String) -> Unit = { },
  isError: (Boolean) -> Boolean = { false },
  enabled: Boolean = true,
  label: String,
  onFocusChange: (Boolean) -> Unit = { },
) {
//    FRCKrawlerTextField(
//        value = "0x",
//        onValueChange = {
//            onValueChange()
//            validityCheck(value)
//        },
//        label = ,
//    )
}