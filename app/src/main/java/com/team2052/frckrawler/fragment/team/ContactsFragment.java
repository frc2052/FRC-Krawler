package com.team2052.frckrawler.fragment.team;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.DatabaseActivity;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.db.Contact;
import com.team2052.frckrawler.db.ContactDao;
import com.team2052.frckrawler.db.Team;
import com.team2052.frckrawler.fragment.ListFragment;
import com.team2052.frckrawler.fragment.dialog.AddContactDialogFragment;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.elements.ContactListElement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam
 */
public class ContactsFragment extends ListFragment
{
    private Team mTeam;

    public static ContactsFragment newInstance(Team team)
    {
        ContactsFragment fragment = new ContactsFragment();
        Bundle b = new Bundle();
        b.putLong(DatabaseActivity.PARENT_ID, team.getNumber());
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void updateList()
    {
        new GetContactsTask().execute();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mTeam = mDaoSession.getTeamDao().load(getArguments().getLong(DatabaseActivity.PARENT_ID, 0));
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.addbutton, menu);
        menu.findItem(R.id.add_action).setIcon(R.drawable.ic_action_add_person);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.add_action) {
            AddContactDialogFragment.newInstance(mTeam).show(getChildFragmentManager(), "addContact");
        }
        return super.onOptionsItemSelected(item);
    }

    public class GetContactsTask extends AsyncTask<Void, Void, List<Contact>>
    {

        @Override
        protected List<Contact> doInBackground(Void... params)
        {
            return mDaoSession.getContactDao().queryBuilder().where(ContactDao.Properties.TeamId.eq(mTeam.getNumber())).list();
        }

        @Override
        protected void onPostExecute(List<Contact> contacts)
        {
            List<ListItem> listItems = new ArrayList<>();

            for (Contact contact : contacts) {
                //TODO List Item
                listItems.add(new ContactListElement(contact, ContactsFragment.this));
            }
            mAdapter = new ListViewAdapter(getActivity(), listItems);
            mListView.setAdapter(mAdapter);
        }
    }
}
