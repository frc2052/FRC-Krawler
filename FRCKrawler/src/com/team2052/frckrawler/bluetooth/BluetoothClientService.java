package com.team2052.frckrawler.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.DriverData;
import com.team2052.frckrawler.database.structures.Event;
import com.team2052.frckrawler.database.structures.MatchData;
import com.team2052.frckrawler.database.structures.Metric;
import com.team2052.frckrawler.database.structures.Robot;
import com.team2052.frckrawler.database.structures.User;
import com.team2052.frckrawler.io.ObjectArrayInputStream;
import com.team2052.frckrawler.io.ObjectArrayOutputStream;

public class BluetoothClientService extends Service {
	
	public static final String SERVER_MAC_ADDRESS = 
			"com.team2052.frckrawler.bluetooth.macAddress";
	
	private static boolean isRunning = false;
	
	private BluetoothClientThread clientThread;
	
	public void onCreate() {
		
		super.onCreate();
		Log.d("FRCKrawler", "Server service created.");
	}
	
	public int onStartCommand(Intent i, int flags, int startId) {
		
		Log.d("FRCKrawler", "Service started.");
		
		if(clientThread == null || !clientThread.isAlive()) {
			
			BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().
					getRemoteDevice(i.getStringExtra(SERVER_MAC_ADDRESS));
			clientThread = new BluetoothClientThread(this, device);
			clientThread.start();
			
			Toast.makeText(this, "Sync started.", Toast.LENGTH_SHORT).show();
		}
		
		return START_STICKY;
	}
	
	public void onDestroy() {
		
		clientThread.closeClient();
		
		Log.d("FRCKrawler", "Client service destroyed.");
	}
	
	public IBinder onBind(Intent i) {
		
		return new ClientBinder();
	}
	
	public static boolean isRunning() {
		
		return isRunning;
	}
	
	
	/*****
	 * Class: ClientBinder
	 * 
	 * Summary: an interface for 
	 *****/
	
	public class ClientBinder extends Binder {

		public boolean isSyncCompleted() {
			
			return clientThread.isAlive();
		}
	}
	
	
	/*****
	 * Class: BluetoothServerThread
	 * 
	 * Summary: The worker thread for the bluetooth server. This thread
	 * does handles almost everything related to the server.
	 *****/
	
	private class BluetoothClientThread extends Thread {
		
		private Context context;
		private DBManager dbManager;
		private BluetoothDevice serverDevice;
		private BluetoothSocket serverSocket;
		
		public BluetoothClientThread(Context _context, BluetoothDevice _serverDevice) {
			
			context = _context;
			dbManager = DBManager.getInstance(context);
			serverDevice = _serverDevice;
		}
		
		public void run() {
			
			Log.d("FRCKrawler", "Client thread started.");
				
			try {
				
				//Open the socket
				BluetoothSocket serverSocket = serverDevice.
						createRfcommSocketToServiceRecord
							(UUID.fromString(BluetoothInfo.UUID));
				serverSocket.connect();
				
				Log.d("FRCKrawler", "Connected.");
				
				//Open the streams
				InputStream inStream = serverSocket.getInputStream();
				OutputStream outStream = serverSocket.getOutputStream();
				ObjectArrayOutputStream oStream = 
						new ObjectArrayOutputStream(outStream);
				
				//Start the reading thread
				ScoutDataReader reader = new ScoutDataReader(context, inStream);
				reader.start();
				
				oStream.write(new Robot[0]);
				oStream.write(new DriverData[0]);
				oStream.write(new MatchData[0]);
				
				Log.d("FRCKrawler", "Wrote data.");
				
				while(!reader.isFinished()) {
					try {
						Thread.sleep(10);
					} catch(InterruptedException e) {}
				}
				
				Log.d("FRCKrawler", "Read data.");
				
				oStream.close();
				serverSocket.close();
				
				Log.d("FRCKrawler", "Sync complete");
					
			} catch (IOException e) {
				e.printStackTrace();
				Log.d("FRCKrawler", "Sync unsuccessful");
			}
		}
		
		public void closeClient() {
			
			if(serverSocket != null)
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	
	/*****
	 * Class: ScoutDataReader
	 * 
	 * Summary: writes the scouting data to the given stream when run.
	 */
	
	private class ScoutDataReader extends Thread {
		
		private boolean isFinished;
		private InputStream iStream;
		private DBManager dbManager;
		
		public ScoutDataReader(Context _context, InputStream _iStream) {
			
			isFinished = false;
			iStream = _iStream;
			dbManager = DBManager.getInstance(_context);
		}
		
		public void run() {
			
			Event inEvent = null;
			User[] inUsers = new User[0];
			Robot[] inRobots = new Robot[0];
			Metric[] inRobotMetrics = new Metric[0];
			Metric[] inMatchMetrics = new Metric[0];
			Metric[] inDriverMetrics = new Metric[0];
			
			try{
				
				ObjectInputStream ioStream = new ObjectInputStream(iStream);
				
				inEvent = (Event)ioStream.readObject();
				
				/*ObjectArrayInputStream arrInStream = 
						new ObjectArrayInputStream(ioStream);
				
				inUsers = arrInStream.readObjectArray(User.class);
				inRobots = arrInStream.readObjectArray(Robot.class);
				inRobotMetrics = arrInStream.readObjectArray(Metric.class);
				inMatchMetrics = arrInStream.readObjectArray(Metric.class);
				inDriverMetrics = arrInStream.readObjectArray(Metric.class);
				
				arrInStream.close();
				
				//Write the received arrays to the database
				dbManager.scoutUpdateEvent(inEvent);
				dbManager.scoutUpdateUsers(inUsers);*/
				
				Log.d("FRCKrawler", inEvent.getEventName());
				
			} catch (ClassCastException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			isFinished = true;
		}
		
		public boolean isFinished() {
			return isFinished;
		}
	}
}

