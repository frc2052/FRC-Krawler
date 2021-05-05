package com.team2052.frckrawler

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.team2052.frckrawler.data.EventRepository
import com.team2052.frckrawler.data.model.Event
import com.team2052.frckrawler.data.provider.FRCKrawlerDatabase
import com.team2052.frckrawler.network.ApiManager
import com.team2052.frckrawler.ui.FRCKrawlerApp
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

//    private val bluetoothBroadcastReceiver = BluetoothBroadcastReceiver(this).also {
//        it.onDeviceDiscovered = {
//
//        }
//        it.onDeviceDiscoveryFinished = {
//
//        }
//        it.onBluetoothStateChange = {
//
//        }
//        it.onDevicePairingStateChange = {
//
//        }
//    }
//    private val bluetoothController = BluetoothController(activity = this, bluetoothBroadcastReceiver = bluetoothBroadcastReceiver)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent { FRCKrawlerApp(activity = this, startDestination = "modeSelectScreen") }

        // Avoid incrementing the schema number
        applicationContext.deleteDatabase("frckrawler.db")
        val database = Room.databaseBuilder(
            applicationContext,
            FRCKrawlerDatabase::class.java,
            "frckrawler.db"
        ).fallbackToDestructiveMigration().build()

//        val repo = EventRepository(database.eventDAO(), )
//
//        lifecycleScope.launch {
//
//            repo.getEvent("Hello world!")
//
//            Log.d("DB_TEST", )
//        }

        // Temporary to test API calls
        lifecycleScope.launch {
            val match = ApiManager.tbaApi.getMatches( 2052, "2019mndu2")
            Log.d("NetworkingTest", match.toString())
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        when(requestCode) {
//            BluetoothManager.BluetoothConstants.BLUETOOTH_ENABLE_REQUEST_CODE -> {
//                when(resultCode) {
//                    Activity.RESULT_OK -> {
//
//                    }
//                    Activity.RESULT_CANCELED -> {
//                        bluetoothManager.requestBluetoothEnable(this)
//                    }
//                }
//            }
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        bluetoothManager.cleanup()
//    }
}