package com.team2052.frckrawler.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.DriverData;
import com.team2052.frckrawler.database.structures.Event;
import com.team2052.frckrawler.database.structures.MatchData;
import com.team2052.frckrawler.database.structures.Metric;
import com.team2052.frckrawler.database.structures.Robot;
import com.team2052.frckrawler.database.structures.Team;
import com.team2052.frckrawler.database.structures.User;

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
				new String[] {Integer.toString
					(i.getIntExtra(HOSTED_EVENT_ID_EXTRA, -1))});
		
		if(eventArr != null && eventArr.length > 0) {
			hostedEvent = eventArr[0];
			
			if(!serverThread.isAlive())
				serverThread.start();
			
			isRunning = true;
		}
			
		return START_REDELIVER_INTENT;
	}
	
	public void onDestroy() {
		serverThread.closeServer();
		Log.d("FRCKrawler", "Server service destroyed.");
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
	
	public static boolean isRunning() {
		return isRunning;
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
		
		private volatile boolean isActive;
		
		private Context context;
		private DBManager dbManager;
		private BluetoothServerSocket serverSocket;
		private BluetoothSocket clientSocket;
		
		public BluetoothServerThread(Context _context) {
			isActive = false;
			context = _context;
			dbManager = DBManager.getInstance(context);
		}
		
		public void run() {
			isActive = true;
			
			if(hostedEvent == null)
				return;
			
			Log.d("FRCKrawler", "Bluetooth server thread started.");
			
			while(isActive) {
				
				Log.d("FRCKrawler", "Starting new server cycle.");
				
				ServerDataReader reader = null;
				
				try {
					
					BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
					
					serverSocket = adapter.listenUsingRfcommWithServiceRecord
							(BluetoothInfo.SERVICE_NAME, UUID.fromString(BluetoothInfo.UUID));
					clientSocket = serverSocket.accept();
					serverSocket.close();
					
					
					//Compile the data to send
					User[] usersArr = dbManager.getAllUsers();
					ArrayList<User> users = new ArrayList<User>();
					for(User u : usersArr)
						users.add(u);
					
					Robot[] robotsArr = dbManager.getRobotsAtEvent
							(hostedEvent.getEventID());
					ArrayList<Robot> robots = new ArrayList<Robot>();
					for(Robot r : robotsArr)
						robots.add(r);
					
					String[] teamNumbers = new String[robotsArr.length];
					String[] colStrings = new String[robotsArr.length];
					for(int i = 0; i < teamNumbers.length; i++) {
						teamNumbers[i] = Integer.toString(robotsArr[i].getTeamNumber());
						colStrings[i] = DBContract.COL_TEAM_NUMBER;
					}
					
					Team[] teams = dbManager.getTeamsByColumns(colStrings, teamNumbers, true);
					ArrayList<String> teamNames = new ArrayList<String>();
					for(Team t : teams) {
						teamNames.add(t.getName());
						Log.d("FRCKrawler", t.getName());
					}
					
					Metric[] rMetricsArr = dbManager.getRobotMetricsByColumns
							(new String[] {DBContract.COL_GAME_NAME}, 
								new String[] {hostedEvent.getGameName()});
					ArrayList<Metric> rMetrics = new ArrayList<Metric>();
					for(Metric m : rMetricsArr)
						rMetrics.add(m);
					
					Metric[] mMetricsArr = dbManager.getMatchPerformanceMetricsByColumns
							(new String[] {DBContract.COL_GAME_NAME}, 
									new String[] {hostedEvent.getGameName()});
					ArrayList<Metric> mMetrics = new ArrayList<Metric>();
					for(Metric m : mMetricsArr)
						mMetrics.add(m);
					
					Metric[] dMetricsArr = new Metric[0];
					ArrayList<Metric> dMetrics = new ArrayList<Metric>();
					for(Metric m : dMetricsArr)
						dMetrics.add(m);
					
					
					//Create our streams
					OutputStream outputStream = clientSocket.getOutputStream();
					ObjectOutputStream oStream = new ObjectOutputStream(outputStream);
					InputStream inputStream = clientSocket.getInputStream();
					
					//Send data
					oStream.writeObject(hostedEvent);
					oStream.writeObject(users);
					oStream.writeObject(teamNames);
					oStream.writeObject(robots);
					oStream.writeObject(rMetrics);
					oStream.writeObject(mMetrics);
					//arrStream.write(dMetrics);
					
					reader = new ServerDataReader(context, inputStream);
					reader.start();
					
					while(!reader.isFinished()) {
						try {
							Thread.sleep(10);
						} catch(InterruptedException e) {}
					}
					
					inputStream.close();
					clientSocket.close();
					
					Log.d("FRCKrawler", "Successful sync. Finished server cycle.");
					
				} catch (IOException e) {
					if(reader != null)
						reader.close();
					Log.d("FRCKrawler", e.getMessage());
				} finally {
					if(reader != null)
						reader.close();
				}
			}
			
			Log.d("FRCKrawler", "Server thread killed.");
		}
		
		public void closeServer() {
			try {
				serverSocket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
				
			try {
				clientSocket.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			isActive = false;
		}
	}
	
	
	/*****
	 * Class: ServerDataReader
	 * 
	 * @author Charles Hofer
	 *
	 * Summary: Reads scouting data from the supplied InputStream
	 * and puts the scout's data in the database.
	 */
	
	private class ServerDataReader extends Thread {
		
		private boolean isFinished;
		private InputStream iStream;
		private DBManager dbManager;
		
		public ServerDataReader(Context _context, InputStream _iStream) {
			
			isFinished = false;
			iStream = _iStream;
			dbManager = DBManager.getInstance(_context);
		}
		
		public void run() {
			
			Event inEvent = null;
			ArrayList<Robot> inRobots = new ArrayList<Robot>();
			ArrayList<DriverData> inDriverData = new ArrayList<DriverData>();
			ArrayList<MatchData> inMatchData = new ArrayList<MatchData>();
			
			try {
				
				ObjectInputStream inStream = new ObjectInputStream(iStream);
				
				inEvent = (Event)inStream.readObject();
				inRobots = (ArrayList<Robot>)inStream.readObject();
				inDriverData = (ArrayList<DriverData>)inStream.readObject();
				inMatchData = (ArrayList<MatchData>)inStream.readObject();
				
				//Send the received data to the database
				if(inEvent != null && inEvent.getEventID() == 
						hostedEvent.getEventID()) {
					dbManager.updateRobots(inRobots.toArray(new Robot[0]));
				
					for(MatchData m : inMatchData)
						dbManager.insertMatchData(m);
				}
				
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			isFinished = true;
		}
		
		public boolean isFinished() {
			return isFinished;
		}
		
		public void close() {
			try {
				iStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
