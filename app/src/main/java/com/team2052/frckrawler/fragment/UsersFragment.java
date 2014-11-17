package com.team2052.frckrawler.fragment;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.db.User;
import com.team2052.frckrawler.fragment.dialog.AddUserDialogFragment;
import com.team2052.frckrawler.fragment.dialog.EditUserDialogFragment;
import com.team2052.frckrawler.listitems.ListElement;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.elements.UserListElement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam
 * @since 8/25/2014
 */
public class UsersFragment extends ListFragmentFab
{
    private ActionMode currentActionMode;
    private final ActionMode.Callback callback = new ActionMode.Callback()
    {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu)
        {
            long userId = Long.parseLong(((ListElement) mAdapter.getItem(currentSelectedListItem)).getKey());
            User user = mDaoSession.getUserDao().load(userId);
            mode.setTitle(user.getName());
            mode.getMenuInflater().inflate(R.menu.edit_delete_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu)
        {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item)
        {
            long userId = Long.parseLong(((ListElement) mAdapter.getItem(currentSelectedListItem)).getKey());
            User user = mDaoSession.getUserDao().load(userId);
            switch (item.getItemId()) {
                case R.id.menu_edit:
                    EditUserDialogFragment fragment = EditUserDialogFragment.newInstance(user);
                    fragment.show(getChildFragmentManager(), "editUser");
                    currentActionMode.finish();
                    return true;
                case R.id.menu_delete:
                    mDaoSession.getUserDao().delete(user);
                    currentActionMode.finish();
                    updateList();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode)
        {
            currentActionMode = null;
        }
    };
    private int currentSelectedListItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (currentActionMode != null) {
                    return false;
                }
                currentSelectedListItem = position;
                currentActionMode = ((ActionBarActivity) getActivity()).startSupportActionMode(callback);
                return true;
            }
        });
        mFab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new AddUserDialogFragment().show(getChildFragmentManager(), "addUser");
            }
        });
        return view;
    }

    @Override
    public void onDestroy()
    {
        if (currentActionMode != null)
            currentActionMode.finish();
        super.onDestroy();
    }

    @Override
    public void updateList()
    {
        new GetUsersTask().execute();
    }

    private class GetUsersTask extends AsyncTask<Void, Void, List<User>>
    {

        @Override
        protected List<User> doInBackground(Void... params)
        {
            return mDaoSession.getUserDao().loadAll();
        }

        @Override
        protected void onPostExecute(List<User> users)
        {
            List<ListItem> userList = new ArrayList<>();
            for (User user : users) {
                userList.add(new UserListElement(user));
            }
            mListView.setAdapter(mAdapter = new ListViewAdapter(getActivity(), userList));
        }
    }
}
