package com.team2052.frckrawler

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.os.ConfigurationCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.team2052.frckrawler.bluetooth.BluetoothManager
import com.team2052.frckrawler.compose.theme.FrcKrawlerTheme
import com.team2052.frckrawler.database.DataBase
import com.team2052.frckrawler.database.table.Event
import com.team2052.frckrawler.database.table.Game
import com.team2052.frckrawler.modeSelect.ModeSelector
import com.team2052.frckrawler.network.ApiManager
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var bluetoothManager: BluetoothManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        setContent {
            FrcKrawlerTheme(darkTheme = true) {
                FRCKrawlerScaffold() {
                    ModeSelector(padding = PaddingValues(24.dp))
                }
            }
        }

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
            BluetoothManager.BluetoothConstants.BLUETOOTH_ENABLE_REQUEST_CODE -> {
                when(resultCode) {
                    Activity.RESULT_OK -> {

                    }
                    Activity.RESULT_CANCELED -> {
                        bluetoothManager.requestBluetoothEnable(this)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothManager.cleanup()
    }
}

@Composable
fun FRCKrawlerScaffold(
    content: @Composable () -> Unit
) {
    FrcKrawlerTheme(darkTheme = true) {
        Scaffold(
            topBar = { FRCKrawlerAppBar("FRC Krawler") }
        ) { padding ->
            Box {
                Icon(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    painter = painterResource(id = R.drawable.ic_bg_logo),
                    contentDescription = "",
                    tint = Color(0x14000000)
                )
                Column(modifier = Modifier.padding(padding)) {
                    content()
//                    Dropdown(
//                        modifier = Modifier,
//                        onDismissRequest = { /*TODO*/ }
//                    ) {
//                        DropdownItem(onClick = { /*TODO*/ }) {
//                            Text("Hello world!")
//                        }
//                        DropdownItem(onClick = { /*TODO*/ }) {
//                            Text("Hello world!")
//                        }
//                        DropdownItem(onClick = { /*TODO*/ }) {
//                            Text("Hello world!")
//                        }
//                    }
                }
            }
        }
    }
}

@Composable
fun FRCKrawlerAppBar(
    title: String
) = TopAppBar(
    title = {
        Text(
            modifier = Modifier.padding(start = /*56.dp*/ 0.dp),
            text = title.toUpperCase(ConfigurationCompat.getLocales(LocalConfiguration.current)[0])
        )
    },
    actions = {
        var actionsExpanded by remember { mutableStateOf(false) }
        IconButton(onClick = { actionsExpanded = true }) {
            Icon(Icons.Filled.MoreVert, contentDescription = "")
        }
        DropdownMenu(expanded = actionsExpanded, onDismissRequest = { actionsExpanded = false }) {
            DropdownMenuItem(onClick = { /*TODO*/ }) {
                Text("Option 1")
            }
            DropdownMenuItem(onClick = { /*TODO*/ }) {
                Text("Option 2")
            }
            DropdownMenuItem(onClick = { /*TODO*/ }) {
                Text("Option 3")
            }
        }
    },
    backgroundColor = MaterialTheme.colors.primary
)