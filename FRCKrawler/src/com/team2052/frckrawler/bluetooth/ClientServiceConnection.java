package com.team2052.frckrawler.bluetooth;

import com.team2052.frckrawler.bluetooth.BluetoothClientService.ClientBinder;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class ClientServiceConnection implements ServiceConnection {
	
	private ClientThreadListener listener;
	private ClientBinder binder;
	
	private ClientServiceConnection() {}
	
	public ClientServiceConnection(ClientThreadListener _listener) {
		listener = _listener;
	}

	public void onServiceConnected(ComponentName c, IBinder i) {
		binder = (ClientBinder)i;
		binder.setListener(listener);
	}

	public void onServiceDisconnected(ComponentName i) {
		closeBTConnection();
	}
	
	public void closeBTConnection() {
		if(binder != null)
			binder.closeConnection();
	}
}
