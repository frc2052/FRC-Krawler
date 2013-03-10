package com.team2052.frckrawler;

import com.example.frckrawler.R;
import com.team2052.frckrawler.bluetooth.BluetoothClientService;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class ClientTestActivity extends Activity implements View.OnClickListener, 
														DialogInterface.OnClickListener {
	
	private BluetoothDevice[] devices;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_bluetooth_client_activity);
		
		findViewById(R.id.sync).setOnClickListener(this);
	}

	public void onClick(View v) {
		
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
		
		Intent i = new Intent(this, BluetoothClientService.class);
		i.putExtra(BluetoothClientService.SERVER_MAC_ADDRESS, devices[which].getAddress());
		startService(i);
		
		System.out.println("Button pressed.");
	}
}
