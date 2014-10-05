package com.team2052.frckrawler.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.activeandroid.query.Select;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.DatabaseActivity;
import com.team2052.frckrawler.adapters.RobotPhotoAdapter;
import com.team2052.frckrawler.database.models.Robot;
import com.team2052.frckrawler.database.models.RobotPhoto;
import com.team2052.frckrawler.util.LogHelper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Adam
 * @since 10/4/2014
 */
public class PhotosFragment extends Fragment
{
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int RESULT_LOAD_IMAGE = 2;

    private String mCurrentPhotoPath;
    private Robot mRobot;
    private GridView mGridview;
    private BaseAdapter mAdapter;

    public static PhotosFragment newInstance(Robot robot)
    {
        PhotosFragment fragment = new PhotosFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(DatabaseActivity.PARENT_ID, robot.getId());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        mRobot = Robot.load(Robot.class, getArguments().getLong(DatabaseActivity.PARENT_ID, 0));
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_add_picture, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_grid_photots, null);
        mGridview = (GridView) view.findViewById(R.id.gridview);
        new GetRobotPhotosTask().execute();
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.menu_take_picture) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {

                File photoFile = null;
                //Create the file template
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (photoFile != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
            return true;
        } else if (item.getItemId() == R.id.menu_import) {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            new RobotPhoto(mRobot, new File(mCurrentPhotoPath)).save();
        } else if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            File file = new File(picturePath);

            try {
                file.renameTo(createImageFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
            new RobotPhoto(mRobot, new File(mCurrentPhotoPath)).save();
            cursor.close();
        }
    }

    private File createImageFile() throws IOException
    {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_" + mRobot.game.name.toLowerCase() + "_" + mRobot.team.number;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        LogHelper.debug(image.getAbsolutePath());
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public class GetRobotPhotosTask extends AsyncTask<Void, Void, List<RobotPhoto>>
    {

        @Override
        protected List<RobotPhoto> doInBackground(Void... voids)
        {
            return new Select().from(RobotPhoto.class).where("Robot = ?", mRobot.getId()).execute();
        }

        @Override
        protected void onPostExecute(List<RobotPhoto> photos)
        {
            LogHelper.debug(String.valueOf(photos.size()));
            mGridview.setAdapter(mAdapter = new RobotPhotoAdapter(getActivity(), photos));
        }
    }
}