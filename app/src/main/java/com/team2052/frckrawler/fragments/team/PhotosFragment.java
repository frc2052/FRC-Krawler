package com.team2052.frckrawler.fragments.team;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.BaseActivity;
import com.team2052.frckrawler.adapters.RobotPhotoAdapter;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.db.RobotPhoto;
import com.team2052.frckrawler.fragments.BaseFragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Not in use anymore.
 * DEAD?
 *
 * @author Adam
 * @since 10/4/2014
 */
@Deprecated
public class PhotosFragment extends BaseFragment {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int RESULT_LOAD_IMAGE = 2;
    @InjectView(R.id.gridview)
    GridView mGridview;
    private String mCurrentPhotoPath;
    private Robot mRobot;
    private BaseAdapter mAdapter;

    public static PhotosFragment newInstance(Robot robot) {
        PhotosFragment fragment = new PhotosFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(BaseActivity.PARENT_ID, robot.getId());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_grid_photos, null);
        ButterKnife.inject(this, view);
        mRobot = null; //mDbManager.getDaoSession().getRobotDao().load(getArguments().getLong(BaseActivity.PARENT_ID, 0));
        new GetRobotPhotosTask().execute();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Name Photo");
            final EditText editText = new EditText(getActivity());
            editText.setHint("Name");
            builder.setView(editText);
            builder.setNegativeButton("Cancel", null);
            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //mDbManager.getDaoSession().getRobotPhotoDao().insert(new RobotPhoto(null, new File(mCurrentPhotoPath).getAbsolutePath(), mRobot.getId(), editText.getText().toString(), new Date()));
                    new GetRobotPhotosTask().execute();
                }
            });
            builder.show();
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

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Name Photo");
            final EditText editText = new EditText(getActivity());
            editText.setHint("Name");
            builder.setView(editText);
            builder.setNegativeButton("Cancel", null);
            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //mDbManager.getDaoSession().getRobotPhotoDao().insert(new RobotPhoto(null, new File(mCurrentPhotoPath).getAbsolutePath(), mRobot.getId(), editText.getText().toString(), new Date()));
                    new GetRobotPhotosTask().execute();
                }
            });
            cursor.close();
            new GetRobotPhotosTask().execute();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_picture, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_" + mDbManager.getRobotsTable().getGame(mRobot) + "_" + mRobot.getTeam_id();
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public class GetRobotPhotosTask extends AsyncTask<Void, Void, List<RobotPhoto>> {

        @Override
        protected List<RobotPhoto> doInBackground(Void... voids) {
            return null; //mDbManager.getDaoSession().getRobotPhotoDao().queryBuilder().where(RobotPhotoDao.Properties.Robot_id.eq(mRobot.getId())).list();
        }

        @Override
        protected void onPostExecute(List<RobotPhoto> photos) {
            mGridview.setAdapter(mAdapter = new RobotPhotoAdapter(getActivity(), photos));
        }
    }
}
