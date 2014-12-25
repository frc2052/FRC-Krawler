package com.team2052.frckrawler.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.bluetooth.scout.LoginHandler;
import com.team2052.frckrawler.database.MetricValue;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.MetricDao;
import com.team2052.frckrawler.db.PitData;
import com.team2052.frckrawler.db.PitDataDao;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.db.RobotEvent;
import com.team2052.frckrawler.db.RobotEventDao;
import com.team2052.frckrawler.events.scout.ScoutSyncSuccessEvent;
import com.team2052.frckrawler.fragment.BaseFragment;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.elements.SimpleListElement;
import com.team2052.frckrawler.util.MetricUtil;
import com.team2052.frckrawler.util.ScoutUtil;
import com.team2052.frckrawler.view.metric.MetricWidget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.event.EventBus;

/**
 * @author Adam
 */
public class ScoutPitFragment extends BaseFragment implements AdapterView.OnItemSelectedListener
{
    private Event mEvent;
    private Spinner mTeamSpinner;
    private List<RobotEvent> mRobots;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        EventBus.getDefault().register(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.action_save) {
            if (mTeamSpinner.getSelectedItem() != null) {
                new SaveAllMetrics().execute();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadAllData(Event event)
    {
        if (event == null) {
            setErrorVisible(true);
            return;
        }

        mEvent = event;
        new GetAllRobots().execute();
    }

    private void setErrorVisible(boolean visible)
    {
        if (visible) {
            getView().findViewById(R.id.error_message).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.scroll_view).setVisibility(View.GONE);
        } else {
            getView().findViewById(R.id.error_message).setVisibility(View.GONE);
            getView().findViewById(R.id.scroll_view).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.scout, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_scouting_pit, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mTeamSpinner = (Spinner) view.findViewById(R.id.team);
        mTeamSpinner.setOnItemSelectedListener(this);
        loadAllData(ScoutUtil.getScoutEvent(getActivity(), mDaoSession));
    }

    @Override
    public void onDestroy()
    {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @SuppressWarnings("unused")
    public void onEvent(ScoutSyncSuccessEvent event)
    {
        loadAllData(ScoutUtil.getScoutEvent(getActivity(), mDaoSession));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        new GetAllMetrics().execute();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }

    public class GetAllRobots extends AsyncTask<Void, Void, List<RobotEvent>>
    {
        @Override
        protected List<RobotEvent> doInBackground(Void... params)
        {
            return mDaoSession.getRobotEventDao().queryBuilder().where(RobotEventDao.Properties.EventId.eq(mEvent.getId())).list();
        }

        @Override
        protected void onPostExecute(List<RobotEvent> robotEventses)
        {
            //Sort by Team number
            Collections.sort(robotEventses, new Comparator<RobotEvent>()
            {
                @Override
                public int compare(RobotEvent lhs, RobotEvent rhs)
                {
                    return Double.compare(lhs.getRobot().getTeam().getNumber(), rhs.getRobot().getTeam().getNumber());
                }
            });

            mRobots = robotEventses;

            List<ListItem> listItems = new ArrayList<>();

            for (RobotEvent robotEvents : robotEventses) {
                listItems.add(new SimpleListElement(Long.toString(robotEvents.getRobot().getTeam().getNumber()) + " - " + robotEvents.getRobot().getTeam().getName(), robotEvents.getRobot().getTeam().getTeamkey()));
            }
            mTeamSpinner.setAdapter(new ListViewAdapter(getActivity(), listItems));
            new GetAllMetrics().execute();
        }
    }

    public class GetAllMetrics extends AsyncTask<Void, Void, List<MetricValue>>
    {

        @Override
        protected List<MetricValue> doInBackground(Void... params)
        {
            QueryBuilder<PitData> pitDataQueryBuilder = mDaoSession.getPitDataDao().queryBuilder();

            pitDataQueryBuilder.where(PitDataDao.Properties.RobotId.eq(mRobots.get(mTeamSpinner.getSelectedItemPosition()).getRobot().getId()));
            List<PitData> list = pitDataQueryBuilder.list();
            List<MetricValue> metricValues = new ArrayList<>();

            for (PitData pitData : list) {
                metricValues.add(new MetricValue(pitData));
            }

            if (metricValues.size() <= 0) {
                QueryBuilder<Metric> metricQueryBuilder = mDaoSession.getMetricDao().queryBuilder();
                metricQueryBuilder.where(MetricDao.Properties.GameId.eq(mEvent.getGame().getId()));
                metricQueryBuilder.where(MetricDao.Properties.Category.eq(MetricUtil.MetricType.ROBOT_METRICS.ordinal()));

                for (Metric metric : metricQueryBuilder.list()) {
                    metricValues.add(new MetricValue(metric, null));
                }

            }

            return metricValues;
        }

        @Override
        protected void onPostExecute(List<MetricValue> metrics)
        {
            ((LinearLayout) getView().findViewById(R.id.metricWidgetList)).removeAllViews();
            for (MetricValue metric : metrics) {
                ((LinearLayout) getView().findViewById(R.id.metricWidgetList)).addView(MetricWidget.createWidget(getActivity(), metric));
            }
            ((EditText) getView().findViewById(R.id.comments)).setText(mRobots.get(mTeamSpinner.getSelectedItemPosition()).getRobot().getComments());
            setErrorVisible(false);
        }
    }

    public class SaveAllMetrics extends AsyncTask<Void, Void, Integer>
    {

        @Override
        protected Integer doInBackground(Void... params)
        {
            //Get data from view
            Robot robot = mRobots.get(mTeamSpinner.getSelectedItemPosition()).getRobot();

            LoginHandler loginHandler = LoginHandler.getInstance(getActivity(), mDaoSession);

            if (!loginHandler.isLoggedOn() && !loginHandler.loggedOnUserStillExists()) {
                loginHandler.login();
            }

            LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.metricWidgetList);
            List<MetricWidget> widgets = new ArrayList<>();

            for (int i = 0; i < linearLayout.getChildCount(); i++) {
                widgets.add((MetricWidget) linearLayout.getChildAt(i));
            }

            //Begin Saving
            for (MetricWidget widget : widgets) {
                if (mDaoSession.getPitDataDao().queryBuilder().where(PitDataDao.Properties.RobotId.eq(robot.getId())).where(PitDataDao.Properties.MetricId.eq(widget.getMetric().getId())).list().size() <= 0)
                    mDaoSession.getPitDataDao().insert(new PitData(widget.getValues(), robot.getId(), widget.getMetric().getId(), mEvent.getId(), loginHandler.getLoggedOnUser().getId()));
                else {
                    List<PitData> pitdata = mDaoSession.getPitDataDao().queryBuilder().where(PitDataDao.Properties.RobotId.eq(robot.getId())).where(PitDataDao.Properties.MetricId.eq(widget.getMetric().getId())).list();
                    for (PitData data : pitdata) {
                        data.delete();
                    }
                    mDaoSession.getPitDataDao().insert(new PitData(widget.getValues(), robot.getId(), widget.getMetric().getId(), mEvent.getId(), loginHandler.getLoggedOnUser().getId()));
                }
            }

            robot.setComments(((EditText) getView().findViewById(R.id.comments)).getText().toString());
            robot.update();
            return 0;
        }

        @Override
        protected void onPostExecute(Integer aVoid)
        {
            Toast.makeText(getActivity(), aVoid == 0 ? "Save Complete!" : "Cannot Save Match Data. Match Is Already Saved", Toast.LENGTH_LONG).show();
        }
    }
}
