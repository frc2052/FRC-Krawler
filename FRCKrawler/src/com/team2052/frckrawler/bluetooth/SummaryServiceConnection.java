package com.team2052.frckrawler.bluetooth;

import android.content.ComponentName;
import android.os.IBinder;

import com.team2052.frckrawler.bluetooth.BluetoothSummaryClientService.SummaryBinder;

public class SummaryServiceConnection implements ClientConnection {
	
	private ClientThreadListener listener;
	private SummaryBinder binder;
	
	private SummaryServiceConnection() {}
	
	public SummaryServiceConnection(ClientThreadListener _listener) {
		listener = _listener;
	}

	@Override
	public void onServiceConnected(ComponentName c, IBinder i) {
		binder = (SummaryBinder)i;
		binder.setListener(listener);
	}

	@Override
	public void onServiceDisconnected(ComponentName i) {
		closeBTConnection();
	}
	
	@Override
	public void closeBTConnection() {
		if(binder != null)
			binder.closeConnection();
	}
}
