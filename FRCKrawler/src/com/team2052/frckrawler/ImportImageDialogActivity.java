package com.team2052.frckrawler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

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
import android.widget.EditText;

import com.ipaulpro.afilechooser.FileChooserActivity;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Robot;
import com.team2052.frckrawler.gui.ProgressSpinner;

public class ImportImageDialogActivity extends Activity implements OnClickListener {
	
	public static final String ROBOT_ID_EXTRA = "com.team2052.frckrawler.robotIDExtra";
	public static final String IMAGE_PATH_EXTRA = "com.team2052.frckrawler.imagePathExtra";
	
	private static final int REQUEST_GALLERY_IMAGE = 11;
	private static final int REQUEST_CHOOSER = 12;
	
	private DBManager dbManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogactivity_import_image_activity);
		
		findViewById(R.id.importWeb).setOnClickListener(this);
		findViewById(R.id.importSD).setOnClickListener(this);
		findViewById(R.id.importGallery).setOnClickListener(this);
		findViewById(R.id.cancelImageImport).setOnClickListener(this);
		
		dbManager = DBManager.getInstance(this);
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.importWeb) {	//Download
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Are You Sure?");
			builder.setMessage("Downloading a new image will erase the old one. Are " +
					"you sure you want to replace the old image?");
			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					new DownloadImageTask().execute();
					dialog.dismiss();
				}
			});
			builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.show();
			
		} else if(v.getId() == R.id.importSD) {	//Import from file
		    Intent intent = new Intent(this, FileChooserActivity.class);
		    startActivityForResult(intent, REQUEST_CHOOSER);
		    
		} else if(v.getId() == R.id.importGallery) {
			Intent intent = new Intent(Intent.ACTION_PICK, 
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, REQUEST_GALLERY_IMAGE);
			
		} else if(v.getId() == R.id.cancelImageImport) {	//Close the activity
			setResult(RESULT_CANCELED);
			finish();		
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_CHOOSER && resultCode == RESULT_OK) {
			Uri uri = data.getData();
            File file = FileUtils.getFile(uri);
            new CopyImageFileTask().execute(file);
            
		} else if(requestCode == REQUEST_GALLERY_IMAGE && resultCode == RESULT_OK) {
			Uri uri = data.getData();
            File file = FileUtils.getFile(uri);
            new CopyImageFileTask().execute(file);
		}
	}
	
	private class DownloadImageTask extends AsyncTask<Void, Void, Boolean> {
		
		AlertDialog progressDialog;
		String url;
		
		protected void onPreExecute() {
			AlertDialog.Builder builder = new AlertDialog.Builder(ImportImageDialogActivity.this);
			builder.setTitle("Downloading...");
			builder.setView(new ProgressSpinner(ImportImageDialogActivity.this));
			builder.setCancelable(false);
			progressDialog = builder.create();
			progressDialog.show();
			
			url = ((EditText)findViewById(R.id.webAdress)).getText().toString();
		}
		
		@Override
		protected Boolean doInBackground(Void... v) {
			if(url == null || url.equals(""))
				return false;
			
			Robot[] robotsArr = dbManager.getRobotsByColumns(
					new String[] {DBContract.COL_ROBOT_ID}, 
					new String[] {Integer.toString(getIntent().
							getIntExtra(ROBOT_ID_EXTRA, -1))}
					);
			Robot robot;
			
			if(robotsArr.length > 0) 
				robot = robotsArr[0];
			else
				return false;
			
			String fileName = getIntent().getStringExtra(IMAGE_PATH_EXTRA);
			url = url.trim();
			
			if(!url.substring(url.length() - 1).equalsIgnoreCase("G") && 
					!url.substring(url.length() - 1).equalsIgnoreCase("F"))
				return false;
			
			 try {
				 downloadFromUrl(url, fileName);
			 } catch(IOException e) {
				 e.printStackTrace();
				 return false;
			 }
			 
			 dbManager.updateRobots(
					 new String[] {DBContract.COL_ROBOT_ID},
					 new String[] {Integer.toString(robot.getID())}, 
					 new String[] {DBContract.COL_IMAGE_PATH}, 
					 new String[] {fileName});
			
			return true;
		}
		
		protected void onPostExecute(Boolean success) {
			progressDialog.dismiss();
			
			if(success) {
				AlertDialog.Builder builder = new AlertDialog.
						Builder(ImportImageDialogActivity.this);
				builder.setTitle("Download Success");
				builder.setMessage("Image successfully downloaded.");
				builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						setResult(RESULT_OK);
						finish();
					}
				});
				builder.show();
				
			} else {
				AlertDialog.Builder builder = new AlertDialog.
						Builder(ImportImageDialogActivity.this);
				builder.setTitle("Download Failed");
				builder.setMessage("There was a problem with downloading this image. Make " +
						"sure you have Internet connection and you are downloading a .gif, " +
						".jpeg, .jpg, or .png image file.");
				builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.show();
			}
		}
		
		/*****
		 * Method: downloadFromUrl
		 * 
		 * @param DownloadUrl
		 * @param fileName
		 * 
		 * Summary: Downloads a file from the specified url and stores it in 
		 * FRCKrawler's image with the name in the passed parameter.
		 *****/
		private void downloadFromUrl(String downloadUrl, String fileName) throws IOException {
			URL url = new URL(downloadUrl); //you can write here any link
			File file = new File(fileName);

			/* Open a connection to that URL. */
			URLConnection ucon = url.openConnection();

			/*
			 * Define InputStreams to read from the URLConnection.
			 */
			InputStream is = ucon.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);

			/*
			 * Read bytes to the Buffer until there is nothing more to read(-1).
			 */
			ByteArrayBuffer baf = new ByteArrayBuffer(5000);
			int current = 0;
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}


			/* Convert the Bytes read to a String. */
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(baf.toByteArray());
			fos.flush();
			fos.close();
		}
	}
	
	private class CopyImageFileTask extends AsyncTask<File, Void, Boolean> {
		
		AlertDialog progressDialog;
		
		@Override
		protected void onPreExecute() {
			AlertDialog.Builder builder = new AlertDialog.Builder(ImportImageDialogActivity.this);
			builder.setTitle("Copying...");
			builder.setView(new ProgressSpinner(ImportImageDialogActivity.this));
			builder.setCancelable(false);
			progressDialog = builder.create();
			progressDialog.show();
		}

		@Override
		protected Boolean doInBackground(File... source) {
			File destination = new File(getIntent().getStringExtra(IMAGE_PATH_EXTRA));
			
			try {
				copy(destination, source[0]);
				
			} catch(FileNotFoundException e) {
				e.printStackTrace();
				return false;
			} catch(IOException e) {
				e.printStackTrace();
				return false;
			}
			
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean success) {
			progressDialog.dismiss();
			
			if(success) {
				AlertDialog.Builder builder = new AlertDialog.
						Builder(ImportImageDialogActivity.this);
				builder.setTitle("Import Success");
				builder.setMessage("Image successfully downloaded.");
				builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						setResult(RESULT_OK);
						finish();
					}
				});
				builder.show();
				
			} else {
				AlertDialog.Builder builder = new AlertDialog.
						Builder(ImportImageDialogActivity.this);
				builder.setTitle("Import Failed");
				builder.setMessage("There was a problem with copying this image. Make sure " +
						"it is a .png, .jpeg, .jpg, or .gif image file.");
				builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.show();
			}
		}
		
		private void copy(File dst, File src) throws IOException, FileNotFoundException {
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dst);
			
			byte[] buf = new byte[1024];
		    int len;
		    while ((len = in.read(buf)) > 0) {
		        out.write(buf, 0, len);
		    }
		    
		    in.close();
		    out.close();
		}
	}
}
