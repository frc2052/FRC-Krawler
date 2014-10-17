package com.team2052.frckrawler.fragment.robot;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.DatabaseActivity;
import com.team2052.frckrawler.adapters.RobotPhotoAdapter;
import com.team2052.frckrawler.fragment.BaseFragment;
import com.team2052.frckrawler.util.LogHelper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import frckrawler.Robot;
import frckrawler.RobotPhoto;
import frckrawler.RobotPhotoDao;

/**
 * @author Adam
 * @since 10/4/2014
 */
public class PhotosFragment extends BaseFragment
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
    public void onActivityCreated(Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_add_picture, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_grid_photots, null);
        mRobot = mDaoSession.getRobotDao().load(getArguments().getLong(DatabaseActivity.PARENT_ID, 0));
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
            mDaoSession.getRobotPhotoDao().insert(new RobotPhoto(null, new File(mCurrentPhotoPath).getAbsolutePath(), mRobot.getId()));
            new GetRobotPhotosTask().execute();
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
            mDaoSession.getRobotPhotoDao().insert(new RobotPhoto(null, new File(mCurrentPhotoPath).getAbsolutePath(), mRobot.getId()));
            cursor.close();
            new GetRobotPhotosTask().execute();
        }
    }

    private File createImageFile() throws IOException
    {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_" + mRobot.getGame().getName().toLowerCase() + "_" + mRobot.getTeam().getNumber();
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
            return mDaoSession.getRobotPhotoDao().queryBuilder().where(RobotPhotoDao.Properties.RobotId.eq(mRobot.getId())).list();
        }

        @Override
        protected void onPostExecute(List<RobotPhoto> photos)
        {
            LogHelper.debug(String.valueOf(photos.size()));
            mGridview.setAdapter(mAdapter = new RobotPhotoAdapter(getActivity(), photos));
        }
    }
}
