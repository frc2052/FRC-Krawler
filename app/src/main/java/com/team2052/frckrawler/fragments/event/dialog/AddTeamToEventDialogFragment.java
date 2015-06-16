package com.team2052.frckrawler.fragments.event.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.BaseActivity;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Team;
import com.team2052.frckrawler.fragments.ListFragment;
import com.team2052.frckrawler.tba.HTTP;
import com.team2052.frckrawler.tba.JSON;
import com.team2052.frckrawler.tba.TBA;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Adam
 * @since 3/7/2015.
 */
public class AddTeamToEventDialogFragment extends android.support.v4.app.DialogFragment implements DialogInterface.OnClickListener {
    @InjectView(R.id.team_number)
    EditText add_team;
    private Event mEvent;

    public static AddTeamToEventDialogFragment newInstance(Event game) {
        AddTeamToEventDialogFragment fragment = new AddTeamToEventDialogFragment();
        Bundle b = new Bundle();
        b.putLong(BaseActivity.PARENT_ID, game.getId());
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mEvent = DBManager.getInstance(getActivity()).getEventsTable().load(getArguments().getLong(BaseActivity.PARENT_ID));
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
            DBManager dbManager = DBManager.getInstance(getActivity());
            //Team doesn't exist nor does robot and robotevent (most likely)
            String url = TBA.BASE_TBA_API_URL + String.format(TBA.TEAM, teamNumber);
            //Query Team from TBA :)
            Team team = JSON.getGson().fromJson(JSON.getAsJsonObject(HTTP.dataFromResponse(HTTP.getResponse(url))), Team.class);
            dbManager.getTeamsTable().insertNew(team, mEvent);
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
