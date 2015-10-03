package com.team2052.frckrawler.background.scout;

import android.content.Context;
import android.os.AsyncTask;

import com.google.common.base.Optional;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.MetricHelper;
import com.team2052.frckrawler.database.MetricValue;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.MatchComment;
import com.team2052.frckrawler.db.MatchData;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.fragments.scout.ScoutMatchFragment;
import com.team2052.frckrawler.tba.JSON;
import com.team2052.frckrawler.views.metric.MetricWidget;

import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * @author Adam
 * @since 12/28/2014.
 * Used to auto fill the metrics so the scout can update the metric data
 */
public class PopulateMatchMetricsTask extends AsyncTask<Void, Void, Void> {

    private final Context context;
    private final DBManager mDbManager;
    private final ScoutMatchFragment mFragment;
    private final int match_num;
    private final int game_type;
    private final Robot robot;
    private final Event event;
    private final ArrayList<MetricValue> mMetricValues = new ArrayList<>();
    private MatchComment mMatchComment;

    public PopulateMatchMetricsTask(@NotNull ScoutMatchFragment fragment, @NotNull Event event, @NotNull Robot robot, @NotNull int match_num, @NotNull int match_type) {
        this.mFragment = fragment;
        this.match_num = match_num;
        this.game_type = match_type;
        this.robot = robot;
        this.context = fragment.getActivity();
        this.mDbManager = DBManager.getInstance(context);
        this.event = event;
    }

    @Override
    protected Void doInBackground(Void... params) {
        //Build the queries

        final QueryBuilder<MatchComment> matchCommentQueryBuilder = mDbManager.getMatchComments().query(match_num, game_type, robot.getId(), event.getId());

        final QueryBuilder<Metric> metricQueryBuilder = mDbManager.getMetricsTable().query(MetricHelper.MetricCategory.MATCH_PERF_METRICS.id, null, event.getGame_id());

        mDbManager.runInTx(() -> {

            List<Metric> metrics = metricQueryBuilder.list();

            for (Metric metric : metrics) {
                //Query for existing data
                QueryBuilder<MatchData> matchDataQueryBuilder = mDbManager.getMatchDataTable().query(robot.getId(), metric.getId(), match_num, game_type, event.getId(), null);
                MatchData currentData = matchDataQueryBuilder.unique();

                //Add the metric values
                mMetricValues.add(new MetricValue(metric, currentData == null ? null : JSON.getAsJsonObject(currentData.getData())));
            }
            mMatchComment = matchCommentQueryBuilder.unique();
        });
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mFragment.mMetricList.removeAllViews();
        List<MetricWidget> widgets = new ArrayList<>();

        for (MetricValue value : mMetricValues) {
            final Optional<MetricWidget> widget = MetricWidget.createWidget(context, value);
            if (widget.isPresent())
                widgets.add(widget.get());
        }

        mFragment.setWidgets(widgets);

        //Set the comment
        if (mMatchComment != null) {
            mFragment.setComment(mMatchComment.getComment());
        } else {
            mFragment.setComment("");
        }
    }
}
