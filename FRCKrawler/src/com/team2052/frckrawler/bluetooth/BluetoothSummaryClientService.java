package com.team2052.frckrawler.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.CompiledData;
import com.team2052.frckrawler.database.structures.Event;
import com.team2052.frckrawler.database.structures.MatchData;

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
		
		public BluetoothSummaryClientThread(BluetoothDevice _serverDevice, 
				ClientThreadListener _listener) {
			
			serverDevice = _serverDevice;
			threadListener = _listener;
		}
		
		public void run() {
			
			try {
				
				//Open the socket
				BluetoothSocket serverSocket = serverDevice.
						createRfcommSocketToServiceRecord
							(UUID.fromString(BluetoothInfo.UUID));
				serverSocket.connect();
				
				if(threadListener != null)
					threadListener.onUpdate("Connected");
				
				//Open the streams
				InputStream inStream = serverSocket.getInputStream();
				OutputStream outStream = serverSocket.getOutputStream();
				ObjectOutputStream ooStream = new ObjectOutputStream(outStream);
				
				//Send what kind of connection this is, scout or summary
				ooStream.writeInt(BluetoothInfo.SUMMARY);
				ooStream.flush();
				
				if(threadListener != null)
					threadListener.onUpdate("Reading Data");
				
				SummaryDataReader reader = new SummaryDataReader(getApplicationContext(), 
						inStream, threadListener);
				reader.start();
				
				while(!reader.isFinished()) {
					try {
						Thread.sleep(10);
					} catch(InterruptedException e) {}
				}
				
				//Close the streams
				ooStream.close();
				outStream.close();
				inStream.close();
				serverSocket.close();
				
				//Notify of a successful sync
				if(threadListener != null && reader.successful())
					threadListener.onSuccessfulSync();
				
			} catch(IOException e) {
				e.printStackTrace();
				if(threadListener != null)
					threadListener.onUnsuccessfulSync(e.getMessage());
			}
		}
	}
	
	
	/*****
	 * Class: SummaryDataReader
	 * 
	 * @author Charles Hofer
	 *
	 * Description: Reads the required data from the passed InputStream.
	 */
	
	private class SummaryDataReader extends Thread {
		
		private boolean wasSuccessful;
		private boolean isFinished;
		private Context context;
		private InputStream iStream;
		private ClientThreadListener listener;
		
		public SummaryDataReader(Context _context, InputStream _iStream, 
				ClientThreadListener _listener) {
			wasSuccessful = false;
			isFinished = false;
			context = _context;
			iStream = _iStream;
			listener = _listener;
		}
		
		@Override
		public void run() {
			
			try {
				ObjectInputStream oiStream = new ObjectInputStream(iStream);
				
				Event inEvent = null;
				CompiledData[] compiledData = new CompiledData[0];
				MatchData[] matchData = new MatchData[0];
				
				inEvent = (Event)oiStream.readObject();
				compiledData = (CompiledData[])oiStream.readObject();
				matchData = (MatchData[])oiStream.readObject();
				
				if(inEvent != null) {
					DBManager db = DBManager.getInstance(context);
					db.summarySetEvent(inEvent);
					db.summarySetCompiledData(compiledData);
					db.summarySetRawMatchData(matchData);
				}
				
				wasSuccessful = true;
				
			}  catch(IOException e) {
				if(listener != null)
					listener.onUnsuccessfulSync(e.getMessage());
			} catch (ClassNotFoundException e) {
				if(listener != null)
					listener.onUnsuccessfulSync(e.getMessage());
			}
			
			isFinished = true;
		}
		
		public boolean isFinished() {
			return isFinished;
		}
		
		public boolean successful() {
			return wasSuccessful;
		}
	}
}
