package com.team2052.frckrawler

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.os.ConfigurationCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.team2052.frckrawler.base.Dropdown4
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
//                    Column(modifier = Modifier.padding(24.dp)) {
//                        Row (modifier = Modifier.fillMaxWidth().padding(0.dp, 32.dp)) {
//                            Column {
//                                Dropdown4(modifier = Modifier.background(Color.Magenta),dropdownList = listOf("Hello world!","Hello world!","Hello world!"))
//                            }
//                        }
//                    }
                    LazyColumn(modifier = Modifier.padding(12.dp)) {
                        item { Column(modifier = Modifier.padding(24.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(text = "TEST", style = MaterialTheme.typography.h6)
                                    Text(text = "TEST", style = MaterialTheme.typography.subtitle1)
                                }
                                IconButton(modifier = Modifier.size(48.dp), onClick = {  }) {
                                    Icon(
                                        modifier = Modifier.size(40.dp),
                                        imageVector = if(true) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                                        contentDescription = "Expand Card"
                                    )
                                }
                            }
                            if(true) {
                                Row (modifier = Modifier.fillMaxWidth().padding(0.dp, 32.dp)) {
                                    Column() {
                                        Dropdown4(modifier = Modifier.background(Color.Magenta),dropdownList = listOf("Hello world!","Hello world!","Hello world!"))
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(onClick = {  }) {
                                        Text(
                                            text = "TEST".toUpperCase(ConfigurationCompat.getLocales(LocalConfiguration.current)[0]),
                                            color = MaterialTheme.colors.secondary
                                        )
                                    }
                                }
                            }
                        } }
                    }
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
        IconButton(onClick = { /*TODO*/ }) {
            Icon(Icons.Filled.MoreVert, contentDescription = "")
        }
    },
    backgroundColor = MaterialTheme.colors.primary
)