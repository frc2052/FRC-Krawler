package com.team2052.frckrawler;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.bluetooth.BluetoothClientService;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class ClientTestActivity extends Activity implements View.OnClickListener, 
														DialogInterface.OnClickListener {
	
	private static final int REQUEST_BT_ENABLE = 1;
	
	private BluetoothDevice[] devices;
	private int selectedDeviceAddress;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_bluetooth_client_activity);
		
		findViewById(R.id.sync).setOnClickListener(this);
	}

	public void onClick(View v) {
		
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

	public void onClick(DialogInterface dialog, int which) {
		
		selectedDeviceAddress = which;
		
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		
		if(adapter == null) {
			Log.d("FRCKrawler", "This device does not support Bluetooth.");
		}
		
		if (!adapter.isEnabled()) {
		    Intent enableBtIntent = 
		    		new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_BT_ENABLE);
		    
		} else {
			
			Intent i = new Intent(this, BluetoothClientService.class);
			i.putExtra(BluetoothClientService.SERVER_MAC_ADDRESS, 
					devices[which].getAddress());
			startService(i);
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(requestCode == REQUEST_BT_ENABLE && resultCode == RESULT_OK) {
			
			Intent i = new Intent(this, BluetoothClientService.class);
			i.putExtra(BluetoothClientService.SERVER_MAC_ADDRESS, 
					devices[selectedDeviceAddress].getAddress());
			startService(i);
		}
	}
}
