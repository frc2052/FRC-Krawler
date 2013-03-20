package com.team2052.frckrawler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.team2052.frckrawler.bluetooth.BluetoothClientService;
import com.team2052.frckrawler.bluetooth.ClientServiceConnection;
import com.team2052.frckrawler.bluetooth.ClientThreadListener;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Event;
import com.team2052.frckrawler.gui.ProgressSpinner;

public class ScoutTypeActivity extends Activity implements OnClickListener, 
															ClientThreadListener, android.content.DialogInterface.OnClickListener {
	
	private AlertDialog progressDialog;
	private ClientServiceConnection connection;
	private Event selectedEvent;
	private DBManager db;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scout_type);
		
		findViewById(R.id.matchScout).setOnClickListener(this);
		findViewById(R.id.pitScout).setOnClickListener(this);
		findViewById(R.id.driverScout).setOnClickListener(this);
		findViewById(R.id.sync).setOnClickListener(this);
		
		connection = new ClientServiceConnection(this);
		db = DBManager.getInstance(this);
		
	}
	
	public void onResume() {
		
		super.onResume();
		
		((TextView)findViewById(R.id.scoutName)).
				setText(GlobalSettings.username);
		
		selectedEvent = db.scoutGetEvent();
		
		if(selectedEvent != null)
			((TextView)findViewById(R.id.eventInfo)).setText
				(selectedEvent.getEventName() + ", " + selectedEvent.getLocation());
	}
	
	public void onDestroy() {
		
		super.onDestroy();
		
		try {
			unbindService(connection);
		} catch(IllegalArgumentException e) {}
	}

	public void onClick(View v) {
		
		Intent i;
		
		switch(v.getId()) {
			case R.id.matchScout:
				
				i = new Intent(this, ScoutActivity.class);
				i.putExtra(ScoutActivity.SCOUT_TYPE_EXTRA, 
						ScoutActivity.SCOUT_TYPE_MATCH);
				startActivity(i);
				
				break;
				
			case R.id.pitScout:
				
				i = new Intent(this, ScoutActivity.class);
				i.putExtra(ScoutActivity.SCOUT_TYPE_EXTRA, 
						ScoutActivity.SCOUT_TYPE_PIT);
				startActivity(i);
				
				break;
				
			case R.id.driverScout:
				
				i = new Intent(this, ScoutActivity.class);
				i.putExtra(ScoutActivity.SCOUT_TYPE_EXTRA, 
						ScoutActivity.SCOUT_TYPE_DRIVER);
				startActivity(i);
				
				break;
				
			case R.id.sync: 
				
				i = new Intent(this, BluetoothClientService.class);
				i.putExtra(BluetoothClientService.SERVER_MAC_ADDRESS, 
						GlobalSettings.masterMACAddress);
				bindService(i, connection, Context.BIND_AUTO_CREATE);
				
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Syncing...");
				builder.setView(new ProgressSpinner(this));
				builder.setNeutralButton("Cancel", this);
				progressDialog = builder.create();
				progressDialog.show();
				
				break;
		}
	}

	public void onSuccessfulSync() {
		
		runOnUiThread(new Runnable() {
			public void run() {
				unbindService(connection);
				progressDialog.dismiss();
				Toast.makeText(getApplicationContext(), 
						"Sync successful.", Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void onUnsuccessfulSync(String _errorMessage) {
		
		final String errorMessage = _errorMessage;
		
		runOnUiThread(new Runnable() {
			
			public void run() {
				unbindService(connection);
				progressDialog.dismiss();
				Toast.makeText(getApplicationContext(), "Sync unsuccessful. " + 
						errorMessage + ".", Toast.LENGTH_SHORT).show();
			}
		});
		
	}
	
	public void onUpdate(String _message) {
		
		final String message = _message;
		
		runOnUiThread(new Runnable() {
			
			public void run() {
				Toast.makeText(getApplicationContext(), message, 
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void onClick(DialogInterface dialog, int which) {
		
		if(which == DialogInterface.BUTTON_NEUTRAL) {
			unbindService(connection);
		}
	}
}
