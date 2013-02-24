package com.team2052.frckrawler.bluetooth;

import android.app.Service;
import android.bluetooth.*;
import android.content.Intent;
import android.os.*;

public class BluetoothServerService extends Service {
	
	private BluetoothServerThread serverThread;
	
	public void onCreate() {
		
		serverThread = new BluetoothServerThread();
	}
	
	public int onStartCommand(Intent i, int flags, int startId) {
		
		serverThread.start();
		
		System.out.println("Bluetoother service started.");
		
		return START_STICKY;
	}
	
	public void onDestroy() {
		
		serverThread.closeServer();
		
		System.out.println("Bluetooth service destroyed.");
	}
	
	public IBinder onBind(Intent i) {
		
		return new CloseBinder(this);
	}
	
	public void closeServer() {
		
		stopSelf();
	}
	
	
	/*****
	 * Class: CloseBinder
	 * 
	 * Summary: Provides a way for another component to close the bluetooth
	 * server.  NOTE: before the server's resources are released and 
	 * bluetooth communications actually stop, the server finishes up whatever
	 * client it is communicating with.
	 *****/
	
	public class CloseBinder extends Binder {
		
		private BluetoothServerService service;
		
		public CloseBinder(BluetoothServerService s) {
			
			service = s;
		}
		
		public void closeServer() {
			
			service.closeServer();
		}
	}
	
	/*****
	 * Class: BluetoothServerThread
	 * 
	 * Summary: The worker thread for the bluetooth server. This thread
	 * does handles almost everything related to the server.
	 *****/
	
	private class BluetoothServerThread extends Thread{
		
		private volatile boolean isActive;
		
		public BluetoothServerThread() {
			
			isActive = false;
		}
		
		public void run() {
			
			isActive = true;
			
			System.out.println("Bluetooth thread started.");
			
			while(isActive) {
				
				System.out.println("Bluetooth thread running...");
				
				//BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
			}
			
			System.out.println("Bluetooth thread stopped.");
		}
		
		public void closeServer() {
			
			isActive = false;
		}
	}
}
