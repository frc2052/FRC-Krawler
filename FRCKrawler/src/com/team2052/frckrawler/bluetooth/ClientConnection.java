package com.team2052.frckrawler.bluetooth;

import android.content.ServiceConnection;

public interface ClientConnection extends ServiceConnection {
	
	public void closeBTConnection();
}
