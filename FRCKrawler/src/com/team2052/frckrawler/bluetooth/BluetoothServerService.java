package com.team2052.frckrawler.bluetooth;

import java.io.*;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.*;
import android.content.*;
import android.os.*;
import android.widget.Toast;

import com.team2052.frckrawler.database.*;
import com.team2052.frckrawler.database.structures.*;
import com.team2052.frckrawler.io.*;

public class BluetoothServerService extends Service {
	
	public static final String HOSTED_EVENT_ID_EXTRA = 
			"com.team2052.frckrawler.bluetooth.eventIDExtra";
	
	private static boolean isRunning = false;
	private static Event hostedEvent;
	
	private BluetoothServerThread serverThread;
	
	public void onCreate() {
		
		super.onCreate();
		
		serverThread = new BluetoothServerThread(getApplicationContext());
	}
	
	public int onStartCommand(Intent i, int flags, int startId) {
		
		Event[] eventArr = DBManager.getInstance(this).getEventsByColumns
			(new String[] {DBContract.COL_EVENT_ID}, 
				new String[] {Integer.toString(i.getIntExtra(HOSTED_EVENT_ID_EXTRA, -1))});
		
		if(eventArr != null && eventArr.length > 0) {
			
			hostedEvent = eventArr[0];
			
			if(!serverThread.isAlive())
				serverThread.start();
			
			isRunning = true;
			
			return START_STICKY;
			
		} else {
			
			stopSelf();
			
			return START_NOT_STICKY;
		}
	}
	
	public void onDestroy() {
		
		serverThread.closeServer();
		
		System.out.println("Bluetooth service destroyed.");
	}
	
	public IBinder onBind(Intent i) {
		
		return new CloseBinder(this);
	}
	
	public void closeServer() {
		
		isRunning = false;
		stopSelf();
	}
	
	public static Event getHostedEvent() {
		
		return hostedEvent;
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
	
	private class BluetoothServerThread extends Thread {
		
		private static final int TIMEOUT_TIME = 1000;
		
		private volatile boolean isActive;
		
		private Context context;
		private DBManager dbManager;
		
		public BluetoothServerThread(Context _context) {
			
			isActive = false;
			context = _context;
			dbManager = DBManager.getInstance(context);
		}
		
		public void run() {
			
			Looper.prepare();
			
			isActive = true;
			
			if(hostedEvent == null)
				return;
			
			System.out.println("Bluetooth server thread started.");
			
			while(isActive) {
				
				System.out.println("Starting new server cycle.");
				
				//Get the device and see if it is activated
				BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
				
				if(adapter == null) {
					Toast.makeText(context, "Your device does not support Bluetooth.", 
							Toast.LENGTH_LONG).show();
					break;
				}
				
				if(!adapter.enable()) {
					
					Toast.makeText(context, "Please enable Bluetooth.", 
							Toast.LENGTH_LONG).show();
					break;
				}
				
				//Compile the data to send
				User[] users = dbManager.getAllUsers();
				Robot[] robots = dbManager.getRobotsAtEvent(hostedEvent.getEventID());
				Metric[] rMetrics = dbManager.getRobotMetricsByColumns
						(new String[] {DBContract.COL_GAME_NAME}, 
							new String[] {hostedEvent.getGameName()});
				Metric[] mMetrics = dbManager.getMatchPerformanceMetricsByColumns
						(new String[] {DBContract.COL_GAME_NAME}, 
								new String[] {hostedEvent.getGameName()});
				/*Metric[] dMetrics = dbManager.getDriverMetricsByColumns
						(new String[] {DBContract.COL_GAME_NAME}, 
								new String[] {hostedEvent.getGameName()});*/
				
				
				try {
					
					BluetoothServerSocket serverSocket = 
							adapter.listenUsingRfcommWithServiceRecord
							(BluetoothInfo.SERVICE_NAME, UUID.fromString(BluetoothInfo.UUID));
					BluetoothSocket clientSocket = serverSocket.accept(TIMEOUT_TIME);
					serverSocket.close();
					
					
					//Receive any updates
					InputStream inputStream = clientSocket.getInputStream();
					ObjectArrayInputStream iStream = 
							new ObjectArrayInputStream(inputStream);
					
					Robot[] inRobots = new Robot[0];
					DriverData[] inDriverData = new DriverData[0];
					MatchData[] inMatchData = new MatchData[0];
					
					try {
						
						inRobots = iStream.readObjectArray(Robot.class);
						inDriverData = iStream.readObjectArray(DriverData.class);
						inMatchData = iStream.readObjectArray(MatchData.class);
						
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					
					//Send the event object
					OutputStream outputStream = clientSocket.getOutputStream();
					ObjectOutputStream oStream = new ObjectOutputStream(outputStream);
					
					oStream.writeObject(hostedEvent);
					
					ObjectArrayOutputStream arrStream = 
							new ObjectArrayOutputStream(oStream);
					
					arrStream.write(users);
					arrStream.write(robots);
					arrStream.write(rMetrics);
					arrStream.write(mMetrics);
					//arrStream.write(dMetrics);
					
					//Release resources
					clientSocket.close();
					iStream.close();
					arrStream.close();
					
					//Send the received data to the database
					dbManager.updateRobots(inRobots);
					
					for(int i = 0; i < inDriverData.length; i++) {
						//dbManager.insertDriverData(inDriverData[i]);
					}
					
					for(int i = 0; i < inMatchData.length; i++) {
						dbManager.insertMatchData(inMatchData[i]);
					}
					
					System.out.println("Finished server cycle.");
					
				} catch (IOException e) {
					
					System.out.println("Bluetooth timed out.");
				}
				
				try {
					Thread.sleep(100);
				} catch(InterruptedException e) {
					stopSelf();
				}
			}
		}
		
		public void closeServer() {
			
			isActive = false;
		}
	}
	
	public static boolean isRunning() {
		
		return isRunning;
	}
}
