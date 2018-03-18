package com.team2052.frckrawler.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import com.team2052.frckrawler.R
import com.team2052.frckrawler.activities.HomeActivity
import com.team2052.frckrawler.bluetooth.server.ServerStatus
import rx.Observer

class NotificationServerStatusObserver(private val context: Context) : Observer<ServerStatus> {
    private val serverOnNotification: Notification
    private val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        //Why oreo, why?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(NotificationChannel("misc", "Miscellaneous", NotificationManager.IMPORTANCE_LOW))
        }

        val b = NotificationCompat.Builder(context, "misc")
        b.setSmallIcon(R.drawable.ic_stat_knightkrawler)
        b.setContentTitle(context.resources.getString(R.string.server_open))
        b.setContentText(context.resources.getString(R.string.server_open_description))
        b.color = context.resources.getColor(R.color.primary)
        b.setOngoing(true)

        val resultIntent = HomeActivity.newInstance(context, R.id.nav_item_server)
        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addParentStack(HomeActivity::class.java)
        stackBuilder.addNextIntent(resultIntent)
        val resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        b.setContentIntent(resultPendingIntent)

        serverOnNotification = b.build()
    }

    override fun onCompleted() {

    }

    override fun onError(e: Throwable) {
        notificationManager.cancel(SERVER_OPEN_ID)
    }

    override fun onNext(serverStatus: ServerStatus) {
        if (serverStatus.state) {
            notificationManager.notify(SERVER_OPEN_ID, serverOnNotification)
        } else {
            notificationManager.cancel(SERVER_OPEN_ID)
        }

        if (serverStatus.syncing && serverStatus.device != null) {
            notificationManager.notify(SYNC_ONGOING_ID, buildSyncingWithDeviceNotification(serverStatus.device))
        } else {
            notificationManager.cancel(SYNC_ONGOING_ID)
        }
    }


    private fun buildSyncingWithDeviceNotification(device: BluetoothDevice): Notification {
        val b = NotificationCompat.Builder(context, "misc")
        b.setSmallIcon(R.drawable.ic_stat_sync)
        b.color = context.resources.getColor(R.color.primary)
        b.setContentTitle("Syncing")
        b.setContentText("Syncing with " + device.name)
        b.setDefaults(0)
        b.setProgress(0, 0, true)
        b.setOngoing(true)
        return b.build()
    }

    companion object {
        val SYNC_ONGOING_ID = 1
        private val SERVER_OPEN_ID = 10
    }
}
