package com.team2052.frckrawler

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.team2052.frckrawler.bluetooth.BluetoothManager
import com.team2052.frckrawler.database.DataBase
import com.team2052.frckrawler.database.table.Event
import com.team2052.frckrawler.database.table.Game
import com.team2052.frckrawler.network.ApiManager
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    var bluetoothManager: BluetoothManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bluetoothManager = BluetoothManager(baseContext)
        // Bluetooth requires background location on newer devices
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if(ContextCompat.checkSelfPermission(baseContext, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), 0)
            }
        }
        bluetoothManager?.setupBluetoothCapabilities(this)

        val db = Room.databaseBuilder(applicationContext, DataBase::class.java, "database-name").fallbackToDestructiveMigration().build()

        lifecycleScope.launch {
            db.gameDAO().insert(Game(0, "test", 2020))
            db.eventDAO().insert(Event(0, "event", "12/12/12"))
            val year = db.gameDAO().getGameByYear(2020);
        }

        // Temporary to test API calls
        lifecycleScope.launch {
            val match = ApiManager.tbaApi.getMatches( 2052, "2019mndu2")
            Log.d("NetworkingTest", match.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            BluetoothManager.BLUETOOTH_ENABLE_REQUEST_CODE -> {
                when(resultCode) {
                    Activity.RESULT_OK -> {

                    }
                    Activity.RESULT_CANCELED -> {
                        bluetoothManager?.requestBluetoothEnable(this)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothManager?.cleanup()
    }
}