package com.team2052.frckrawler.core.ui;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.core.FRCKrawler;
import com.team2052.frckrawler.core.adapters.ListViewAdapter;
import com.team2052.frckrawler.core.database.DBManager;
import com.team2052.frckrawler.core.listitems.ListItem;
import com.team2052.frckrawler.core.listitems.elements.SimpleListElement;
import com.team2052.frckrawler.core.tba.JSON;
import com.team2052.frckrawler.db.PickList;
import com.team2052.frckrawler.db.Team;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adam on 3/29/15.
 */
public class PickListView extends FrameLayout implements View.OnClickListener {
    private ListView mListView;
    private PickList mPickList;
    private DBManager dbManager;

    public PickListView(Context context) {
        super(context);
    }

    public PickListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PickListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PickListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void initWithParams(PickList pickList) {
        this.dbManager = ((FRCKrawler) getContext().getApplicationContext()).getDBSession();
        LayoutInflater.from(getContext()).inflate(R.layout.pick_list_view, this, true);
        this.mPickList = pickList;
        TextView header = (TextView) findViewById(R.id.pick_list_title);
        header.setText(pickList.getName());
        findViewById(R.id.add_pick_button).setOnClickListener(this);
        mListView = (ListView) findViewById(R.id.pick_list_list);
        getPicks();
    }

    @Override
    public void onClick(View v) {
        //Open Alert Dialog
        if (v.getId() == R.id.add_pick_button) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.add_pick_dialog, null);
            Spinner team_spinner = (Spinner) view.findViewById(R.id.add_pick_spinner);
            EditText prioity = (EditText) view.findViewById(R.id.add_pick_priority);
            List<Team> teams = dbManager.getTeams(dbManager.getDaoSession().getEventDao().load(mPickList.getEventId()));
            ArrayList<ListItem> listItems = new ArrayList<>();
            for (Team event : teams) {
                listItems.add(new SimpleListElement(String.valueOf(event.getNumber()), String.valueOf(event.getNumber())));
            }
            team_spinner.setAdapter(new ListViewAdapter(getContext(), listItems));

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setPositiveButton("Add Pick", (dialog, which) -> {
                int team_number = Integer.parseInt(((SimpleListElement) team_spinner.getSelectedItem()).getKey());
                JsonObject data = JSON.getAsJsonObject(mPickList.getData());
                JsonArray picks = data.get("picks").getAsJsonArray();
                JsonObject pick = new JsonObject();
                pick.addProperty("team", team_number);
                pick.addProperty("prioity", Integer.parseInt(prioity.getText().toString()));
                picks.add(picks);
                data.add("picks", picks);
                mPickList.setData(JSON.getGson().toJson(data));
                dbManager.getDaoSession().update(mPickList);
            });
            builder.setView(view);
            builder.setNegativeButton("Cancel", null);
            builder.create().show();
        }
    }


    public void getPicks() {
        if (!Strings.isNullOrEmpty(mPickList.getData())) {
            JsonObject data = JSON.getAsJsonObject(mPickList.getData());
            JsonArray picks = data.get("picks").getAsJsonArray();
            for (JsonElement element : picks) {

            }
        } else {
            JsonObject data = new JsonObject();
            JsonArray picks = new JsonArray();
            data.add("picks", picks);
            mPickList.setData(JSON.getGson().toJson(data));
            dbManager.getDaoSession().update(mPickList);
            getPicks();
        }
    }

    public class AddPickDialog extends AsyncTask<Void, Void, List<Team>> {

        @Override
        protected List<Team> doInBackground(Void... params) {
            List<Team> teams = dbManager.getTeams(dbManager.getDaoSession().getEventDao().load(mPickList.getEventId()));
            Iterables.filter(teams, new Predicate<Team>() {
                @Override
                public boolean apply(Team input) {


                    return false;
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(List<Team> teams) {

        }
    }


}
