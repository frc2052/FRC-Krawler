package com.team2052.frckrawler.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.dialog.AddUserDialogActivity;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.User;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.UserListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam on 8/25/2014.
 */
public class UsersFragment extends Fragment {
    private DBManager dbManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbManager = DBManager.getInstance(getActivity());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.addbutton, menu);
        menu.findItem(R.id.add_metric_action).setIcon(R.drawable.ic_action_add_person);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_metric_action) {
            Intent i = new Intent(getActivity(), AddUserDialogActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_users, null);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        new GetUsersTask().execute();
    }

    private class GetUsersTask extends AsyncTask<Void, Void, User[]> {

        @Override
        protected User[] doInBackground(Void... params) {
            return dbManager.getAllUsers();
        }

        @Override
        protected void onPostExecute(User[] users) {
            List<ListItem> userList = new ArrayList<ListItem>();
            for (User user : users) {
                userList.add(new UserListItem(user));
            }
            ((ListView) getView().findViewById(R.id.users_list_view)).setAdapter(new ListViewAdapter(getActivity(), userList));
        }
    }
}
