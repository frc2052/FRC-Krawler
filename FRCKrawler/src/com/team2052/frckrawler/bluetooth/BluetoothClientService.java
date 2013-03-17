package com.team2052.frckrawler.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.team2052.frckrawler.database.DBContract;
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
		System.out.println("Server service created.");
	}
	
	public int onStartCommand(Intent i, int flags, int startId) {
		
		System.out.println("Service started.");
		
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
		
		System.out.println("Bluetooth service destroyed.");
	}
	
	public IBinder onBind(Intent i) {
		
		return new ClientBinder();
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
	
	private class BluetoothClientThread extends Thread{
		
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
			
			System.out.println("Client thread started.");
				
			try {
				
				//Open the socket
				BluetoothSocket serverSocket = serverDevice.
						createRfcommSocketToServiceRecord
							(UUID.fromString(BluetoothInfo.UUID));
				/*Method m = serverDevice.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
				BluetoothSocket serverSocket = (BluetoothSocket) m.invoke(serverDevice, 1);*/
				serverSocket.connect();
				
				System.out.println("Connected.");
				
				//Open the outstream and write saved data
				OutputStream outStream = serverSocket.getOutputStream();
				ObjectArrayOutputStream oStream = new ObjectArrayOutputStream(outStream);
				
				System.out.println("Opened out streams.");
				
				oStream.write(new Robot[0]);
				oStream.write(new DriverData[0]);
				oStream.write(new MatchData[0]);
				
				System.out.println("Wrote data.");
				
				//Get any updates
				InputStream inStream = serverSocket.getInputStream();
				ObjectInputStream ioStream = new ObjectInputStream(inStream);
				
				Event inEvent = null;
				User[] inUsers = new User[0];
				Robot[] inRobots = new Robot[0];
				Metric[] inRobotMetrics = new Metric[0];
				Metric[] inMatchMetrics = new Metric[0];
				Metric[] inDriverMetrics = new Metric[0];
				
				try{
					
					inEvent = (Event)ioStream.readObject();
					
					ObjectArrayInputStream arrInStream = 
							new ObjectArrayInputStream(ioStream);
					
					inUsers = arrInStream.readObjectArray(User.class);
					inRobots = arrInStream.readObjectArray(Robot.class);
					inRobotMetrics = arrInStream.readObjectArray(Metric.class);
					inMatchMetrics = arrInStream.readObjectArray(Metric.class);
					inDriverMetrics = arrInStream.readObjectArray(Metric.class);
					
					arrInStream.close();
					
					//Write the received arrays to the database
					dbManager.scoutUpdateEvent(inEvent);
					dbManager.scoutUpdateUsers(inUsers);
					
				} catch (ClassCastException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				
				oStream.close();
				serverSocket.close();
				
				dbManager.printQuery("SELECT * FROM " + DBContract.SCOUT_TABLE_EVENT, null);
				dbManager.printQuery("SELECT * FROM " + DBContract.SCOUT_TABLE_USERS, null);
				
				System.out.println("Sync complete");
					
			} catch (IOException e) {
				
				e.printStackTrace();
				System.out.println("Sync unsuccessful");
			} /*catch (NoSuchMethodException e1) {
				e1.printStackTrace();
				System.out.println("Sync unsuccessful");
			} catch (IllegalArgumentException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
		}
		
		public void closeClient() {
			
			if(serverSocket != null)
				try {
					serverSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	public static boolean isRunning() {
		
		return isRunning;
	}
}

