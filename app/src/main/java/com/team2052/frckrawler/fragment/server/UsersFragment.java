package com.team2052.frckrawler.fragment.server;

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

import com.activeandroid.query.Select;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.database.models.User;
import com.team2052.frckrawler.fragment.dialog.AddUserDialogFragment;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.UserListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam on 8/25/2014.
 */
public class UsersFragment extends Fragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.addbutton, menu);
        menu.findItem(R.id.add_action).setIcon(R.drawable.ic_action_add_person);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_action) {
            AddUserDialogFragment fragment = new AddUserDialogFragment();
            fragment.show(getFragmentManager(), "Add User");
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

    public void updateUsers() {
        new GetUsersTask().execute();
    }

    private class GetUsersTask extends AsyncTask<Void, Void, List<User>> {

        @Override
        protected List<User> doInBackground(Void... params) {
            return new Select().from(User.class).execute();
        }

        @Override
        protected void onPostExecute(List<User> users) {
            List<ListItem> userList = new ArrayList<ListItem>();
            for (User user : users) {
                userList.add(new UserListItem(user));
            }
            ((ListView) getView().findViewById(R.id.users_list_view)).setAdapter(new ListViewAdapter(getActivity(), userList));
        }
    }
}
