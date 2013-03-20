package com.team2052.frckrawler;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.*;
import android.os.*;
import android.view.View;
import android.widget.*;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.bluetooth.BluetoothClientService;
import com.team2052.frckrawler.bluetooth.BluetoothServerService;
import com.team2052.frckrawler.bluetooth.BluetoothServerService.CloseBinder;
import com.team2052.frckrawler.database.*;
import com.team2052.frckrawler.database.structures.*;

public class BluetoothServerManagerActivity extends TabActivity 
							implements View.OnClickListener, DialogInterface.OnClickListener {
	
	private int REQUEST_BT_ENABLED = 1;
	private DBManager dbManager;
	private Event selectedEvent;
	
	private Event[] events;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth_server_manager);
		
		findViewById(R.id.chooseEvent).setOnClickListener(this);
		findViewById(R.id.hostToggle).setOnClickListener(this);
		
		dbManager = DBManager.getInstance(this);
	}
	
	public void onResume() {
		
		super.onResume();
		
		((ToggleButton)findViewById(R.id.hostToggle)).
			setChecked(BluetoothServerService.isRunning());
		
		Event e = BluetoothServerService.getHostedEvent();
		
		if(e != null) {
			((TextView)findViewById(R.id.eventName)).
				setText(e.getEventName() + ", " + e.getGameName());
			
			selectedEvent = e;
		}
	}
	
	public void onClick(View v) {
		
		switch(v.getId()) {
			case R.id.chooseEvent:
				
				events = dbManager.getAllEvents();
				CharSequence[] eventNames = new String[events.length];
				
				for(int i = 0; i < events.length; i++) {
					
					eventNames[i] = events[i].getEventName() + 
							", " + events[i].getGameName();
				}
				
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				
				builder.setTitle("Events");
				builder.setItems(eventNames, this);
				
				builder.show();
				
				break;
				
			case R.id.hostToggle:
				
				ToggleButton toggle = (ToggleButton)findViewById(R.id.hostToggle);
				
				if(selectedEvent != null) {
					
					Intent btIntent = new Intent(this, BluetoothServerService.class);
					btIntent.putExtra(BluetoothServerService.HOSTED_EVENT_ID_EXTRA, 
							selectedEvent.getEventID());
				
					if(toggle.isChecked()) {
						if(BluetoothAdapter.getDefaultAdapter() == null) {
							Toast.makeText(this, "Sorry, your device does not " +
									"support Bluetooth. You will not be able to " +
									"open a server and recieve data from scouts.", 
									Toast.LENGTH_LONG).show();
							
							((ToggleButton)findViewById(R.id.hostToggle)).
								setChecked(false);
							return;
							
						} else {
							if(BluetoothAdapter.getDefaultAdapter().isEnabled()) {
								startService(btIntent);
								Toast.makeText(this, "Server started.", Toast.LENGTH_LONG).show();
								
							} else {
								
								Intent enableBtIntent = 
							    		new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
							    startActivityForResult(enableBtIntent, REQUEST_BT_ENABLED);
							}
						}
					
					} else {
						this.getApplication().bindService(btIntent, 
								new ServerCloseConnection(), Context.BIND_IMPORTANT);
						Toast.makeText(this, "Server stopped.", Toast.LENGTH_LONG).show();
					}
				} else {
					
					Toast.makeText(this, 
							"Could not start Bluetooth. No event selected.", 
							Toast.LENGTH_LONG).show();
					
					toggle.setChecked(false);
				}
				
				break;
		}
	}

	public void onClick(DialogInterface dialog, int which) {
		
		selectedEvent = events[which];
		((TextView)findViewById(R.id.eventName)).
				setText(selectedEvent.getEventName() + ", " + selectedEvent.getGameName());
		dialog.dismiss();
	}
	
	private class ServerCloseConnection implements ServiceConnection {

		public void onServiceConnected(ComponentName comp, IBinder binder) {
			
			CloseBinder closeBinder = (CloseBinder)binder;
			closeBinder.closeServer();
		}

		public void onServiceDisconnected(ComponentName comp) {
			
			
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(requestCode == REQUEST_BT_ENABLED && resultCode == RESULT_OK) {
			
			Intent btIntent = new Intent(this, BluetoothServerService.class);
			btIntent.putExtra(BluetoothServerService.HOSTED_EVENT_ID_EXTRA, 
					selectedEvent.getEventID());
			startService(btIntent);
		}
	}
}
