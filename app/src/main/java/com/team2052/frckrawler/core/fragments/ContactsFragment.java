package com.team2052.frckrawler.core.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team2052.frckrawler.core.activities.DatabaseActivity;
import com.team2052.frckrawler.core.adapters.ListViewAdapter;
import com.team2052.frckrawler.db.Contact;
import com.team2052.frckrawler.db.ContactDao;
import com.team2052.frckrawler.db.Team;
import com.team2052.frckrawler.core.fragments.dialog.AddContactDialogFragment;
import com.team2052.frckrawler.core.listitems.ListItem;
import com.team2052.frckrawler.core.listitems.elements.ContactListElement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam
 */
public class ContactsFragment extends ListFragmentFab {
    private Team mTeam;

    public static ContactsFragment newInstance(Team team) {
        ContactsFragment fragment = new ContactsFragment();
        Bundle b = new Bundle();
        b.putLong(DatabaseActivity.PARENT_ID, team.getNumber());
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void updateList() {
        new GetContactsTask().execute();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTeam = mDaoSession.getTeamDao().load(getArguments().getLong(DatabaseActivity.PARENT_ID, 0));
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddContactDialogFragment.newInstance(mTeam).show(getChildFragmentManager(), "addContact");
            }
        });
        return v;
    }

    public class GetContactsTask extends AsyncTask<Void, Void, List<Contact>> {

        @Override
        protected List<Contact> doInBackground(Void... params) {
            return mDaoSession.getContactDao().queryBuilder().where(ContactDao.Properties.TeamId.eq(mTeam.getNumber())).list();
        }

        @Override
        protected void onPostExecute(List<Contact> contacts) {
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
