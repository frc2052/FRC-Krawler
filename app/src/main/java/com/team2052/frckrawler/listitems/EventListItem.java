package com.team2052.frckrawler.listitems;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.DatabaseActivity;
import com.team2052.frckrawler.activity.MatchScheduleActivity;
import com.team2052.frckrawler.activity.RawMatchDataActivity;
import com.team2052.frckrawler.activity.SummaryActivity;
import com.team2052.frckrawler.activity.dialog.AttendingTeamsDialogActivity;
import com.team2052.frckrawler.activity.dialog.EditEventDialogActivity;
import com.team2052.frckrawler.activity.dialog.ImportDialogActivity;
import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.structures.Event;

public class EventListItem implements ListItem {
    private final Event event;

    public EventListItem(Event event) {
        this.event = event;
    }

    @Override
    public View getView(final Context c, LayoutInflater inflater, View convertView) {
        convertView = inflater.inflate(R.layout.list_item_event, null);
        ((TextView) convertView.findViewById(R.id.list_view_event_location)).setText(event.getLocation());
        ((TextView) convertView.findViewById(R.id.list_view_event_date)).setText(event.getDateStamp().toString());
        ((TextView) convertView.findViewById(R.id.list_view_event_name)).setText(event.getEventName());
        convertView.findViewById(R.id.list_view_event_edit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(c, EditEventDialogActivity.class);
                i.putExtra(EditEventDialogActivity.EVENT_ID_EXTRA, Integer.toString(event.getEventID()));
                c.startActivity(i);
            }
        });
        Spinner spinner = (Spinner) convertView.findViewById(R.id.list_view_event_spinner);
        spinner.setAdapter(ArrayAdapter.createFromResource(c, R.array.event_spinner, android.R.layout.simple_list_item_1));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Intent i = null;
                System.out.println(position);
                switch (position) {
                    case 0:
                        return;
                    case 1:
                        //Summary
                        i = new Intent(c, SummaryActivity.class);
                        i.putExtra(DatabaseActivity.PARENTS_EXTRA, new String[]{});
                        i.putExtra(DatabaseActivity.DB_VALUES_EXTRA, new String[]{Integer.toString(event.getEventID())});
                        i.putExtra(DatabaseActivity.DB_KEYS_EXTRA, new String[]{DBContract.COL_EVENT_ID});
                        break;
                    case 2:
                        //Match Scedule
                        i = new Intent(c, MatchScheduleActivity.class);
                        i.putExtra(MatchScheduleActivity.EVENT_ID_EXTRA, Integer.toString(event.getEventID()));
                        break;
                    case 3:
                        //match Data
                        i = new Intent(c, RawMatchDataActivity.class);
                        i.putExtra(DatabaseActivity.PARENTS_EXTRA, new String[]{});
                        i.putExtra(DatabaseActivity.DB_VALUES_EXTRA, new String[]{Integer.toString(event.getEventID())});
                        i.putExtra(DatabaseActivity.DB_KEYS_EXTRA, new String[]{DBContract.COL_EVENT_ID});
                        i.putExtra(RawMatchDataActivity.LIMIT_LOADING_EXTRA, true);
                        break;
                    case 4:
                        break;
                    case 5:
                        //Attending Teams
                        //TODO MAKE FULL ACTIVITY NOT DIALOG
                        i = new Intent(c, AttendingTeamsDialogActivity.class);
                        i.putExtra(AttendingTeamsDialogActivity.GAME_NAME_EXTRA, event.getGameName());
                        i.putExtra(AttendingTeamsDialogActivity.EVENT_ID_EXTRA, Integer.toString(event.getEventID()));
                        break;
                    case 6:
                        //Import
                        ConnectivityManager connMgr = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                        if (networkInfo != null && networkInfo.isConnected()) {
                            Intent intent = new Intent(c, ImportDialogActivity.class);
                            intent.putExtra(ImportDialogActivity.EVENT_ID_EXTRA, event.getEventID());
                            c.startActivity(intent);
                        } else {
                            AlertDialog.Builder b = new AlertDialog.Builder(c);
                            b.setMessage("You must have internet connection to import event data " + "and OPR from the web.");
                            b.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            b.show();
                        }
                        return;
                }
                if (i != null)
                    c.startActivity(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return convertView;
    }
}
