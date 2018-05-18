package com.team2052.frckrawler.services

import android.app.IntentService
import android.content.Intent
import android.os.Environment
import com.google.common.io.Files
import com.team2052.frckrawler.core.data.models.DBManager
import java.io.File

class BackupDBIntentService : IntentService("BackupDBIntentService") {
    override fun onHandleIntent(intent: Intent?) {
        val fileSystem = Environment.getExternalStorageDirectory()
        var backupLoc = File(fileSystem, "/FRCKrawler/DBBackups/")
        if (!backupLoc.exists()) {
            val created = backupLoc.mkdirs()
            if (!created) {
                backupLoc = File(fileSystem, "/")
            }
        }
        val fileName = intent?.getStringExtra(FILE_NAME_EXTRA) ?: "frc-krawler-db-backup"
        val currentDB = getDatabasePath(DBManager.DB_NAME)
        val backupDB = File(backupLoc, "$fileName.db")
        if (currentDB.exists()) {
            Files.copy(currentDB, backupDB)
        }
    }

    companion object {
        const val FILE_NAME_EXTRA = "com.team2052.FILE_NAME"
    }
}
