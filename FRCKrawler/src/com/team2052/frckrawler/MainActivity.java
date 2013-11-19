package com.team2052.frckrawler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
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

    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        findViewById(R.id.continueScouting).setOnClickListener(this);
        /*findViewById(R.id.sync_summary).setOnClickListener(this);
        findViewById(R.id.view_summary).setOnClickListener(this);*/
        
        OptionsActivity.restoreDefaultOptions(this, false);
    }
    
    @Override
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
	}
    
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
    
    public void openAdminInterface(View view) {
    	Intent i = new Intent(getApplicationContext(), 
				BluetoothServerManagerActivity.class);
		startActivity(i);
    }
    
    public void displayScoutLogin() {
    	AlertDialog.Builder nameStamp = new AlertDialog.Builder(MainActivity.this);
    	final EditText name = new EditText(this);
    		
    	nameStamp
    		.setTitle("Scout's Login")
    		.setView(name)
    		.setCancelable(false);
    	nameStamp.setNeutralButton("Login", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {}
		});
    	
    	AlertDialog alertName = nameStamp.create();
    	alertName.show();
    }
    
	@Override
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
						(GlobalValues.PREFS_FILE_NAME, 0);
				Editor prefsEditor = prefs.edit();
				
				Intent i = new Intent(this, BluetoothScoutClientService.class);
				i.putExtra(BluetoothScoutClientService.SERVER_MAC_ADDRESS, 
						devices[which].getAddress());
				connection = new ScoutServiceConnection(this);
				bindService(i, connection, Context.BIND_AUTO_CREATE);
				prefsEditor.putString(GlobalValues.MAC_ADRESS_PREF, 
						devices[selectedDeviceAddress].getAddress());
			
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Syncing...");
				builder.setView(new ProgressSpinner(this));
				builder.setNeutralButton("Cancel", this);
				builder.setCancelable(false);
				progressDialog = builder.create();
				progressDialog.show();
				prefsEditor.commit();
				lockScreenOrientation();
			}
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_BT_ENABLE && resultCode == RESULT_OK) {
			SharedPreferences prefs = getSharedPreferences
					(GlobalValues.PREFS_FILE_NAME, 0);
			Editor prefsEditor = prefs.edit();
			Intent i = new Intent(this, BluetoothScoutClientService.class);
			i.putExtra(BluetoothScoutClientService.SERVER_MAC_ADDRESS, 
					devices[selectedDeviceAddress].getAddress());
			connection = new ScoutServiceConnection(this);
			bindService(i, connection, Context.BIND_AUTO_CREATE);
			prefsEditor.putString(GlobalValues.MAC_ADRESS_PREF, 
					devices[selectedDeviceAddress].getAddress());
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Syncing...");
			builder.setView(new ProgressSpinner(this));
			builder.setNeutralButton("Cancel", this);
			progressDialog = builder.create();
			progressDialog.show();
			prefsEditor.commit();
			lockScreenOrientation();
		}
	}
    
    public void onCreditsClicked(View v) {
    	Intent i = new Intent(this, AboutDialogActivity.class);
    	startActivity(i);
    }

	@Override
	public void onSuccessfulSync() {
		runOnUiThread(new Runnable() {
			@Override
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
				releaseScreenOrientation();
			}
		});
	}

	@Override
	public void onUnsuccessfulSync(String _errorMessage) {
		final String errorMessage = _errorMessage;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				connection.closeBTConnection();
				unbindService(connection);
				progressDialog.dismiss();
				Toast.makeText(getApplicationContext(), "Sync unsuccessful. " + 
						errorMessage + ".", Toast.LENGTH_SHORT).show();
				releaseScreenOrientation();
			}
		});
	}
	
	@Override
	public void onUpdate(String _message) {
		final String message = _message;
		runOnUiThread(new Runnable() {
			@Override
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
						GlobalValues.userID = u.getID();
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
			
		} /*else if(v.getId() == R.id.sync_summary) {
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
			Intent i = new Intent(this, ClientSummaryActivity.class);
			startActivity(i);
		}*/
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
					
					lockScreenOrientation();
				}
			}
		}
	}
	
	private class SummaryClientListener implements ClientThreadListener {

		@Override
		public void onSuccessfulSync() {
			
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					progressDialog.dismiss();
					summaryConnection.closeBTConnection();
					
					try{
						unbindService(summaryConnection);
					} catch(IllegalArgumentException e) {}
					
					Toast.makeText(getApplicationContext(), 
							"Sync successful.", Toast.LENGTH_SHORT).show();
					
					Intent i = new Intent(MainActivity.this, ClientSummaryActivity.class);
					startActivity(i);
					
					releaseScreenOrientation();
				}
			});
		}

		@Override
		public void onUnsuccessfulSync(String _errorMessage) {
			final String errorMessage = _errorMessage;
			
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					summaryConnection.closeBTConnection();
					
					try {
						unbindService(summaryConnection);
					} catch(IllegalArgumentException e) {}
					
					progressDialog.dismiss();
					Toast.makeText(getApplicationContext(), "Sync unsuccessful. " + 
							errorMessage + ".", Toast.LENGTH_SHORT).show();
					
					releaseScreenOrientation();
				}
			});
		}

		@Override
		public void onUpdate(String _message) {
			final String message = _message;
			
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					Toast.makeText(getApplicationContext(), message, 
							Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
	
	@SuppressLint("InlinedApi")
	private void lockScreenOrientation() {
		int currentOrientation = getResources().getConfiguration().orientation;
		if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
			else
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		else {
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
			else
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}
	
	private void releaseScreenOrientation() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
	}
}
