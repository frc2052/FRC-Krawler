package com.team2052.frckrawler.ui.components.fields

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.team2052.frckrawler.R
import com.team2052.frckrawler.ui.theme.gray
import com.team2052.frckrawler.ui.theme.lightGray

@Composable
fun FRCKrawlerTextField(
  modifier: Modifier = Modifier,
  value: String,
  onValueChange: (String) -> Unit,
  icon: @Composable () -> Unit = { },
  validity: Boolean = true,
  isError: (Boolean) -> Boolean = { false },
  enabled: Boolean = true,
  readOnly: Boolean = false,
  label: String? = null,
  keyboardOptions: KeyboardOptions = KeyboardOptions(
    capitalization = KeyboardCapitalization.Sentences,
    imeAction = ImeAction.Done
  ),
  onFocusChange: (Boolean) -> Unit = { },
) {
  val focusManager = LocalFocusManager.current

  val interactionSource = remember { MutableInteractionSource() }
  val hasFocus by interactionSource.collectIsFocusedAsState()
  var lastFocus by remember { mutableStateOf(hasFocus) }
  if (hasFocus != lastFocus) {
    onFocusChange(hasFocus)
  }
  // Sets the last focus before the next recompose (the placement is important)
  lastFocus = hasFocus

  val errorCheck = { if (!hasFocus) isError(validity) else false }

  OutlinedTextField(
    modifier = modifier,
    value = value,
    onValueChange = {
      onValueChange(it)
    },
    enabled = enabled,
    readOnly = readOnly,
    label = { if (label != null) Text(label) },
    trailingIcon = {
      Crossfade(
        targetState = errorCheck(),
        animationSpec = tween(200),
      ) {
        if (it) Icon(
          imageVector = Icons.Filled.Warning,
          contentDescription = stringResource(R.string.cd_text_field_error),
          tint = MaterialTheme.colors.error, // this prevents the icon from switching colors between animations
        ) else icon()
      }
    },
    isError = errorCheck(),
    keyboardOptions = keyboardOptions,
    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
    singleLine = true,
    interactionSource = interactionSource,
    colors = TextFieldDefaults.outlinedTextFieldColors(
      unfocusedBorderColor = lightGray,
      cursorColor = MaterialTheme.colors.primary,
      focusedBorderColor = MaterialTheme.colors.primary,
      focusedLabelColor = MaterialTheme.colors.primary,
      unfocusedLabelColor = gray,
      trailingIconColor = gray,
      errorLabelColor = MaterialTheme.colors.error,
    ),
  )
}