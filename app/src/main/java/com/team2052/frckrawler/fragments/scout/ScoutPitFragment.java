package com.team2052.frckrawler.fragments.scout;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.background.scout.SavePitMetricsTask;
import com.team2052.frckrawler.database.MetricHelper;
import com.team2052.frckrawler.database.MetricValue;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.PitData;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.tba.JSON;
import com.team2052.frckrawler.util.SnackbarUtil;

import java.util.List;

import butterknife.Bind;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Adam
 */
public class ScoutPitFragment extends BaseScoutFragment {
    public static final String EVENT_ID = "EVENT_ID";
    public Event mEvent;
    @Bind(R.id.comments)
    public TextInputLayout mComments;

    public static ScoutPitFragment newInstance(Event event) {
        Bundle args = new Bundle();
        args.putLong(EVENT_ID, event.getId());
        ScoutPitFragment fragment = new ScoutPitFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scoutType = MetricHelper.ROBOT_METRICS;
    }

    public Observable<ScoutData> pitScoutDataObservable(Robot robot) {
        return Observable.just(robot).map(robotMap -> {
            ScoutData pitScoutData = new ScoutData();

            final QueryBuilder<Metric> metricQueryBuilder = dbManager.getMetricsTable().query(MetricHelper.MATCH_PERF_METRICS, null, mEvent.getGame_id());
            List<Metric> metrics = metricQueryBuilder.list();

            for (int i = 0; i < metrics.size(); i++) {
                Metric metric = metrics.get(i);
                //Query for existing data
                QueryBuilder<PitData> matchDataQueryBuilder = dbManager.getPitDataTable()
                        .query(robot.getId(), metric.getId(), mEvent.getId(), null);
                PitData currentData = matchDataQueryBuilder.unique();
                //Add the metric values
                pitScoutData.values.add(new MetricValue(metric, currentData == null ? null : JSON.getAsJsonObject(currentData.getData())));
            }
            pitScoutData.comments = robot.getComments();
            return pitScoutData;
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scouting_pit, container, false);
    }

    @Override
    public void inject() {
        mComponent.inject(this);
    }

    @Override
    protected void updateMetricList() {
        if (getRobot() != null)
            pitScoutDataObservable(getRobot())
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(scoutDataSubscriber());
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.scout, menu);
        menu.removeItem(R.id.action_view_match);
    }

    private String getComment() {
        return mComments.getEditText().getText().toString();
    }

    @Override
    protected void saveMetrics() {
        if (getRobot() == null || mEvent == null) {
            SnackbarUtil.make(getView(), getActivity().getString(R.string.something_seems_wrong), Snackbar.LENGTH_SHORT).show();
            return;
        }
        new SavePitMetricsTask(
                this,
                mEvent,
                getRobot(),
                getValues(),
                getComment(),
                null).execute();
    }


}

