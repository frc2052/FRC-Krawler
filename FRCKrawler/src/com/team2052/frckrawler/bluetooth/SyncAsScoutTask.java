package com.team2052.frckrawler.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Event;
import com.team2052.frckrawler.database.structures.MatchData;
import com.team2052.frckrawler.database.structures.Metric;
import com.team2052.frckrawler.database.structures.Robot;
import com.team2052.frckrawler.database.structures.User;

public class SyncAsScoutTask extends AsyncTask<BluetoothDevice, Void, Integer> {
	
	private static int SYNC_SUCCESS = 1;
	private static int SYNC_SERVER_OPEN = 2;
	private static int SYNC_CANCELLED = 3;
	private static int SYNC_ERROR = 4;
	private static int tasksRunning = 0;
	private volatile String deviceName;
	private Context context;
	private DBManager dbManager;
	private SyncCallbackHandler handler;
	
	public SyncAsScoutTask(Context c, SyncCallbackHandler h) {
		deviceName = "device";
		context = c.getApplicationContext();
		dbManager = DBManager.getInstance(context);
		handler = h;
	}
	
	public static boolean isTaskRunning() {
		return tasksRunning > 0;
	}
	
	@Override
	protected void onPreExecute() {
		tasksRunning++;
		handler.onSyncStart("device");
	}

	@Override
	protected Integer doInBackground(BluetoothDevice... dev) {
		deviceName = dev[0].getName();
		if(Server.getInstance(context).isOpen()) 
			return SYNC_SERVER_OPEN;
		try {
			long startTime = System.currentTimeMillis();
			BluetoothSocket serverSocket = dev[0].createRfcommSocketToServiceRecord
					(UUID.fromString(BluetoothInfo.UUID));
			serverSocket.connect();
			
			if(isCancelled())
				return SYNC_CANCELLED;
			
			//Open the streams
			InputStream inStream = serverSocket.getInputStream();
			ObjectInputStream ioStream = new ObjectInputStream(inStream);
			OutputStream outStream = serverSocket.getOutputStream();
			ObjectOutputStream ooStream = new ObjectOutputStream(outStream);
			
			//Get the data to send
			Event event = dbManager.scoutGetEvent();
			Robot[] robotsArr = dbManager.scoutGetUpdatedRobots();
			MatchData[] matchDataArr = dbManager.scoutGetAllMatchData();
			
			if(isCancelled())
				return SYNC_CANCELLED;
			
			//Write the scout data
			ooStream.writeInt(BluetoothInfo.SCOUT);
			ooStream.writeObject(event);
			ooStream.writeObject(robotsArr);
			ooStream.writeObject(matchDataArr);
			ooStream.flush();
			
			//Clear out the old data after it is sent
			dbManager.scoutClearMatchData();
			
			if(isCancelled())
				return SYNC_CANCELLED;
			
			//Start the reading thread
			Event inEvent = (Event)ioStream.readObject();
			User[] inUsers = (User[])ioStream.readObject();
			String[] inTeamNames = (String[])ioStream.readObject();
			Robot[] inRobots = (Robot[])ioStream.readObject();
			Metric[] inRobotMetrics = (Metric[])ioStream.readObject();
			Metric[] inMatchMetrics = (Metric[])ioStream.readObject();
			
			if(isCancelled())
				return SYNC_CANCELLED;
			
			//Write the received arrays to the database
			dbManager.scoutReplaceEvent(inEvent);
			dbManager.scoutReplaceUsers(inUsers);
			dbManager.scoutReplaceRobots(inRobots, inTeamNames);
			dbManager.scoutReplaceRobotMetrics(inRobotMetrics);
			dbManager.scoutReplaceMatchMetrics(inMatchMetrics);
			
			//Close the streams
			ooStream.close();
			outStream.close();
			serverSocket.close();
			Log.d("FRCKrawler", "Time: " + (System.currentTimeMillis() - startTime));
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("FRCKrawler", "Scout not synced, I/O error.");
			return SYNC_ERROR;
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
			Log.e("FRCKrawler", "Scout not synced, class not found.");
			return SYNC_ERROR;
		}
		
		return SYNC_SUCCESS;
	}
	
	@Override
	protected void onPostExecute(Integer i) {
		tasksRunning--;
		if(i == SYNC_SUCCESS)
			handler.onSyncSuccess(deviceName);
		else if(i == SYNC_ERROR)
			handler.onSyncError(deviceName);
		else if(i == SYNC_CANCELLED)
			handler.onSyncCancel(deviceName);
	}
}
