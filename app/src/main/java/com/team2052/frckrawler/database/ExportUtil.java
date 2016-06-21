package com.team2052.frckrawler.database;

import android.content.Context;
import android.util.Log;

import com.google.common.collect.Lists;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.MatchComment;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.db.Team;
import com.team2052.frckrawler.util.PreferenceUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;
import de.greenrobot.dao.query.QueryBuilder;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author Adam
 * @since 10/4/2014
 */
public class ExportUtil {
    private static final String TAG = "ExportUtil";

    public static File exportEventDataToCSV(Context context, Event event, File location) {
        return location;
    }

    public static Observable<List<List<String>>> getFullExport(Context context, Event event) {
        DBManager dbManager = DBManager.getInstance(context);
        float compileWeight = PreferenceUtil.compileWeight(context);
        final Observable<List<Metric>> matchMetricListObservable = PreferenceUtil.compileMatchMetricsToExport(context) ? dbManager.metricsInGame(event.getGame_id(), MetricHelper.MATCH_PERF_METRICS) : Observable.just(Lists.newArrayList());
        final Observable<List<Metric>> robotMetricListObservable = PreferenceUtil.compilePitMetricsToExport(context) ? dbManager.metricsInGame(event.getGame_id(), MetricHelper.ROBOT_METRICS) : Observable.just(Lists.newArrayList());
        final Observable<List<Metric>> metricsObservable = Observable.merge(matchMetricListObservable, robotMetricListObservable);
        Observable<List<Robot>> robotObservable = dbManager.robotsAtEvent(event.getId());
        Observable<Float> compileWeightObservable = Observable.defer(() -> Observable.just(PreferenceUtil.compileWeight(context)));


        Observable<Boolean> compileTeamNamesObservable = Observable.defer(() -> Observable.just(PreferenceUtil.compileTeamNamesToExport(context)));


        return Observable.combineLatest(Observable.just(event), metricsObservable, robotObservable, compileWeightObservable, compileTeamNamesObservable, FullExportData::new)
                .flatMap(fullExportData -> {
                    Observable<List<List<String>>> robotDataObservable = Observable.from(fullExportData.robots)
                            .flatMap(robotListItem -> Observable.just(robotListItem)
                                    .map(robot -> buildRecord(dbManager, robot, fullExportData.metrics, fullExportData.event, fullExportData.compileWeight))
                                    .subscribeOn(Schedulers.computation())).toList();
                    return robotDataObservable;
                });
    }

    public static class FullExportData {
        Event event;
        List<Metric> metrics;
        List<Robot> robots;
        float compileWeight;
        boolean compileTeamNames;

        public FullExportData(Event event, List<Metric> metrics, List<Robot> robots, float compileWeight, boolean compileTeamNames) {
            this.event = event;
            this.metrics = metrics;
            this.robots = robots;
            this.compileWeight = compileWeight;
            this.compileTeamNames = compileTeamNames;
        }
    }

    public static class RXIOException extends RuntimeException {
        public RXIOException(IOException throwable) {
            super(throwable);
        }
    }

    private static List<String> buildRecord(DBManager dbManager, Robot robot, List<Metric> metrics, Event event, float compileWeight) {
        Log.d(TAG, "buildRecord: " + Thread.currentThread().getName());
        List<String> record = Lists.newArrayList();

        Team team = dbManager.getRobotsTable().getTeam(robot);

        record.add(String.valueOf(team.getNumber()));
        record.add(team.getName());

        QueryBuilder<MatchComment> matchCommentQueryBuilder = dbManager.getMatchComments().query(null, null, robot.getId(), event.getId());
        String comments = "";
        for (MatchComment matchComment : matchCommentQueryBuilder.list()) {
            comments += matchComment.getMatch_number() + ": " + matchComment.getComment() + ", ";
        }

        record.add(comments);
        record.add(robot.getComments());

        List<CompiledMetricValue> compiledRobot = MetricCompiler.getCompiledRobot(event, robot, dbManager, compileWeight);
        for (CompiledMetricValue metricValue : compiledRobot) {
            record.add(metricValue.getCompiledValue());
        }

        return record;
    }
}
