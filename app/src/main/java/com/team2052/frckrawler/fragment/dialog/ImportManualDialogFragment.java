package com.team2052.frckrawler.fragment.dialog;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.team2052.frckrawler.FRCKrawler;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.DatabaseActivity;
import com.team2052.frckrawler.database.ImportUtil;
import com.team2052.frckrawler.db.DaoSession;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Match;
import com.team2052.frckrawler.util.LogHelper;

import java.io.File;

/**
 * For desperate measures
 *
 * @author Adam
 * @since 10/9/2014
 */
public class ImportManualDialogFragment extends DialogFragment implements View.OnClickListener
{
    private DaoSession mDaoSession;
    private Button importMatches;
    private Button importTeams;
    private Event mEvent;
    private int REQUEST_FILE_MATCHES = 1;
    private int REQUEST_FILE_TEAMS = 2;
    private View mView;

    public static ImportManualDialogFragment newInstance(Event event)
    {
        ImportManualDialogFragment fragment = new ImportManualDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(DatabaseActivity.PARENT_ID, event.getId());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        mDaoSession = ((FRCKrawler) getActivity().getApplication()).getDaoSession();
        mEvent = mDaoSession.getEventDao().load(getArguments().getLong(DatabaseActivity.PARENT_ID));
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_import_manual, null);
        getDialog().setTitle("Import Manual");
        importMatches = (Button) view.findViewById(R.id.import_matches);
        importTeams = (Button) view.findViewById(R.id.import_teams_manual);
        importMatches.setOnClickListener(this);
        importTeams.setOnClickListener(this);
        this.mView = view;
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_FILE_TEAMS && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
            }
            if (uri != null) {
                new ImportTeamsTask(new File(uri.getPath())).execute();
            }
        }
    }

    @Override
    public void onClick(View view)
    {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.import_matches:
                new ImportRandomMatches().execute();
                break;
            case R.id.import_teams_manual:
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("file/csv");
                startActivityForResult(intent, REQUEST_FILE_TEAMS);
                break;
        }
    }

    public class ImportTeamsTask extends AsyncTask<Void, Void, Void>
    {

        public final File file;

        public ImportTeamsTask(File file)
        {

            LogHelper.debug("Constructing");
            this.file = file;
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            ProgressDialogFragment.showLoadingProgress(getChildFragmentManager());
            mDaoSession.runInTx(new Runnable()
            {
                @Override
                public void run()
                {
                    LogHelper.debug("Starting Import");
                    ImportUtil.importTeamsFromCSV(mDaoSession, file, mEvent);
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            ProgressDialogFragment.dismissLoadingProgress(getChildFragmentManager());
        }
    }

    public class ImportRandomMatches extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... voids)
        {
            ProgressDialogFragment.showLoadingProgress(getChildFragmentManager());
            final int number = Integer.parseInt(((EditText) mView.findViewById(R.id.num_matches)).getText().toString());
            mDaoSession.runInTx(new Runnable()
            {
                @Override
                public void run()
                {
                    for (int i = 0; i < number; i++) {
                        //Create a filler match
                        mDaoSession.insert(new Match(null, mEvent.getId(), mEvent.getFmsid() + "_filler_match_" + (i + 1), i + 1, "qm", (long) 2052, (long) 8052, (long) 2052, (long) 2052, (long) 8052, (long) 2052, 0, 0));
                    }
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            ProgressDialogFragment.dismissLoadingProgress(getChildFragmentManager());
        }
    }


}
