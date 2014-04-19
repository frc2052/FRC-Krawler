package com.team2052.frckrawler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVWriter;

import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.CompiledData;
import com.team2052.frckrawler.database.structures.Event;
import com.team2052.frckrawler.database.structures.Query;

public class ExcelExportDialogActivity extends Activity implements OnClickListener {
	private DBManager db;
	private Event[] events;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogactivity_excel_export);
		findViewById(R.id.exportFile).setOnClickListener(this);
		findViewById(R.id.exportEmail).setOnClickListener(this);
		db = DBManager.getInstance(this);
		new GetEventsTask().execute();
	}

	@Override
	public void onClick(View v) {
		CharSequence[] items = new String[events.length];
		for(int i = 0; i < events.length; i++) {
			items[i] = events[i].toString();
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Choose an Event...");
		switch(v.getId()) {
			case R.id.exportFile:
				builder.setItems(items, new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new ExportCSVTask(true).execute(events[which]);
						dialog.dismiss();
					}
				});
				break;
			case R.id.exportEmail:
				builder.setItems(items, new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new ExportCSVTask(false).execute(events[which]);
						dialog.dismiss();
						Intent intent = new Intent(Intent.ACTION_SEND);
						intent.setType("text/plain");
						intent.putExtra(Intent.EXTRA_EMAIL, new String[] {});
						intent.putExtra(Intent.EXTRA_SUBJECT, events[which].toString() + " Data");
						intent.putExtra(Intent.EXTRA_TEXT, "This export was generated " +
								"by the FRCKrawler Scouting System. Just open the file with " +
								"Excel, Google Docs, or Tableu to view and visualize your " +
								"data");
						File root = Environment.getExternalStorageDirectory();
						String csvFilename = events[which].getEventName() + 
								events[which].getEventID() + "Summary.csv";
						File file = new File(root, csvFilename);
						Log.d("FRCKrawler", file.getAbsolutePath());
						if (!file.exists() || !file.canRead()) {
						    Log.e("FRCKrawler", "Could not read the CSV file into email.");
						}
						Uri uri = Uri.parse("file://" + file);
						intent.putExtra(Intent.EXTRA_STREAM, uri);
						startActivity(Intent.createChooser(intent, "Send email..."));
					}
				});
				break;
		}
		builder.show();
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
	
	/*****
	 * Class: ExportCSVTask
	 * 
	 * Description: exports the summary data to an Excel friendly format
	 *****/
	
	private class ExportCSVTask extends AsyncTask<Event, Void, Boolean> {
		boolean showFeedback;
		
		public ExportCSVTask(boolean _showFeedback) {
			showFeedback = _showFeedback;
		}
		
		@Override
		protected Boolean doInBackground(Event... params) {
			if(params[0] == null)
				return false;
			CompiledData[] compiledData = db.getCompiledEventData
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
			if(!showFeedback)
				return;
			AlertDialog.Builder builder = new AlertDialog.Builder
					(ExcelExportDialogActivity.this);
			if(b) {
				builder.setTitle("Export Success");
				builder.setMessage("Event summary was exported succesfully. " +
						"File saved as (Event Name)(Event ID)Summary.exe in " +
						"the file system's root directory.");
				builder.setNeutralButton("OK", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				
			} else {
				builder.setTitle("Export Failed!");
				builder.setMessage("The export has failed. Either the SD card was " +
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
