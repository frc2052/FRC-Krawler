package com.team2052.frckrawler.fragments.event;

import android.os.Bundle;
import android.view.View;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.DatabaseActivity;
import com.team2052.frckrawler.activities.EventInfoActivity;
import com.team2052.frckrawler.adapters.items.smart.EventItemView;
import com.team2052.frckrawler.adapters.items.smart.SmartAdapterInteractions;
import com.team2052.frckrawler.di.binding.NoDataParams;
import com.team2052.frckrawler.di.binding.RecyclerViewBinder;
import com.team2052.frckrawler.fragments.RecyclerViewFragment;
import com.team2052.frckrawler.fragments.dialog.ImportDataSimpleDialogFragment;
import com.team2052.frckrawler.models.Event;

import java.util.List;

import io.nlopez.smartadapters.SmartAdapter;
import rx.Observable;

/**
 * Displays events from a game_id
 */
public class EventsInGameFragment extends RecyclerViewFragment<List<Event>, RecyclerViewBinder> implements View.OnClickListener {
    private long mGame_id;

    public static EventsInGameFragment newInstance(long game_id) {
        EventsInGameFragment fragment = new EventsInGameFragment();
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
    protected NoDataParams getNoDataParams() {
        return new NoDataParams("No events found", R.drawable.ic_event);
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
