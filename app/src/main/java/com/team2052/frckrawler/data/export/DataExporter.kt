package com.team2052.frckrawler.data.export

import android.net.Uri

interface DataExporter {
  suspend fun export(fileUri: Uri, type: ExportType, eventId: Int)
}