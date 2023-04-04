package com.team2052.frckrawler.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarData
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.*

@Composable
fun FRCKrawlerSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
) {
    Snackbar(
        modifier = modifier.padding(24.dp),
        action = {
            TextButton(onClick = { onDismiss() }) {
                snackbarData.actionLabel?.let { actionLabel ->
                    Text(text = actionLabel)
                }
            }
        },
    ) { Text(snackbarData.message) }
}

data class Message(val id: Long, val message: String)

object SnackbarManager {
    private val _snacks: MutableStateFlow<List<Message>> = MutableStateFlow(emptyList())
    val snacks: StateFlow<List<Message>> get() = _snacks

    fun showSnackbar(message: String) {
        _snacks.update { currentMessages ->
            currentMessages + Message(
                UUID.randomUUID().mostSignificantBits,
                message
            )
        }
    }

    fun dismissSnackbar(id: Long) {
        _snacks.update { currentMessages ->
            currentMessages.filterNot { it.id == id }
        }
    }
}