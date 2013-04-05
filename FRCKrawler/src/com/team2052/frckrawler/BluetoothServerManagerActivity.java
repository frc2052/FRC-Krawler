package com.team2052.frckrawler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.team2052.frckrawler.bluetooth.BluetoothServerService;
import com.team2052.frckrawler.bluetooth.BluetoothServerService.CloseBinder;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Event;

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
		findViewById(R.id.importDB).setOnClickListener(this);
		findViewById(R.id.exportDB).setOnClickListener(this);
		
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
				
			case R.id.importDB:
				
				if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
					AlertDialog.Builder bu = new AlertDialog.Builder(this);
					bu.setTitle("Sorry...");
					bu.setMessage("FRCKrawler could not import your database. Your device " +
							"does not have an SD card mounted.");
					bu.setNeutralButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					bu.show();
				}
				
				try {
			        File sd = Environment.getExternalStorageDirectory();
			        File data = Environment.getDataDirectory();

			        if (sd.canWrite()) {
			            String currentDBPath = "//data//com.team2052.frckrawler//databases//scoutingdb";
			            String backupDBPath = "FRCKrawlerBackup.sqlite";
			            File currentDB = new File(data, currentDBPath);
			            File backupDB = new File(sd, backupDBPath);

			            if (backupDB.exists()) {
			                FileChannel src = new FileInputStream(currentDB).getChannel();
			                FileChannel dst = new FileOutputStream(backupDB).getChannel();
			                src.transferFrom(dst, 0, dst.size());
			                src.close();
			                dst.close();
			            } else 
			            	Toast.makeText(this, "Could not find the file " +
			            			"FRCKrawlerBackup.sqlite in your SD card's " +
			            			"root folder.", Toast.LENGTH_LONG).show();
			        }
			    } catch (FileNotFoundException e) {
			    	e.printStackTrace();
			    	Log.e("FCKrawler", e.getMessage());
			    } catch (IOException e) {
			    	Log.e("FCKrawler", e.getMessage());
				}
				
				AlertDialog.Builder bu = new AlertDialog.Builder(this);
				bu.setTitle("Success!");
				bu.setMessage("Imported the database.");
				bu.setNeutralButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				bu.show();
				
				break;
				
			case R.id.exportDB:
				
				if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
					AlertDialog.Builder b = new AlertDialog.Builder(this);
					b.setTitle("Sorry...");
					b.setMessage("FRCKrawler could not export your database to the " +
							"SD card. Your device does not have an SD card, or it is not " +
							"available for writing.");
					b.setNeutralButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					b.show();
				}
				
				try {
			        File sd = Environment.getExternalStorageDirectory();
			        File data = Environment.getDataDirectory();

			        if (sd.canWrite()) {
			            String currentDBPath = "//data//com.team2052.frckrawler//databases//scoutingdb";
			            String backupDBPath = "FRCKrawlerBackup.sqlite";
			            File currentDB = new File(data, currentDBPath);
			            File backupDB = new File(sd, backupDBPath);

			            if (currentDB.exists()) {
			                FileChannel src = new FileInputStream(currentDB).getChannel();
			                FileChannel dst = new FileOutputStream(backupDB).getChannel();
			                dst.transferFrom(src, 0, src.size());
			                src.close();
			                dst.close();
			            } else 
			            	Toast.makeText(this, "Error in finding local " +
			            			"database file.", Toast.LENGTH_LONG).show();
			        }
			    } catch (FileNotFoundException e) {
			    	e.printStackTrace();
			    	Log.e("FCKrawler", e.getMessage());
			    } catch (IOException e) {
			    	Log.e("FCKrawler", e.getMessage());
				}
				
				AlertDialog.Builder b = new AlertDialog.Builder(this);
				b.setTitle("Success!");
				b.setMessage("Exported file to the SD card. File saved as " +
						"FRCKrawlerBackup.db in the SD card's root directory.");
				b.setNeutralButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				b.show();
				
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
