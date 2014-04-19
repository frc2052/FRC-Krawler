package com.team2052.frckrawler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Event;

public class DBBackupDialogActivity extends Activity implements OnClickListener {
	private DBManager db;
	private Event[] events;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogactivity_db_backup);
		findViewById(R.id.createBackup).setOnClickListener(this);
		findViewById(R.id.revertBackup).setOnClickListener(this);
		db = DBManager.getInstance(this);
		new GetEventsTask().execute();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.createBackup:
				exportDB();
				break;
			case R.id.revertBackup:
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Warning!");
				builder.setMessage("Importing another version of the database will " +
						"erase all data that is not in the version you are attempting to " +
						"import. Are you sure you wan't to do this?");
				builder.setPositiveButton("Yes", new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							importDB();
							dialog.dismiss();
						}
					});
				builder.setNegativeButton("No", new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.show();
				break;
		}
	}
	
	/*****
	 * Class: GetEventsTask
	 * 
	 * Description: an AsyncTask to populate the events array and the events spinner.
	 */
	private class GetEventsTask extends AsyncTask<Void, Void, Event[]> {

		@Override
		protected Event[] doInBackground(Void... params) {
			return db.getAllEvents();
		}
		
		@Override
		protected void onPostExecute(Event[] _events) {
			events = _events;
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
		b.setMessage("Exported file to the device's root directory. File saved as " +
				"FRCKrawlerBackup.db");
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
}
