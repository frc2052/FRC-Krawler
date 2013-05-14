package com.team2052.frckrawler;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.team2052.frckrawler.bluetooth.BluetoothScoutClientService;
import com.team2052.frckrawler.bluetooth.BluetoothSummaryClientService;
import com.team2052.frckrawler.bluetooth.ClientConnection;
import com.team2052.frckrawler.bluetooth.ClientThreadListener;
import com.team2052.frckrawler.bluetooth.ScoutServiceConnection;
import com.team2052.frckrawler.bluetooth.SummaryServiceConnection;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.User;
import com.team2052.frckrawler.gui.ProgressSpinner;

public class MainActivity extends Activity implements DialogInterface.OnClickListener, 
																ClientThreadListener, 
																OnClickListener {
	
	Context context = this;
	
	private static final String SUPERUSER_NAME = "admin";
	private static final int REQUEST_BT_ENABLE = 1;
	
	private BluetoothDevice[] devices;
	private int selectedDeviceAddress;
	private AlertDialog progressDialog;
	private ClientConnection connection;
	private ClientConnection summaryConnection;
	private EditText scoutLoginName;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        findViewById(R.id.continueScouting).setOnClickListener(this);
        findViewById(R.id.sync_summary).setOnClickListener(this);
        findViewById(R.id.view_summary).setOnClickListener(this);
    }
    
    public void onDestroy() {
		super.onDestroy();
		
		try {
			connection.closeBTConnection();
			unbindService(connection);
		} catch(Exception e) {}
		
		try {
			summaryConnection.closeBTConnection();
			unbindService(summaryConnection);
		} catch(Exception e) {}
		
		if(progressDialog != null)
    		progressDialog.dismiss();
	}
    
    /*
     * Joins a competition in progress
     * Device needs to have BlueTooth paired with other devices
     * Displays list of devices then calls the namestamp method
     * 
     * @param view
     */
    public void joinCompetition(View view) {
    	
    	if(BluetoothAdapter.getDefaultAdapter() == null) {
			Toast.makeText(this, "Sorry, your device does not support Bluetooth. " +
					"You are unable to sync with a server.", Toast.LENGTH_LONG);
			return;
		}
		
		devices = BluetoothAdapter.getDefaultAdapter().
				getBondedDevices().toArray(new BluetoothDevice[0]);
		CharSequence[] deviceNames = new String[devices.length];
		
		for(int k = 0; k < deviceNames.length; k++)
			deviceNames[k] = devices[k].getName();
			
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select Server Device");
		builder.setItems(deviceNames, this);
		builder.show();
    }
    
    /*
     * Hosts a competition with superuser access
     * 
     * @param view
     */
    public void hostCompetition(final View view) {
 
    	
    	final AlertDialog.Builder login = new AlertDialog.Builder(this);
    	login
    		.setTitle("Superuser Login")
    		.setCancelable(false);
    	
    	final EditText username = new EditText(context);
    	username.setHint("Superuser Password");
    	login.setView(username);
    	
    	login.setPositiveButton("Login", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				String user2 = username.getText().toString();
				
				if (user2.equalsIgnoreCase(SUPERUSER_NAME)) {
					Intent i = new Intent(getApplicationContext(), 
							BluetoothServerManagerActivity.class);
					startActivity(i);
					
				} else {
					
					Toast.makeText(getApplicationContext(), "Login failed.", 
							Toast.LENGTH_SHORT).show();
					
					dialog.dismiss();
				}
			}
		});
    	
    	login.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
    	
    	AlertDialog loginAlert = login.create();
    	loginAlert.show();
    }
    
    /*
     * Displays a Dialog that takes the name and adds it to a table in the database
     * 
     * @param blueToothDevice -
     * Meant to pass in the name of the selected BlueTooth device.
     */
    public void getScoutLogin() {
		
    	AlertDialog.Builder nameStamp = new AlertDialog.Builder(MainActivity.this);
    	final EditText name = new EditText(this);
    		
    	nameStamp
    		.setTitle("Scout's Login")
    		.setView(name)
    		.setCancelable(false);
    		
    	nameStamp.setNeutralButton("Login", new DialogInterface.OnClickListener() {
				
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				
			}
		});
    	
    	AlertDialog alertName = nameStamp.create();
    	alertName.show();
    }
    
	public void onClick(DialogInterface dialog, int which) {
		
		if(which == DialogInterface.BUTTON_NEUTRAL) {
			connection.closeBTConnection();
			unbindService(connection);
			
		} else {
			
			selectedDeviceAddress = which;
			BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		
			if(adapter == null) {
				Toast.makeText(this, "Sorry, your device does not support " +
						"Bluetooth. You may not sync with another database.", 
						Toast.LENGTH_LONG);
			}
		
			if (!adapter.isEnabled()) {
				Intent enableBtIntent = 
						new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_BT_ENABLE);
		    
			} else {
				
				SharedPreferences prefs = getSharedPreferences
						(GlobalSettings.PREFS_FILE_NAME, 0);
				Editor prefsEditor = prefs.edit();
				
				Intent i = new Intent(this, BluetoothScoutClientService.class);
				i.putExtra(BluetoothScoutClientService.SERVER_MAC_ADDRESS, 
						devices[which].getAddress());
				connection = new ScoutServiceConnection(this);
				bindService(i, connection, Context.BIND_AUTO_CREATE);
				prefsEditor.putString(GlobalSettings.MAC_ADRESS_PREF, 
						devices[selectedDeviceAddress].getAddress());
			
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Syncing...");
				builder.setView(new ProgressSpinner(this));
				builder.setNeutralButton("Cancel", this);
				progressDialog = builder.create();
				progressDialog.show();
				
				prefsEditor.commit();
			}
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(requestCode == REQUEST_BT_ENABLE && resultCode == RESULT_OK) {
			
			SharedPreferences prefs = getSharedPreferences
					(GlobalSettings.PREFS_FILE_NAME, 0);
			Editor prefsEditor = prefs.edit();
			
			Intent i = new Intent(this, BluetoothScoutClientService.class);
			i.putExtra(BluetoothScoutClientService.SERVER_MAC_ADDRESS, 
					devices[selectedDeviceAddress].getAddress());
			connection = new ScoutServiceConnection(this);
			bindService(i, connection, Context.BIND_AUTO_CREATE);
			prefsEditor.putString(GlobalSettings.MAC_ADRESS_PREF, 
					devices[selectedDeviceAddress].getAddress());
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Syncing...");
			builder.setView(new ProgressSpinner(this));
			builder.setNeutralButton("Cancel", this);
			progressDialog = builder.create();
			progressDialog.show();
			
			prefsEditor.commit();
		}
	}
    
    public void onCreditsClicked(View v) {
    	Intent i = new Intent(this, AboutDialogActivity.class);
    	startActivity(i);
    }

	public void onSuccessfulSync() {
		
		runOnUiThread(new Runnable() {
			public void run() {
				connection.closeBTConnection();
				unbindService(connection);
				progressDialog.dismiss();
				Toast.makeText(getApplicationContext(), 
						"Sync successful.", Toast.LENGTH_SHORT).show();
				
				scoutLoginName = new EditText(MainActivity.this);
		        scoutLoginName.setHint("Name");
				
				AlertDialog.Builder builder = new AlertDialog.Builder
						(MainActivity.this);
				builder.setTitle("Login");
				builder.setView(scoutLoginName);
				builder.setPositiveButton("Login", new UserDialogListener());
				builder.setNegativeButton("Cancel", new UserDialogListener());
				builder.show();
			}
		});
	}

	public void onUnsuccessfulSync(String _errorMessage) {
		
		final String errorMessage = _errorMessage;
		
		runOnUiThread(new Runnable() {
			
			public void run() {
				connection.closeBTConnection();
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
	
	private class UserDialogListener implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if(which == DialogInterface.BUTTON_POSITIVE) {
				User[] users = DBManager.getInstance(getApplicationContext()).
						scoutGetAllUsers();
				
				boolean isValid = false;
				
				for(User u : users) {
					if(u.getName().equals(scoutLoginName.getText().toString())) {
						GlobalSettings.userID = u.getID();
						isValid = true;
					}
				}
				
				if(isValid) {
					Intent i = new Intent(getApplicationContext(), 
							ScoutTypeActivity.class);
					startActivity(i);
					
				} else {
					Toast.makeText(getApplicationContext(), "Not a valid username. " +
							"The username must already be in the database.", 
							Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					
					 scoutLoginName = new EditText(MainActivity.this);
				     scoutLoginName.setHint("Name");
					
					AlertDialog.Builder builder = new AlertDialog.Builder
							(MainActivity.this);
					builder.setTitle("Login");
					builder.setView(scoutLoginName);
					builder.setPositiveButton("Login", new UserDialogListener());
					builder.setNegativeButton("Cancel", new UserDialogListener());
					builder.show();
				}
				
			} else {
				dialog.dismiss();
			}
		}
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.continueScouting) {
			if(DBManager.getInstance(this).scoutGetAllUsers().length == 0) {
				Toast.makeText(this, "This device has not been synced with a database. " +
						"Hit the 'Join' button to sync.", Toast.LENGTH_LONG).show();
				return;
			}
			
			scoutLoginName = new EditText(MainActivity.this);
		    scoutLoginName.setHint("Name");
			
			AlertDialog.Builder builder = new AlertDialog.Builder
					(MainActivity.this);
			builder.setTitle("Scout's Login");
			builder.setView(scoutLoginName);
			builder.setPositiveButton("Login", new UserDialogListener());
			builder.setNegativeButton("Cancel", new UserDialogListener());
			builder.show();
			
		} else if(v.getId() == R.id.sync_summary) {
			
			if(BluetoothAdapter.getDefaultAdapter() == null) {
				Toast.makeText(this, "Sorry, your device does not support Bluetooth. " +
						"You are unable to sync with a server.", Toast.LENGTH_LONG);
				return;
			}
			
			devices = BluetoothAdapter.getDefaultAdapter().
					getBondedDevices().toArray(new BluetoothDevice[0]);
			CharSequence[] deviceNames = new String[devices.length];
			
			for(int k = 0; k < deviceNames.length; k++)
				deviceNames[k] = devices[k].getName();
				
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Select Server Device");
			builder.setItems(deviceNames, new SummaryDialogListener());
			builder.show();
			
		} else if(v.getId() == R.id.view_summary) {
			Intent i = new Intent(this, SummaryActivity.class);
			startActivity(i);
		}
	}
	
	private class SummaryDialogListener implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			
			if(which == DialogInterface.BUTTON_NEUTRAL) {
				summaryConnection.closeBTConnection();
				unbindService(summaryConnection);
				dialog.dismiss();
				
			} else {
				
				selectedDeviceAddress = which;
				BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
			
				if(adapter == null) {
					Toast.makeText(MainActivity.this, "Sorry, your device does not support " +
							"Bluetooth. You may not sync with another database.", 
							Toast.LENGTH_LONG);
				}
			
				if (!adapter.isEnabled()) {
					Intent enableBtIntent = 
							new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableBtIntent, REQUEST_BT_ENABLE);
			    
				} else {
					Intent i = new Intent(MainActivity.this, BluetoothSummaryClientService.class);
					i.putExtra(BluetoothSummaryClientService.SERVER_MAC_ADDRESS, 
							devices[which].getAddress());
					summaryConnection = new SummaryServiceConnection(new SummaryClientListener());
					bindService(i, summaryConnection, Context.BIND_AUTO_CREATE);
				
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setTitle("Syncing...");
					builder.setView(new ProgressSpinner(MainActivity.this));
					builder.setNeutralButton("Cancel", new SummaryDialogListener());
					builder.setCancelable(false);
					progressDialog = builder.create();
					progressDialog.show();
				}
			}
		}
	}
	
	private class SummaryClientListener implements ClientThreadListener {

		@Override
		public void onSuccessfulSync() {
			
			runOnUiThread(new Runnable() {
				
				public void run() {
					
					progressDialog.dismiss();
					summaryConnection.closeBTConnection();
					
					try{
						unbindService(summaryConnection);
					} catch(IllegalArgumentException e) {}
					
					Toast.makeText(getApplicationContext(), 
							"Sync successful.", Toast.LENGTH_SHORT).show();
					
					Intent i = new Intent(MainActivity.this, SummaryActivity.class);
					startActivity(i);
				}
			});
		}

		@Override
		public void onUnsuccessfulSync(String _errorMessage) {
			final String errorMessage = _errorMessage;
			
			runOnUiThread(new Runnable() {
				
				public void run() {
					summaryConnection.closeBTConnection();
					
					try {
						unbindService(summaryConnection);
					} catch(IllegalArgumentException e) {}
					
					progressDialog.dismiss();
					Toast.makeText(getApplicationContext(), "Sync unsuccessful. " + 
							errorMessage + ".", Toast.LENGTH_SHORT).show();
				}
			});
		}

		@Override
		public void onUpdate(String _message) {
			final String message = _message;
			
			runOnUiThread(new Runnable() {
				
				public void run() {
					Toast.makeText(getApplicationContext(), message, 
							Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
}
