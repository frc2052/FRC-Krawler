package com.team2052.frckrawler.database.metric;

import android.content.Context;
import android.support.annotation.Nullable;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.team2052.frckrawler.database.RxDBManager;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.MatchComment;
import com.team2052.frckrawler.db.MatchDatumDao;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.util.MetricHelper;
import com.team2052.frckrawler.util.PreferenceUtil;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Main Compiler functions for summaries, raw data, etc.
 */
public class Compiler {
    private static final String TAG = "Compiler";
    private Context context;
    public Func2<List<List<String>>, List<Metric>, List<List<String>>> summaryHeaderFunc = (data, metrics) -> {
        List<String> header = Lists.newArrayList();

        header.add("Team Number");

        if (PreferenceUtil.compileTeamNamesToExport(context)) {
            header.add("Team Names");
        }

        for (int i = 0; i < metrics.size(); i++) {
            Metric metric = metrics.get(i);
            header.add(metric.getName());
        }

        if (PreferenceUtil.compileMatchMetricsToExport(context)) {
            header.add("Match Comments");
        }

        if (PreferenceUtil.compilePitMetricsToExport(context)) {
            header.add("Robot Comments");
        }
        data.add(0, header);
        return data;
    };
    private RxDBManager rxDbManager;

    public Compiler(Context context, RxDBManager rxDbManager) {
        this.context = context;
        this.rxDbManager = rxDbManager;
    }

    /**
     * @return the list of metrics in respect to the preferences
     */
    public Observable<List<Metric>> getMetrics(long game_id) {
        Observable<List<Metric>> matchMetricsObservable = PreferenceUtil.compileMatchMetricsToExport(context) ? rxDbManager.metricsInGame(game_id, MetricHelper.MATCH_PERF_METRICS) : Observable.just(Lists.newArrayListWithCapacity(0));
        Observable<List<Metric>> pitMetricsObservable = PreferenceUtil.compilePitMetricsToExport(context) ? rxDbManager.metricsInGame(game_id, MetricHelper.ROBOT_METRICS) : Observable.just(Lists.newArrayListWithCapacity(0));
        return Observable.combineLatest(matchMetricsObservable, pitMetricsObservable, (matchMetrics, pitMetrics) -> {
            List<Metric> metrics = Lists.newArrayListWithCapacity(matchMetrics.size() + pitMetrics.size());
            metrics.addAll(matchMetrics);
            metrics.addAll(pitMetrics);
            return metrics;
        });
    }

    /**
     * @return A summary for every robot in an event
     */
    public Observable<List<CompiledMetricValue>> getMetricEventSummary(Event event, Metric metric) {
        float compileWeight = getCompileWeight();
        return Observable.just(event)
                .flatMap(event1 -> Observable.from(rxDbManager.getEventsTable().getRobots(event1)))
                .concatMap(robot -> getRobotMetricSummary(event.getId(), metric, robot, compileWeight).subscribeOn(Schedulers.computation()))
                .toSortedList((cmv, cmv1) -> Double.compare(cmv.getRobot().getTeam_id(), cmv1.getRobot().getTeam_id()));
    }

    /**
     * @return A summary of a robot throughout every event
     */
    public Observable<List<MetricValue>> getRobotMetricData(Metric metric, Robot robot) {
        return getRobotMetricData(null, metric, robot);
    }

    /**
     * @return Data for a robot at an event
     */
    public Observable<List<MetricValue>> getRobotMetricData(@Nullable Long event_id, Metric metric, Robot robot) {
        return Observable.defer(() -> {
            Observable<List<MetricValue>> metricValueListObservable = Observable.empty();
            if (metric.getCategory() == MetricHelper.MATCH_PERF_METRICS) {
                metricValueListObservable = rxDbManager.getMatchDataTable()
                        .query(robot.getId(), metric.getId(), null, MetricHelper.MATCH_GAME_TYPE, event_id)
                        .orderAsc(MatchDatumDao.Properties.Match_number)
                        .rx()
                        .list()
                        .concatMap(Observable::from)
                        .map(MetricHelper.mapMatchDataToMetricValue)
                        .toList();
            } else if (metric.getCategory() == MetricHelper.ROBOT_METRICS) {
                metricValueListObservable = rxDbManager.getPitDataTable().query(robot.getId(), metric.getId(), event_id)
                        .rx()
                        .list()
                        .concatMap(Observable::from)
                        .map(MetricHelper.mapPitDataToMetricValue)
                        .toList();
            }
            return metricValueListObservable;
        });
    }

    public Observable<CompiledMetricValue> getRobotMetricSummary(@Nullable Long event_id, Metric metric, Robot robot, float compileWeight) {
        return getRobotMetricData(event_id, metric, robot).map(CompileUtil.metricValuesToCompiledValue(robot, metric, compileWeight));
    }

    public Observable<CompiledMetricValue> getRobotMetricSummary(Metric metric, Robot robot, float compileWeight) {
        return getRobotMetricData(null, metric, robot).map(CompileUtil.metricValuesToCompiledValue(robot, metric, compileWeight));
    }

    public List<String> getRobotMatchComments(Event event, Robot robot) {
        List<String> comments = Lists.newArrayList();
        QueryBuilder<MatchComment> matchCommentQueryBuilder = rxDbManager.getMatchCommentsTable().query(null, null, robot.getId(), event.getId());
        for (MatchComment matchComment : matchCommentQueryBuilder.list()) {
            comments.add(matchComment.getMatch_number() + ": " + matchComment.getComment());
        }
        return comments;
    }

