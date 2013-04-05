package com.team2052.frckrawler.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
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
import com.team2052.frckrawler.database.structures.StringSet;
import com.team2052.frckrawler.database.structures.User;

public class BluetoothScoutClientService extends Service 
										implements ClientThreadListener {
	
	public static final String SERVER_MAC_ADDRESS = 
			"com.team2052.frckrawler.bluetooth.macAddress";
	
	private BluetoothClientThread clientThread;
	private ClientBinder binder;
	
	public void onCreate() {
		
		super.onCreate();
		binder = new ClientBinder();
		Log.d("FRCKrawler", "Service created.");
	}
	
	public void onDestroy() {
		
		clientThread.closeClient();
		Log.d("FRCKrawler", "Client service destroyed.");
	}
	
	public IBinder onBind(Intent i) {
		
		Log.d("FRCKrawler", "Service bound");
		
		binder = new ClientBinder();
		
		BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().
				getRemoteDevice(i.getStringExtra(SERVER_MAC_ADDRESS));
		clientThread = new BluetoothClientThread(this, device, this);
		clientThread.start();
			
		Toast.makeText(this, "Sync started.", Toast.LENGTH_SHORT).show();
		
		return binder;
	}
	
	public boolean onUnbind(Intent i) {
		
		binder = null;
		
		return true;
	}
	
	public void close() {
		clientThread.closeClient();
	}
	
	/*
	 * Methods for the ClientThreadListener
	 */
	public void onSuccessfulSync() {
		
		if(binder != null && binder.getListener() != null) 
			binder.getListener().onSuccessfulSync();
	}
	
	public void onUnsuccessfulSync(String errorMessage) {
		
		if(binder != null && binder.getListener() != null) 
			binder.getListener().onUnsuccessfulSync(errorMessage);
	}
	
	public void onUpdate(String message) {
		
		if(binder != null && binder.getListener() != null)
			binder.getListener().onUpdate(message);
	}
	
	
	/*****
	 * Class: ClientBinder
	 * 
	 * Summary: an interface so that clients can interact with the service
	 *****/
	
	public class ClientBinder extends Binder {

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
		private ClientThreadListener threadListener;
		
		public BluetoothClientThread(Context _context, BluetoothDevice _serverDevice, 
				ClientThreadListener _listener) {
			
			context = _context;
			dbManager = DBManager.getInstance(context);
			serverDevice = _serverDevice;
			threadListener = _listener;
		}
		
		public void run() {
			
			Log.d("FRCKrawler", "Client thread started.");
				
			try {
				
				//Open the socket
				BluetoothSocket serverSocket = serverDevice.
						createRfcommSocketToServiceRecord
							(UUID.fromString(BluetoothInfo.ClientServerUUID));
				serverSocket.connect();
				
				if(threadListener != null)
					threadListener.onUpdate("Connected");
				
				//Open the streams
				InputStream inStream = serverSocket.getInputStream();
				OutputStream outStream = serverSocket.getOutputStream();
				ObjectOutputStream ooStream = new ObjectOutputStream(outStream);
				
				if(threadListener != null)
					threadListener.onUpdate("Exchanging data");
				
				//Get the data to send
				Event event = dbManager.scoutGetEvent();
				
				Robot[] robotsArr = dbManager.scoutGetUpdatedRobots();
				ArrayList<Robot> robots = new ArrayList<Robot>();
				for(Robot r : robotsArr)
					robots.add(r);
				
				MatchData[] matchDataArr = dbManager.scoutGetAllMatchData();
				ArrayList<MatchData> matchData = new ArrayList<MatchData>();
				for(MatchData m : matchDataArr)
					matchData.add(m);
				
				//Write the scout data
				ooStream.writeInt(BluetoothInfo.SCOUT);
				ooStream.writeObject(event);
				ooStream.writeObject(robots);
				//ooStream.writeObject(new ArrayList<DriverData>());
				ooStream.writeObject(matchData);
				
				//Clear out the old data after it is sent
				dbManager.scoutClearMatchData();
				//dbManager.scoutClearDriverData();
				
				//Start the reading thread
				ScoutDataReader reader = new ScoutDataReader(context, inStream);
				reader.start();
				
				while(!reader.isFinished()) {
					try {
						Thread.sleep(10);
					} catch(InterruptedException e) {}
				}
				
				ooStream.close();
				outStream.close();
				inStream.close();
				serverSocket.close();
				
				if(threadListener != null)
					threadListener.onSuccessfulSync();
					
			} catch (IOException e) {
				if(threadListener != null)
					threadListener.onUnsuccessfulSync(e.getMessage());
				
				e.printStackTrace();
			}
		}
		
		public void closeClient() {
			
			if(serverSocket != null)
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (NullPointerException e) {}
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
			ArrayList<User> inUsers = new ArrayList<User>();
			ArrayList<String> inTeamNames = new ArrayList<String>();
			ArrayList<Robot> inRobots = new ArrayList<Robot>();
			ArrayList<Metric> inRobotMetrics = new ArrayList<Metric>();
			ArrayList<Metric> inMatchMetrics = new ArrayList<Metric>();
			//ArrayList<Metric> inDriverMetrics = new ArrayList<Metric>();
			
			try{
				
				ObjectInputStream ioStream = new ObjectInputStream(iStream);
				
				inEvent = (Event)ioStream.readObject();
				inUsers = (ArrayList<User>)ioStream.readObject();
				inTeamNames = (ArrayList<String>)ioStream.readObject();
				inRobots = (ArrayList<Robot>)ioStream.readObject();
				inRobotMetrics = (ArrayList<Metric>)ioStream.readObject();
				inMatchMetrics = (ArrayList<Metric>)ioStream.readObject();
				
				//inDriverMetrics = (ArrayList<Metric>)ioStream.readObject();
				
				//Write the received arrays to the database
				dbManager.scoutReplaceEvent(inEvent);
				dbManager.scoutReplaceUsers(inUsers.toArray(new User[0]));
				dbManager.scoutReplaceRobots(inRobots.toArray(new Robot[0]), 
						inTeamNames.toArray(new String[0]));
				dbManager.scoutReplaceRobotMetrics(inRobotMetrics.toArray(new Metric[0]));
				dbManager.scoutReplaceMatchMetrics(inMatchMetrics.toArray(new Metric[0]));
				
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

