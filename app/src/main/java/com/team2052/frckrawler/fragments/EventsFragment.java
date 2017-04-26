package com.team2052.frckrawler.fragments;

import android.os.Bundle;
import android.view.View;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.DatabaseActivity;
import com.team2052.frckrawler.activities.EventInfoActivity;
import com.team2052.frckrawler.binding.ListViewNoDataParams;
import com.team2052.frckrawler.binding.RecyclerViewBinder;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.fragments.dialog.ImportDataSimpleDialogFragment;
import com.team2052.frckrawler.listitems.smart.EventItemView;
import com.team2052.frckrawler.listitems.smart.SmartAdapterInteractions;

import java.util.List;

import io.nlopez.smartadapters.SmartAdapter;
import rx.Observable;

/**
 * Created by Adam on 11/17/2015.
 */
public class EventsFragment extends RecyclerViewFragment<List<Event>, RecyclerViewBinder> implements View.OnClickListener {
    private long mGame_id;

    public static EventsFragment newInstance(long game_id) {
        EventsFragment fragment = new EventsFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(DatabaseActivity.PARENT_ID, game_id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void inject() {
        mComponent.inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mGame_id = getArguments().getLong(DatabaseActivity.PARENT_ID, 0);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Observable<? extends List<Event>> getObservable() {
        return rxDbManager.eventsByGame(mGame_id);
    }

    @Override
    protected ListViewNoDataParams getNoDataParams() {
        return new ListViewNoDataParams("No events found", R.drawable.ic_event);
    }

    @Override
    public void provideAdapterCreator(SmartAdapter.MultiAdaptersCreator creator) {
        creator.map(Event.class, EventItemView.class);
        creator.listener((actionId, item, position, view) -> {
            if (actionId == SmartAdapterInteractions.EVENT_CLICKED && item instanceof Event) {
                Event event = (Event) item;
                startActivity(EventInfoActivity.newInstance(getActivity(), event.getId()));
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.floating_action_button) {
            ImportDataSimpleDialogFragment.newInstance(mGame_id).show(getChildFragmentManager(), "importEvent");
        }
    }
}
