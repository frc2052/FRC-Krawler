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
import android.util.SparseIntArray;

import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.CompiledData;
import com.team2052.frckrawler.database.structures.Event;
import com.team2052.frckrawler.database.structures.MatchData;
import com.team2052.frckrawler.database.structures.Metric;
import com.team2052.frckrawler.database.structures.Query;
import com.team2052.frckrawler.database.structures.Robot;
import com.team2052.frckrawler.database.structures.Team;
import com.team2052.frckrawler.database.structures.User;

public class BluetoothServerService extends Service {
	
	public static final String HOSTED_EVENT_ID_EXTRA = 
			"com.team2052.frckrawler.bluetooth.eventIDExtra";
	
	private static boolean isRunning = false;
	private static Event hostedEvent;
	
	private BluetoothServerThread serverThread;
	
	@Override
	public void onCreate() {
		super.onCreate();
		serverThread = new BluetoothServerThread(getApplicationContext());
	}
	
	@Override
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
	
	@Override
	public void onDestroy() {
		serverThread.closeServer();
	}
	
	@Override
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
		
		@Override
		public void run() {
			isActive = true;
			
			if(hostedEvent == null)
				return;
			
			while(isActive) {
				ServerDataReader reader = null;
				
				try {
					
					BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
					
					serverSocket = adapter.listenUsingRfcommWithServiceRecord
							(BluetoothInfo.SERVICE_NAME, UUID.fromString(BluetoothInfo.UUID));
					clientSocket = serverSocket.accept();
					serverSocket.close();
					
					//Create our streams
					OutputStream outputStream = clientSocket.getOutputStream();
					ObjectOutputStream oStream = new ObjectOutputStream(outputStream);
					InputStream inputStream = clientSocket.getInputStream();
					
					//Start the reader
					reader = new ServerDataReader(context, inputStream);
					reader.start();
					
					while(!reader.isFinished()) {
						try {
							Thread.sleep(10);
						} catch(InterruptedException e) {}
					}
					
					int connectionType = reader.getConnectionType();
					
					if(connectionType == BluetoothInfo.SCOUT) {
						
						//Compile the data to send
						User[] usersArr = dbManager.getAllUsers();
					
						Robot[] robotsArr = dbManager.getRobotsAtEvent
								(hostedEvent.getEventID());
						
						String[] teamNumbers = new String[robotsArr.length];
						String[] colStrings = new String[robotsArr.length];
						for(int i = 0; i < teamNumbers.length; i++) {
							teamNumbers[i] = Integer.toString(robotsArr[i].getTeamNumber());
							colStrings[i] = DBContract.COL_TEAM_NUMBER;
						}
					
						Team[] teams = dbManager.getTeamsByColumns(colStrings, teamNumbers, true);
						String[] teamNames = new String[teams.length];
						for(int i = 0; i < teams.length; i++) {
							teamNames[i] = teams[i].getName();
						}
					
						Metric[] rMetricsArr = dbManager.getRobotMetricsByColumns
								(new String[] {DBContract.COL_GAME_NAME}, 
									new String[] {hostedEvent.getGameName()});
						
						Metric[] mMetricsArr = dbManager.getMatchPerformanceMetricsByColumns
								(new String[] {DBContract.COL_GAME_NAME}, 
										new String[] {hostedEvent.getGameName()});
					
						
						//Send data
						oStream.writeObject(hostedEvent);
						oStream.writeObject(usersArr);
						oStream.writeObject(teamNames);
						oStream.writeObject(robotsArr);
						oStream.writeObject(rMetricsArr);
						oStream.writeObject(mMetricsArr);
						oStream.flush();
						
					} else if(connectionType == BluetoothInfo.SUMMARY) {
						
						//Compile the SummaryData
						CompiledData[] compiledData = dbManager.getCompiledEventData
								(hostedEvent, new Query[0], null);
						MatchData[] matchData = dbManager.getMatchDataByColumns
								(new String[] {DBContract.COL_EVENT_ID}, 
										new String[] {Integer.toString(hostedEvent.getEventID())});
						
						//Map for the number of matches a team has loaded into the array 
						//		to be sent to the client
						SparseIntArray loadedMatchCount = new SparseIntArray();
						ArrayList<MatchData> minMatchData = new ArrayList<MatchData>();
						Robot[] robots = new Robot[compiledData.length];
						for(int i = 0; i < robots.length; i++)
							robots[i] = compiledData[i].getRobot();
						
						for(Robot r : robots)
							loadedMatchCount.put(r.getID(), 0);
						
						for(int i = matchData.length - 1; i >= 0; i--) {
							if(loadedMatchCount.get(matchData[i].getRobotID()) < 3) {
								minMatchData.add(matchData[i]);
								loadedMatchCount.put(matchData[i].getRobotID(), 
										loadedMatchCount.get(matchData[i].getRobotID()) + 1);
							}
						}
						
						oStream.writeObject(hostedEvent);
						oStream.writeObject(compiledData);
						oStream.writeObject(minMatchData.toArray(new MatchData[0]));
						oStream.flush();
					}
					
					oStream.close();
					outputStream.close();
					inputStream.close();
					clientSocket.close();
					
				} catch (IOException e) {
					if(reader != null)
						reader.close();
				} finally {
					if(reader != null)
						reader.close();
				}
			}
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
		
		private int connectionType;
		private boolean isFinished;
		private InputStream iStream;
		private DBManager dbManager;
		
		public ServerDataReader(Context _context, InputStream _iStream) {
			
			connectionType = 0;
			isFinished = false;
			iStream = _iStream;
			dbManager = DBManager.getInstance(_context);
		}
		
		@Override
		public void run() {
			
			Event inEvent = null;
			Robot[] inRobots = new Robot[0];
			MatchData[] inMatchData = new MatchData[0];
			
			try {
				
				ObjectInputStream inStream = new ObjectInputStream(iStream);
				
				connectionType = inStream.readInt();
				
				if(connectionType == BluetoothInfo.SCOUT) {
					inEvent = (Event)inStream.readObject();
					inRobots = (Robot[])inStream.readObject();
					inMatchData = (MatchData[])inStream.readObject();
					
					//Send the received data to the database
					if(inEvent != null && inEvent.getEventID() == 
							hostedEvent.getEventID()) {
						
						dbManager.updateRobots(inRobots);
				
						for(MatchData m : inMatchData)
							dbManager.insertMatchData(m);
					}
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
		
		public int getConnectionType() {
			return connectionType;
		}
	}
}