    public Observable<List<Long>> getMatchNumbers(Event event, Robot robot) {
        return rxDbManager.getMatchDataTable().query(robot.getId(), null, null, null, event.getId())
                .orderAsc(MatchDatumDao.Properties.Match_number)
                .rx()
                .list()
                .map(rxDbManager.getMatchDataTable().mapMatchDataToMatchNumbers)
                .concatMap(Observable::from)
                .toSortedList();
    }

    public Observable<List<List<String>>> getSummaryExport(Event event) {
        final float compileWeight = getCompileWeight();
        final boolean compileTeamNames = PreferenceUtil.compileTeamNamesToExport(context);
        final boolean compileMatchMetric = PreferenceUtil.compileMatchMetricsToExport(context);
        final boolean compilePitMetric = PreferenceUtil.compilePitMetricsToExport(context);

        final Scheduler scheduler = Schedulers.from(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));

        return getMetrics(event.getGame_id())
                .concatMap(metrics -> rxDbManager.robotsAtEvent(event.getId())
                        .flatMap(Observable::from)
                        .flatMap(robot -> Observable.from(metrics)
                                .concatMap(metric -> getRobotMetricSummary(event.getId(), metric, robot, compileWeight).map(CompileUtil.compiledMetricValueToString))
                                .toList()
                                .map(list -> {
                                    if (compileTeamNames) {
                                        list.add(0, robot.getTeam().getName());
                                    }
                                    list.add(0, String.valueOf(robot.getTeam_id()));
                                    if (compileMatchMetric) {
                                        list.add(Joiner.on(", ").join(getRobotMatchComments(event, robot)));
                                    }
                                    if (compilePitMetric) {
                                        list.add(robot.getComments());
                                    }
                                    return list;
                                })
                                .subscribeOn(scheduler))
                        .toList()
                        .map(lists -> {
                            Collections.sort(lists, (strings1, strings) -> Double.compare(Integer.parseInt(strings1.get(0)), Integer.parseInt(strings.get(0))));
                            return lists;
                        })
                        .zipWith(getMetrics(event.getGame_id()), summaryHeaderFunc)
                );
    }

    public Observable<List<CompiledMetricValue>> getCompiledRobotSummary(long robot_id, Long event_id) {
        return Observable.just(robot_id)
                .map(rxDbManager.getRobotsTable().mapIdToModel)
                .concatMap(robot -> getMetrics(robot.getGame_id())
                        .concatMap(Observable::from)
                        .concatMap(metric -> getRobotMetricSummary(event_id, metric, robot))
                        .toList());
    }

    public Observable<AbstractMap.SimpleEntry<String, String>> getCompiledMetricValueKayValue(Observable<CompiledMetricValue> compiledMetricValueObservable) {
        return compiledMetricValueObservable.map(CompileUtil.mapCompiledMetricValueToKeyValue);
    }

    public Observable<Map<String, String>> getCompiledMetricToHashMap(Observable<List<CompiledMetricValue>> metricValueObservable) {
        return metricValueObservable
                .concatMap(Observable::from)
                .map(CompileUtil.mapCompiledMetricValueToKeyValue)
                .toList()
                .map(CompileUtil.mapEntriesToMap);
    }

    public Observable<CompiledMetricValue> getRobotMetricSummary(Long event_id, Metric metric, Robot robot) {
        return getRobotMetricSummary(event_id, metric, robot, getCompileWeight());
    }

    public float getCompileWeight() {
        return PreferenceUtil.compileWeight(context);
    }

    //Todo: Optimize
    public Observable<List<List<String>>> getRawExport(Event event) {
        final Scheduler scheduler = Schedulers.from(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
        return Observable.just(event.getId()).flatMap(eventId -> {
            final List<Metric> metrics = rxDbManager.getMetricsTable().query(MetricHelper.MATCH_PERF_METRICS, null, event.getGame_id(), null).list();
            return rxDbManager.robotsAtEvent(eventId)
                    .flatMap(Observable::from)
                    .concatMap(robot -> getMatchNumbers(event, robot)
                            .concatMap(Observable::from)
                            .concatMap(matchNumber ->
                                    Observable.from(metrics)
                                            .map(metric -> rxDbManager.getMatchDataTable().query(robot.getId(), metric.getId(), matchNumber, null, eventId).unique())
                                            .map(CompileUtil.convertMatchDataToStringFunc)
                                            .toList()
                                            .map(record -> {
                                                record.add(0, String.valueOf(matchNumber));
                                                record.add(0, String.valueOf(robot.getTeam_id()));
                                                MatchComment comment = rxDbManager.getMatchCommentsTable().query(matchNumber, null, robot.getId(), eventId).unique();
                                                if (comment != null) {
                                                    record.add(comment.getComment());
                                                }
                                                return record;
                                            })
                            )
                            .subscribeOn(scheduler))
                    .toList()
                    .map(lists -> {
                        List<String> header = Lists.newArrayList();

                        header.add("Team Number");
                        header.add("Match Number");

                        for (int i = 0; i < metrics.size(); i++) {
                            Metric metric = metrics.get(i);
                            header.add(metric.getName());
                        }

                        header.add("Match Comment");
                        lists.add(0, header);
                        return lists;
                    });
        });
    }
}
