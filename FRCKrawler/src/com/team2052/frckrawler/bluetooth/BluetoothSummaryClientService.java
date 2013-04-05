package com.team2052.frckrawler.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.team2052.frckrawler.database.structures.Event;
import com.team2052.frckrawler.database.structures.SummaryData;

public class BluetoothSummaryClientService extends Service implements ClientThreadListener {
	
	public static final String SERVER_MAC_ADDRESS = 
			"com.team2052.frckrawler.bluetooth.macAddress";
	
	private BluetoothSummaryClientThread clientThread;
	private SummaryBinder binder;
	
	public void onCreate() {
		
		super.onCreate();
		Log.d("FRCKrawler", "Service created.");
	}

	@Override
	public IBinder onBind(Intent i) {
		
		binder = new SummaryBinder();
		BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().
				getRemoteDevice(i.getStringExtra(SERVER_MAC_ADDRESS));
		clientThread = new BluetoothSummaryClientThread(device, this);
		clientThread.start();
		
		return binder;
	}
	
	private void close() {
		clientThread.cancel();
	}
	
	@Override
	public void onSuccessfulSync() {
		if(binder != null && binder.getListener() != null)
			binder.getListener().onSuccessfulSync();
	}

	@Override
	public void onUnsuccessfulSync(String errorMessage) {
		if(binder != null && binder.getListener() != null)
			binder.getListener().onUnsuccessfulSync(errorMessage);
	}

	@Override
	public void onUpdate(String message) {
		if(binder != null && binder.getListener() != null)
			binder.getListener().onUpdate(message);
	}
	
	
	/*****
	 * Class: SummaryBinder
	 * 
	 * @author Charles Hofer
	 *
	 * Description: provides an interface for clients
	 * to interact with this Service
	 */
	
	public class SummaryBinder extends Binder {

		private ClientThreadListener listener;
		
		public void setListener(ClientThreadListener _listener) {
			listener = _listener;
		}
		
		public ClientThreadListener getListener() {
			return listener;
		}
		
		public void closeConnection() {
			close();
		}
	}
	
	
	/*****
	 * Class: BluetoothSummaryClientThread
	 * 
	 * @author Charles Hofer
	 * 
	 * Description: the worker thread for the summary client service. This
	 * handles most database and Bluetooth operations.
	 */
	
	private class BluetoothSummaryClientThread extends Thread {
		
		private BluetoothDevice serverDevice;
		private ClientThreadListener threadListener;
		private BluetoothSocket serverSocket;
		
		public BluetoothSummaryClientThread(BluetoothDevice _serverDevice, 
				ClientThreadListener _listener) {
			
			serverDevice = _serverDevice;
			threadListener = _listener;
		}
		
		public void run() {
			
			try {
				
				//Open the socket
				serverSocket = serverDevice.
						createRfcommSocketToServiceRecord
							(UUID.fromString(BluetoothInfo.ClientServerUUID));
				serverSocket.connect();
				
				if(threadListener != null)
					threadListener.onUpdate("Connected");
				
				//Open the streams
				InputStream inStream = serverSocket.getInputStream();
				ObjectInputStream ioStream = new ObjectInputStream(inStream);
				
				//Get the data from the server
				Event inEvent = null;
				ArrayList<SummaryData> summaryData = new ArrayList<SummaryData>();
				
				inEvent = (Event)ioStream.readObject();
				summaryData = (ArrayList<SummaryData>)ioStream.readObject();
				
				//Close the streams
				ioStream.close();
				inStream.close();
				serverSocket.close();
				
				//Write the data to the database
				
				
			} catch(IOException e) {
				if(threadListener != null)
					threadListener.onUnsuccessfulSync(e.getMessage());
			} catch (ClassNotFoundException e) {
				if(threadListener != null)
					threadListener.onUnsuccessfulSync(e.getMessage());
			}
		}
		
		public void cancel() {
			try {
				serverSocket.close();
			} catch(IOException e) {
				Log.e("FRCKrawler", "IOException in closing client socket. " 
						+ e.getMessage());
			} catch(NullPointerException e) {}
		}
	}
}
