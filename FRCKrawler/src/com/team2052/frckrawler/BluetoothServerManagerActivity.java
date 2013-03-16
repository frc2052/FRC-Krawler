package com.team2052.frckrawler;

import android.app.AlertDialog;
import android.content.*;
import android.os.*;
import android.view.View;
import android.widget.*;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.bluetooth.BluetoothServerService;
import com.team2052.frckrawler.bluetooth.BluetoothServerService.CloseBinder;
import com.team2052.frckrawler.database.*;
import com.team2052.frckrawler.database.structures.*;

public class BluetoothServerManagerActivity extends TabActivity 
							implements View.OnClickListener, DialogInterface.OnClickListener {
	
	private DBManager dbManager;
	private Event selectedEvent;
	
	private Event[] events;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth_server_manager);
		
		findViewById(R.id.chooseEvent).setOnClickListener(this);
		findViewById(R.id.hostToggle).setOnClickListener(this);
		findViewById(R.id.clientTest).setOnClickListener(this);
		
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
					
						startService(btIntent);
						Toast.makeText(this, "Server started.", Toast.LENGTH_LONG).show();
					
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
				
			case R.id.clientTest:
				
				Intent i = new Intent(this, ClientTestActivity.class);
				startActivity(i);
				
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
}
