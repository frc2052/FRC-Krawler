package com.team2052.frckrawler.core.fragments.dialog.manualmode;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.core.FRCKrawler;
import com.team2052.frckrawler.core.activities.DatabaseActivity;
import com.team2052.frckrawler.core.database.DBManager;
import com.team2052.frckrawler.core.fragments.ListFragment;
import com.team2052.frckrawler.core.tba.HTTP;
import com.team2052.frckrawler.core.tba.JSON;
import com.team2052.frckrawler.core.tba.TBA;
import com.team2052.frckrawler.core.util.LogHelper;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Team;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Adam
 * @since 3/7/2015.
 */
public class AddTeamDialogFragment extends android.support.v4.app.DialogFragment implements DialogInterface.OnClickListener {
    @InjectView(R.id.team_number)
    EditText add_team;
    private Event mEvent;

    public static AddTeamDialogFragment newInstance(Event game) {
        AddTeamDialogFragment fragment = new AddTeamDialogFragment();
        Bundle b = new Bundle();
        b.putLong(DatabaseActivity.PARENT_ID, game.getId());
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mEvent = ((FRCKrawler) getActivity().getApplication()).getDBSession().getDaoSession().getEventDao().load(getArguments().getLong(DatabaseActivity.PARENT_ID));
    }

    @Override
    //Build the dialog
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_add_team, null);
        ButterKnife.inject(this, view);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Event");
        builder.setPositiveButton("Add", this);
        builder.setNegativeButton("Cancel", this);
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            new SaveTeamTask(Integer.parseInt(add_team.getText().toString())).execute();
        } else {
            dismiss();
        }
    }

    public class SaveTeamTask extends AsyncTask<Void, Void, Void> {

        private final int teamNumber;

        public SaveTeamTask(int teamNumber) {
            this.teamNumber = teamNumber;
        }

        @Override
        protected Void doInBackground(Void... params) {
            LogHelper.info("importing team");
            DBManager dbManager = ((FRCKrawler) getActivity().getApplicationContext()).getDBSession();
            //Team doesn't exist nor does robot and robotevent (most likely)
            String url = TBA.BASE_TBA_API_URL + String.format(TBA.TEAM, teamNumber);
            //Query Team from TBA :)
            Team team = JSON.getGson().fromJson(JSON.getAsJsonObject(HTTP.dataFromResponse(HTTP.getResponse(url))), Team.class);
            dbManager.insertTeam(team, mEvent);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(getActivity(), "Imported Team" + teamNumber, Toast.LENGTH_SHORT).show();
            ((ListFragment) getParentFragment()).updateList();
            dismiss();
        }
    }
}
