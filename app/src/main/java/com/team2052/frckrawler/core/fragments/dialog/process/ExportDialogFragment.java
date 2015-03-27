package com.team2052.frckrawler.core.fragments.dialog.process;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import com.team2052.frckrawler.core.GlobalValues;
import com.team2052.frckrawler.core.activities.DatabaseActivity;
import com.team2052.frckrawler.core.database.DBManager;
import com.team2052.frckrawler.core.database.ExportUtil;
import com.team2052.frckrawler.core.util.LogHelper;
import com.team2052.frckrawler.db.Event;

import java.io.File;
import java.io.IOException;

/**
 * @author Adam
 * @since 3/10/2015.
 */
public class ExportDialogFragment extends BaseProgressDialog {

    public static ExportDialogFragment newInstance(Event event) {
        ExportDialogFragment exportDialogFragment = new ExportDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(DatabaseActivity.PARENT_ID, event.getId());
        exportDialogFragment.setArguments(bundle);
        return exportDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Event event = mDaoSession.getEventDao().load(getArguments().getLong(DatabaseActivity.PARENT_ID));
        new ExportToFileSystem(event).execute();
    }

    @Override
    public CharSequence getMessage() {
        return "Generating CSV";
    }

    public class ExportToFileSystem extends AsyncTask<Void, Void, File> {
        final float compileWeight;
        private final Event event;
        File file = null;

        public ExportToFileSystem(Event event) {
            this.compileWeight = getActivity().getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0).getFloat(GlobalValues.PREFS_COMPILE_WEIGHT, 1.0f);
            this.event = event;
        }

        @Override
        protected File doInBackground(Void... voids) {
            File fileSystem = Environment.getExternalStorageDirectory();

            if (event != null) {
                if (fileSystem.canWrite()) {
                    LogHelper.debug("Starting Export");
                    try {
                        file = File.createTempFile(
                                mDaoSession.getGameDao().load(event.getGameId()).getName() + "_" + event.getName() + "_" + "Summary",  /* prefix */
                                ".csv",         /* suffix */
                                fileSystem      /* directory */
                        );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (file != null) {
                        return ExportUtil.exportEventDataToCSV(event, file, DBManager.getInstance(getActivity(), mDaoSession), compileWeight);
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(File file) {
            dismissAllowingStateLoss();
            if (file != null) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                shareIntent.setType("file/csv");
                startActivity(Intent.createChooser(shareIntent, "Share CSV with..."));
            }
        }
    }
}
