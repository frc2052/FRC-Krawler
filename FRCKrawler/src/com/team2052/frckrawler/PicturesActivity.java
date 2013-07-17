package com.team2052.frckrawler;

import java.io.File;
import java.io.IOException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Robot;

public class PicturesActivity extends StackableTabActivity implements OnClickListener {
	
	private static final int IMAGE_REQUEST_CODE = 100;
	private static final int IMPORT_REQUEST_CODE = 3;
	
	private static Uri tempUri;
	
	private Robot robot;
	private Uri imageFile;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pictures);
		findViewById(R.id.changePicture).setOnClickListener(this);
		findViewById(R.id.importPicture).setOnClickListener(this);
		refreshImage();
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.changePicture && getPackageManager().
				hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			
		    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		    imageFile = getOutputMediaFile();
		    tempUri = imageFile;
		    
		    if(imageFile != null) {
		    	intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFile);
		    	startActivityForResult(intent, IMAGE_REQUEST_CODE);
		    } else {
		    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		        builder.setMessage("Could not create image directory.");
		        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(which == DialogInterface.BUTTON_NEUTRAL)
							dialog.dismiss();
					}
		        });
		        builder.show();
		    }
		} else if(v.getId() == R.id.importPicture) {
			Intent i = new Intent(this, ImportImageDialogActivity.class);
			i.putExtra(ImportImageDialogActivity.IMAGE_PATH_EXTRA, getOutputMediaFile().getPath());
			i.putExtra(ImportImageDialogActivity.ROBOT_ID_EXTRA, robot.getID());
			startActivityForResult(i, IMPORT_REQUEST_CODE);
			
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setMessage("This device is not equiped with a camera. Your device" +
	        		"must have a camera in order to take pictures.");
	        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(which == DialogInterface.BUTTON_NEUTRAL)
						dialog.dismiss();
				}
	        });
	        builder.show();
		}
	}
	
	
	/*****
	 * Method: getOutputMediaFile
	 * 
	 * @return
	 * 
	 * Summary: gets the file where the robot's image is to be stored.
	 */
	
	private Uri getOutputMediaFile() {
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "FRCKrawler");
			
	    	if(!mediaStorageDir.mkdirs() && !mediaStorageDir.exists())
	    		return null;
	    	
	    	File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	    		"IMG_"+ robot.getTeamNumber() + "_" + robot.getID() + ".jpg");
	    	
	    	System.out.println(mediaFile.getAbsolutePath());
	    	
	    	if(!mediaFile.exists())
	    		try {
	    			mediaFile.createNewFile();
	    		} catch(IOException e) {}
	    	
	    	return Uri.fromFile(mediaFile);
	    	
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setMessage("There is no SD card mounted with this device. You " +
	        		"must have a mounted SD card to take pictures of teams with " +
	        		"your device.");
	        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(which == DialogInterface.BUTTON_NEUTRAL)
						dialog.dismiss();
				}
	        });
	        builder.show();
	        
	        return null;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == IMAGE_REQUEST_CODE) {
			if(resultCode == RESULT_OK) {
				Toast.makeText(this, "Image saved to:\n" +
						tempUri.getPath(), Toast.LENGTH_LONG).show();

				Robot[] robotsArr = DBManager.getInstance(this).getRobotsByColumns
						(databaseKeys, databaseValues);
				if(robotsArr.length > 0) {
					robot = robotsArr[0];
				}

				DBManager.getInstance(this).updateRobots(
						new String[] {DBContract.COL_ROBOT_ID},
						new String[] {Integer.toString(robot.getID())}, 
						new String[] {DBContract.COL_IMAGE_PATH}, 
						new String[] {tempUri.getPath()});

				refreshImage();
			} else {
				Toast.makeText(this, "No image saved", Toast.LENGTH_LONG).show();
			}
		} else if(requestCode == IMPORT_REQUEST_CODE) {
			if(resultCode == RESULT_OK)
				refreshImage();
		}
	}
	
	
	/*****
	 * Method: refreshImage
	 * 
	 * Summary: gets the robot from the SQL database and puts its image
	 * in the image view.
	 */
	
	private void refreshImage() {
		Robot[] robotsArr = DBManager.getInstance(this).getRobotsByColumns
				(databaseKeys, databaseValues);
		
		if(robotsArr.length > 0) {
			robot = robotsArr[0];
			
			try {
				imageFile = Uri.parse(robot.getImagePath());
			} catch(NullPointerException e) {
				Log.e("FRCKrawler", "Image file was empty.");
				imageFile = Uri.EMPTY;
			}
		}
		
		if(robot.getImagePath() != null)
			((ImageView)findViewById(R.id.image)).setImageBitmap(
				    decodeSampledBitmapFromFile(imageFile.getPath(), 500, 500));
	}
	
	public static int calculateInSampleSize(
            	BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

    	if (height > reqHeight || width > reqWidth) {

        // Calculate ratios of height and width to requested height and width
        	final int heightRatio = Math.round((float) height / (float) reqHeight);
        	final int widthRatio = Math.round((float) width / (float) reqWidth);

        // Choose the smallest ratio as inSampleSize value, this will guarantee
        // a final image with both dimensions larger than or equal to the
        // requested height and width.
        	inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
    	}

    	return inSampleSize;
	}
	
	/*****
	 * Method: decodeSampledBitmapFromFile
	 * 
	 * @param path
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 * 
	 * Summary: Gets a Bitmap object from an image file. Uses the specified width
	 * and height.
	 *****/
	public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, 
			int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(new File(path).getAbsolutePath(), options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(new File(path).getAbsolutePath(), options);
	}
}
