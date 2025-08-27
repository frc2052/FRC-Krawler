package com.team2052.frckrawler.ui.scout

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.FrcKrawlerPreview
import com.team2052.frckrawler.ui.theme.FrcKrawlerTheme
import com.team2052.frckrawler.ui.theme.StaticColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AnimatedSaveButton(
  modifier: Modifier = Modifier,
  onSave: () -> Unit
) {
  var showSuccess by remember { mutableStateOf(false) }
  val scope = rememberCoroutineScope()
  val transition = updateTransition(showSuccess, label = "fab_success")

  val fabColor by transition.animateColor { success ->
    if (success) StaticColors.successContainer else FloatingActionButtonDefaults.containerColor
  }
  FloatingActionButton(
    containerColor = fabColor,

    onClick = {
      if (showSuccess) return@FloatingActionButton

      onSave()

      scope.launch {
        showSuccess = true
        delay(800)
        showSuccess = false
      }
    }
  ) {
    val iconTint by transition.animateColor { success ->
      if (success) StaticColors.onSuccess else LocalContentColor.current
    }

    transition.AnimatedContent { success ->
      if (success) {
        Icon(
          imageVector = Icons.Filled.Check,
          contentDescription = stringResource(R.string.data_saved_description),
          tint = iconTint
        )
      } else {
        Icon(
          imageVector = Icons.Filled.Save,
          contentDescription = stringResource(R.string.save_description),
          tint = iconTint
        )
      }
    }
  }
}

@FrcKrawlerPreview
@Composable
private fun AnimatedSaveButtonPreview() {
  FrcKrawlerTheme {
    AnimatedSaveButton(onSave = {})
  }
}