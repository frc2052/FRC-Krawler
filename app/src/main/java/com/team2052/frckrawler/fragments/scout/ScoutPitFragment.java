package com.team2052.frckrawler.fragments.scout;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.Lists;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.background.scout.SavePitMetricsTask;
import com.team2052.frckrawler.comparators.RobotTeamNumberComparator;
import com.team2052.frckrawler.database.MetricHelper;
import com.team2052.frckrawler.database.MetricValue;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.PitData;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.db.RobotEvent;
import com.team2052.frckrawler.subscribers.BaseScoutData;
import com.team2052.frckrawler.tba.JSON;
import com.team2052.frckrawler.util.SnackbarUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import rx.Observable;

/**
 * @author Adam
 */
public class ScoutPitFragment extends BaseScoutFragment {
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

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    public Observable<? extends BaseScoutData> getObservable() {
        return Observable.create(subscriber -> {
            String comment = "";
            Robot robot = consumer.getSelectedRobot();
            List<MetricValue> metricValues = Lists.newArrayList();

            //Get Robots
            List<RobotEvent> robotEvents = dbManager.getEventsTable().getRobotEvents(mEvent);
            List<Robot> robots = Lists.newArrayList();
            for (int i = 0; i < robotEvents.size(); i++) {
                robots.add(robotEvents.get(i).getRobot());
            }
            Collections.sort(robots, new RobotTeamNumberComparator());

            if (robot != null) {
                final QueryBuilder<Metric> metricQueryBuilder = dbManager.getMetricsTable().query(scoutType, null, mEvent.getGame_id(), true);
                List<Metric> metrics = metricQueryBuilder.list();

                for (int i = 0; i < metrics.size(); i++) {
                    Metric metric = metrics.get(i);
                    //Query for existing data
                    QueryBuilder<PitData> matchDataQueryBuilder = dbManager.getPitDataTable()
                            .query(robot.getId(), metric.getId(), mEvent.getId(), null);
                    PitData currentData = matchDataQueryBuilder.unique();
                    //Add the metric values
                    metricValues.add(new MetricValue(metric, currentData == null ? null : JSON.getAsJsonObject(currentData.getData())));
                }
                comment = robot.getComments();
            }
            subscriber.onNext(new BaseScoutData(metricValues, robots, comment));
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_scouting_pit, container, false);
        consumer.setRootView(v);
        subscriber.bindViewsIfNeeded();
        return v;
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

    @Override
    protected void saveMetrics() {
        if (consumer.getSelectedRobot() == null || mEvent == null) {
            SnackbarUtil.make(getView(), getActivity().getString(R.string.something_seems_wrong), Snackbar.LENGTH_SHORT).show();
            return;
        }
        new SavePitMetricsTask(
                this,
                mEvent,
                consumer.getSelectedRobot(),
                consumer.getValues(),
                consumer.getComment(),
                null).execute();
    }


}

