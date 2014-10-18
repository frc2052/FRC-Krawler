package com.team2052.frckrawler.fragment.game;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.DatabaseActivity;
import com.team2052.frckrawler.activity.EventInfoActivity;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.fragment.ListFragment;
import com.team2052.frckrawler.fragment.dialog.ImportDataSimpleDialogFragment;
import com.team2052.frckrawler.listitems.ListElement;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.elements.EventListElement;

import java.util.ArrayList;
import java.util.List;

import frckrawler.Event;
import frckrawler.EventDao;
import frckrawler.Game;

/**
 * @author Adam
 * @since 10/15/2014
 */
public class EventsFragment extends ListFragment
{
    private Game mGame;

    private int mCurrentSelectedItem;
    private ActionMode mCurrentActionMode;
    private final ActionMode.Callback callback = new ActionMode.Callback()
    {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu)
        {
            long eventId = Long.parseLong(((ListElement) mAdapter.getItem(mCurrentSelectedItem)).getKey());
            Event event = mDaoSession.getEventDao().load(eventId);
            actionMode.getMenuInflater().inflate(R.menu.edit_delete_menu, menu);
            menu.removeItem(R.id.menu_edit);
            actionMode.setTitle(event.getName());
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu)
        {
            return false;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode actionMode, MenuItem menuItem)
        {
            long eventId = Long.parseLong(((ListElement) mAdapter.getItem(mCurrentSelectedItem)).getKey());
            final Event event = mDaoSession.getEventDao().load(eventId);
            switch (menuItem.getItemId()) {
                case R.id.menu_delete:
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Are you sure you want to remove this event and all its data?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            mDaoSession.runInTx(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    DBManager.deleteEvent(mDaoSession, event);
                                }
                            });
                            dialogInterface.dismiss();
                            updateList();
                            actionMode.finish();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
                    break;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode)
        {
            mCurrentActionMode = null;
        }
    };

    public static EventsFragment newInstance(Game game)
    {
        EventsFragment fragment = new EventsFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(DatabaseActivity.PARENT_ID, game.getId());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void preUpdateList()
    {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                startActivity(EventInfoActivity.newInstance(getActivity(), mDaoSession.getEventDao().load(Long.valueOf(((ListElement) mAdapter.getItem(i)).getKey()))));
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                if (mCurrentActionMode != null) return false;
                mCurrentSelectedItem = i;
                mCurrentActionMode = getActivity().startActionMode(callback);
                return true;
            }
        });
        mGame = mDaoSession.getGameDao().load(getArguments().getLong(DatabaseActivity.PARENT_ID, 0));
    }

    @Override
    public void onAttach(Activity activity)
    {
        setHasOptionsMenu(true);
        super.onAttach(activity);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            inflater.inflate(R.menu.addbutton, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.add_action) {
            ImportDataSimpleDialogFragment.newInstance(mGame).show(getChildFragmentManager(), "importEvent");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updateList()
    {
        new GetEventsTask().execute();
    }

    private class GetEventsTask extends AsyncTask<Void, Void, List<Event>>
    {

        @Override
        protected List<Event> doInBackground(Void... params)
        {
            return mDaoSession.getEventDao().queryBuilder().where(EventDao.Properties.GameId.eq(mGame.getId())).list();
        }

        @Override
        protected void onPostExecute(List<Event> events)
        {
            ArrayList<ListItem> eventList = new ArrayList<>();
            for (Event event : events) {
                eventList.add(new EventListElement(event));
            }
            mListView.setAdapter(mAdapter = new ListViewAdapter(getActivity(), eventList));
        }
    }
}
