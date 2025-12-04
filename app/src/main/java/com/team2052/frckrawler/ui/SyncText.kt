package com.team2052.frckrawler.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import com.team2052.frckrawler.R
import com.team2052.frckrawler.bluetooth.OperationResult

@ReadOnlyComposable
@Composable
fun getResultText(result: OperationResult): String {
  return when (result) {
    OperationResult.VersionMismatch -> stringResource(R.string.sync_failed_version_mismatch)
    OperationResult.ServerConfigurationMismatch -> stringResource(R.string.sync_failed_server_config_mismatch)
    OperationResult.FailedToSaveConfiguration -> stringResource(R.string.sync_failed_save_config_error)
    OperationResult.Success,
    OperationResult.Unknown -> "" // Shouldn't be possible
  }
}