package com.team2052.frckrawler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import au.com.bytecode.opencsv.CSVWriter;

import com.team2052.frckrawler.bluetooth.Server;
import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.CompiledData;
import com.team2052.frckrawler.database.structures.Event;
import com.team2052.frckrawler.database.structures.Query;

public class ServerActivity extends TabActivity implements View.OnClickListener, 
							DialogInterface.OnClickListener {
	
	private static final int REQUEST_BT_ENABLED = 1;
	private DBManager dbManager;
	private Event selectedEvent;
	private Server server;
	private Event[] events;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth_server_manager);
		findViewById(R.id.chooseEvent).setOnClickListener(this);
		findViewById(R.id.hostToggle).setOnClickListener(this);
		findViewById(R.id.exportCSV).setOnClickListener(this);
		findViewById(R.id.importDB).setOnClickListener(this);
		findViewById(R.id.exportDB).setOnClickListener(this);
		dbManager = DBManager.getInstance(this);
		server = Server.getInstance(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		((ToggleButton)findViewById(R.id.hostToggle)).
			setChecked(server.isOpen());
		if(server.isOpen()) {
			selectedEvent = server.getHostedEvent();
			((TextView)findViewById(R.id.eventName))
					.setText(selectedEvent.getEventName());
		}
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.chooseEvent:
				events = dbManager.getAllEvents();
				CharSequence[] eventNames = new String[events.length];
				for(int i = 0; i < events.length; i++) {
					eventNames[i] = events[i].getEventName() + 
							", " + events[i].getGameName();
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Events");
				builder.setItems(eventNames, this);
				builder.show();
				break;
				
			case R.id.hostToggle:
				ToggleButton toggle = (ToggleButton)findViewById(R.id.hostToggle);
				if(selectedEvent != null) {
					if(toggle.isChecked()) {
						if(BluetoothAdapter.getDefaultAdapter() == null) {
							Toast.makeText(this, "Sorry, your device does not " +
									"support Bluetooth. You will not be able to " +
									"open a server and recieve data from scouts.", 
									Toast.LENGTH_LONG).show();
							
							((ToggleButton)findViewById(R.id.hostToggle)).
								setChecked(false);
							return;
						} else {
							if(BluetoothAdapter.getDefaultAdapter().isEnabled()) 
								server.open(selectedEvent);
							else {
								Intent enableBtIntent = 
							    		new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
							    startActivityForResult(enableBtIntent, REQUEST_BT_ENABLED);
							}
						}
					
					} else
						server.close();
				} else {
					Toast.makeText(this, 
							"Could not open server. No event selected.", 
							Toast.LENGTH_LONG).show();
					toggle.setChecked(false);
				}
				break;
				
			case R.id.exportCSV:
				new ExportCSVTask().execute(selectedEvent);
				break;
				
			case R.id.importDB:
				importDB();				
				break;
				
			case R.id.exportDB:
				exportDB();
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		selectedEvent = events[which];
		((TextView)findViewById(R.id.eventName)).
				setText(selectedEvent.getEventName() + ", " + selectedEvent.getGameName());
		dialog.dismiss();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_BT_ENABLED && resultCode == RESULT_OK) {
			server.open(selectedEvent);
		}
	}
	
	private void exportDB() {
		if(!Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Export Failed!");
			b.setMessage("FRCKrawler could not export your database to the " +
					"SD card. Your device does not have an SD card, or it is not " +
					"available for writing at the moment.");
			b.setNeutralButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			b.show();
		}
		try {
	        File sd = Environment.getExternalStorageDirectory();
	        if (sd.canWrite()) {
	            String backupDBPath = "FRCKrawlerBackup.db";
	            File currentDB = new File(getFilesDir(), DBContract.DATABASE_NAME);
	            File backupDB = new File(sd, backupDBPath);
	            if (currentDB.exists()) {
	            	FileOutputStream fOut = new FileOutputStream(backupDB);
	            	FileInputStream fIn = new FileInputStream(currentDB);
	                FileChannel src = fIn.getChannel();
	                FileChannel dst = fOut.getChannel();
	                dst.transferFrom(src, 0, src.size());
	                src.close();
	                dst.close();
	                fOut.close();
	                fIn.close();
	            } else 
	            	Toast.makeText(this, "Error in finding local " +
	            			"database file.", Toast.LENGTH_LONG).show();
	        }
	    } catch (FileNotFoundException e) {
	    	e.printStackTrace();
	    	Log.e("FCKrawler", e.getMessage());
	    	return;
	    } catch (IOException e) {
	    	Log.e("FCKrawler", e.getMessage());
	    	return;
		}
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("Export Success!");
		b.setMessage("Exported file to the SD card. File saved as " +
				"FRCKrawlerBackup.db in the SD card's root directory.");
		b.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		b.show();
	}
	
	private void importDB() {
		if(!Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			AlertDialog.Builder bu = new AlertDialog.Builder(this);
			bu.setTitle("Sorry...");
			bu.setMessage("FRCKrawler could not import your database. Your device " +
					"does not have an SD card mounted.");
			bu.setNeutralButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			bu.show();
		}
		try {
	        File sd = Environment.getExternalStorageDirectory();
	        if (sd.canWrite()) {
	            String backupDBPath = "FRCKrawlerBackup.db";
	            File currentDB = new File(getFilesDir(), DBContract.DATABASE_NAME);
	            File backupDB = new File(sd, backupDBPath);
	            if (backupDB.exists()) {
	            	FileOutputStream fOut = new FileOutputStream(currentDB);
	            	FileInputStream fIn = new FileInputStream(backupDB);
	                FileChannel src = fOut.getChannel();
	                FileChannel dst = fIn.getChannel();
	                src.transferFrom(dst, 0, dst.size());
	                src.close();
	                dst.close();
	                fOut.close();
	                fIn.close();
	                AlertDialog.Builder bu = new AlertDialog.Builder(this);
					bu.setTitle("Import Success!");
					bu.setMessage("Imported the database.");
					bu.setNeutralButton("OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					bu.show();
	            } else {
	            	AlertDialog.Builder bu = new AlertDialog.Builder(this);
					bu.setTitle("Impor Failed!");
					bu.setMessage("Could not find the backup file " +
	            			"FRCKrawlerBackup.db in your SD card's " +
	            			"root folder.");
					bu.setNeutralButton("OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					bu.show();
	            }
	        }
	    } catch (FileNotFoundException e) {
	    	e.printStackTrace();
	    	Log.e("FCKrawler", e.getMessage());
	    	return;
	    } catch (IOException e) {
	    	Log.e("FCKrawler", e.getMessage());
	    	return;
		}
	}
	
	private class ExportCSVTask extends AsyncTask<Event, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Event... params) {
			if(params[0] == null)
				return false;
			CompiledData[] compiledData = dbManager.getCompiledEventData
					(params[0], new Query[0], null);
			String[][] dataArray = new String[compiledData.length + 1][];
			if(compiledData.length > 0)
				dataArray[0] = compiledData[0].getMetricsAsStrings();
			else
				dataArray[0] = new String[0];
			for(int i = 0; i < compiledData.length; i++)
				dataArray[i + 1] = compiledData[i].getValuesAsStrings();
			try {
		        File sd = Environment.getExternalStorageDirectory();
		        if (sd.canWrite()) {
		            String exportPath = params[0].getEventName() + 
		            		params[0].getEventID() + "Summary.csv";
		            File exportFile = new File(sd, exportPath);
		            if(!exportFile.exists())
		            	exportFile.createNewFile();
		            CSVWriter csvWriter = new CSVWriter(new FileWriter(exportFile), ',');
		            for(String[] arr : dataArray)
		            	csvWriter.writeNext(arr);
		            csvWriter.close();
		        } else {
		        	return false;
		        }
		    } catch (FileNotFoundException err) {
		    	err.printStackTrace();
		    	Log.e("FCKrawler", err.getMessage());
		    	return false;
		    } catch (IOException err) {
		    	Log.e("FCKrawler", err.getMessage());
		    	return false;
			}
			
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean b) {
			AlertDialog.Builder builder = new AlertDialog.Builder
					(ServerActivity.this);
			
			if(b) {
				builder.setTitle("Export Success");
				builder.setMessage("Event summary was exported succesfully. " +
						"File saved as (Event Name)(Event ID)Summary.exe in " +
						"the SD card's root director.");
				builder.setNeutralButton("OK", new DialogInterface.OnClickListener(){
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				
			} else {
				builder.setTitle("Export Failed!");
				builder.setMessage("The export has failed. Either SD card was " +
						"unavailable or you have not chosen an Event's summary to export.");
				builder.setNeutralButton("OK", new DialogInterface.OnClickListener(){
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
			}
			
			builder.show();
		}
	}
}
