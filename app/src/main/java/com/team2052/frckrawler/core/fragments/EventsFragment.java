package com.team2052.frckrawler.core.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.core.activities.DatabaseActivity;
import com.team2052.frckrawler.core.activities.EventInfoActivity;
import com.team2052.frckrawler.core.adapters.ListViewAdapter;
import com.team2052.frckrawler.core.database.DBManager;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.EventDao;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.core.fragments.dialog.AddEventDialogFragment;
import com.team2052.frckrawler.core.fragments.dialog.ImportDataSimpleDialogFragment;
import com.team2052.frckrawler.core.listeners.FABButtonListener;
import com.team2052.frckrawler.core.listitems.ListElement;
import com.team2052.frckrawler.core.listitems.ListItem;
import com.team2052.frckrawler.core.listitems.elements.EventListElement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam
 * @since 10/15/2014
 */
public class EventsFragment extends ListFragment implements FABButtonListener {
    private Game mGame;
    private int mCurrentSelectedItem;
    private ActionMode mCurrentActionMode;
    private final ActionMode.Callback callback = new ActionMode.Callback() {
        Event event;

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            long eventId = Long.parseLong(((ListElement) mAdapter.getItem(mCurrentSelectedItem)).getKey());
            event = mDaoSession.getEventDao().load(eventId);
            actionMode.getMenuInflater().inflate(R.menu.edit_delete_menu, menu);
            menu.removeItem(R.id.menu_edit);
            actionMode.setTitle(event.getName());
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.menu_delete:
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Are you sure you want to remove this event and all its data?");
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mDaoSession.runInTx(new Runnable() {
                                @Override
                                public void run() {
                                    DBManager.getInstance(getActivity(), mDaoSession).deleteEvent(event);
                                }
                            });
                            dialogInterface.dismiss();
                            updateList();
                            actionMode.finish();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
                    break;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            mCurrentActionMode = null;
        }
    };

    public static EventsFragment newInstance(Game game) {
        EventsFragment fragment = new EventsFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(DatabaseActivity.PARENT_ID, game.getId());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void preUpdateList() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(EventInfoActivity.newInstance(getActivity(), mDaoSession.getEventDao().load(Long.valueOf(((ListElement) mAdapter.getItem(i)).getKey()))));
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mCurrentActionMode != null) return false;
                mCurrentSelectedItem = i;
                mCurrentActionMode = ((ActionBarActivity) getActivity()).startSupportActionMode(callback);
                return true;
            }
        });
        mGame = mDaoSession.getGameDao().load(getArguments().getLong(DatabaseActivity.PARENT_ID, 0));
    }

    @Override
    public void onAttach(Activity activity) {
        setHasOptionsMenu(true);
        super.onAttach(activity);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.event_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_event) {
            AddEventDialogFragment.newInstance(mGame).show(getChildFragmentManager(), "addEventDialog");
        }
        return false;
    }

    @Override
    public void updateList() {
        new GetEventsTask().execute();
    }

    @Override
    public void onFABPressed() {
        ImportDataSimpleDialogFragment.newInstance(mGame).show(getChildFragmentManager(), "importEvent");
    }

    private class GetEventsTask extends AsyncTask<Void, Void, List<Event>> {

        @Override
        protected List<Event> doInBackground(Void... params) {
            return mDaoSession.getEventDao().queryBuilder().where(EventDao.Properties.GameId.eq(mGame.getId())).list();
        }

        @Override
        protected void onPostExecute(List<Event> events) {
            ArrayList<ListItem> eventList = new ArrayList<>();
            for (Event event : events) {
                eventList.add(new EventListElement(event));
            }
            mListView.setAdapter(mAdapter = new ListViewAdapter(getActivity(), eventList));
        }
    }
}
