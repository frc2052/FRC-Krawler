package com.team2052.frckrawler;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.team2052.frckrawler.bluetooth.BluetoothClientService;
import com.team2052.frckrawler.bluetooth.ClientServiceConnection;
import com.team2052.frckrawler.bluetooth.ClientThreadListener;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.User;
import com.team2052.frckrawler.gui.ProgressSpinner;

public class MainActivity extends Activity implements DialogInterface.OnClickListener, 
																ClientThreadListener{
	
	Context context = this;
	
	private static final String SUPERUSER_NAME = "admin";
	private static final int REQUEST_BT_ENABLE = 1;
	
	private BluetoothDevice[] devices;
	private int selectedDeviceAddress;
	private AlertDialog progressDialog;
	private ClientServiceConnection connection;
	private EditText scoutLoginName;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        connection = new ClientServiceConnection(this);
    }
    
    public void onDestroy() {
		super.onDestroy();
		
		try {
			connection.closeBTConnection();
			unbindService(connection);
		} catch(IllegalArgumentException e) {}
		
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
    	
    	LinearLayout layout = new LinearLayout(this);
    	
    	final AlertDialog.Builder login = new AlertDialog.Builder(this);
    	login
    		.setTitle("Login")
    		.setMessage("Please Enter Superuser Credentials")
    		.setCancelable(false);
    	
    	final EditText username = new EditText(context);
    	username.setWidth(200);
    	username.setHint("Username");
    	layout.addView(username);
    	login.setView(layout);
    	
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
    		.setMessage("Please enter your username.")
    		.setView(name);
    		
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
				
				Intent i = new Intent(this, BluetoothClientService.class);
				i.putExtra(BluetoothClientService.SERVER_MAC_ADDRESS, 
						devices[which].getAddress());
				bindService(i, connection, Context.BIND_AUTO_CREATE);
				GlobalSettings.masterMACAddress = 
						devices[selectedDeviceAddress].getAddress();
			
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Syncing...");
				builder.setView(new ProgressSpinner(this));
				builder.setNeutralButton("Cancel", this);
				progressDialog = builder.create();
				progressDialog.show();
			}
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(requestCode == REQUEST_BT_ENABLE && resultCode == RESULT_OK) {
			
			Intent i = new Intent(this, BluetoothClientService.class);
			i.putExtra(BluetoothClientService.SERVER_MAC_ADDRESS, 
					devices[selectedDeviceAddress].getAddress());
			bindService(i, connection, Context.BIND_AUTO_CREATE);
			GlobalSettings.masterMACAddress = 
					devices[selectedDeviceAddress].getAddress();
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Syncing...");
			builder.setView(new ProgressSpinner(this));
			builder.setNeutralButton("Cancel", this);
			progressDialog = builder.create();
			progressDialog.show();
		}
	}
    
    public void onCreditsClicked(View v) {
    	
    	
    }

	public void onSuccessfulSync() {
		
		runOnUiThread(new Runnable() {
			public void run() {
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
}
