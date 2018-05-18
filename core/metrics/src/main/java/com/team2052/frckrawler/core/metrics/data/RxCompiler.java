package com.team2052.frckrawler.core.metrics.data;

import android.content.Context;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.team2052.frckrawler.core.data.models.RxDBManager;
import com.team2052.frckrawler.core.common.PreferenceHelper;
import com.team2052.frckrawler.core.metrics.MetricDataHelper;
import com.team2052.frckrawler.core.common.MetricHelper;
import com.team2052.frckrawler.core.data.models.MatchComment;
import com.team2052.frckrawler.core.data.models.MatchDatumDao;
import com.team2052.frckrawler.core.data.models.Metric;
import com.team2052.frckrawler.core.data.models.Team;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class RxCompiler {
    private static final String TAG = "Compiler";
    private Context context;
    public Func2<List<List<String>>, List<Metric>, List<List<String>>> summaryHeaderFunc = (data, metrics) -> {
        List<String> header = Lists.newArrayList();

        header.add("Team Number");

        if (PreferenceHelper.compileTeamNamesToExport(context)) {
            header.add("Team Names");
        }

        for (int i = 0; i < metrics.size(); i++) {
            Metric metric = metrics.get(i);
            header.add(metric.getName());
        }

        if (PreferenceHelper.compileMatchMetricsToExport(context)) {
            header.add("Match Comments");
        }

        if (PreferenceHelper.compilePitMetricsToExport(context)) {
            header.add("Robot Comments");
        }
        data.add(0, header);
        return data;
    };
    private RxDBManager rxDbManager;

    public RxCompiler(Context context, RxDBManager rxDbManager) {
        this.context = context;
        this.rxDbManager = rxDbManager;
    }

    /**
     * @return the list of metrics in respect to the preferences
     */
    public Observable<List<Metric>> getMetrics() {
        Observable<List<Metric>> matchMetricsObservable = PreferenceHelper.compileMatchMetricsToExport(context) ? rxDbManager.metricsByCategory(MetricHelper.MATCH_PERF_METRICS) : Observable.just(Lists.newArrayListWithCapacity(0));
        Observable<List<Metric>> pitMetricsObservable = PreferenceHelper.compilePitMetricsToExport(context) ? rxDbManager.metricsByCategory(MetricHelper.ROBOT_METRICS) : Observable.just(Lists.newArrayListWithCapacity(0));
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
    public Observable<List<CompiledMetricValue>> getMetricEventSummary(Metric metric) {
        float compileWeight = getCompileWeight();
        return rxDbManager.getTeamsTable().loadAllObservable()
                .concatMap(Observable::from)
                .concatMap(robot -> getRobotMetricSummary(metric, robot, compileWeight).subscribeOn(Schedulers.computation()))
                .toSortedList((cmv, cmv1) -> Double.compare(cmv.getTeam().getNumber(), cmv1.getTeam().getNumber()));
    }

    /**
     * @return Data for a robot at an event
     */
    public Observable<List<MetricValue>> getRobotMetricData(Metric metric, Team robot) {
        return Observable.defer(() -> {
            Observable<List<MetricValue>> metricValueListObservable = Observable.empty();
            if (metric.getCategory() == MetricHelper.MATCH_PERF_METRICS) {
                metricValueListObservable = rxDbManager.getMatchDataTable()
                        .query(robot.getNumber(), metric.getId(), null, MetricHelper.MATCH_GAME_TYPE)
                        .orderAsc(MatchDatumDao.Properties.Match_number)
                        .rx()
                        .list()
                        .concatMap(Observable::from)
                        .map(MetricDataHelper.INSTANCE.getMapMatchDataToMetricValue())
                        .toList();
            } else if (metric.getCategory() == MetricHelper.ROBOT_METRICS) {
                metricValueListObservable = rxDbManager.getPitDataTable().query(robot.getNumber(), metric.getId())
                        .rx()
                        .list()
                        .concatMap(Observable::from)
                        .map(MetricDataHelper.INSTANCE.getMapPitDataToMetricValue())
                        .toList();
            }
            return metricValueListObservable;
        });
    }

    public Observable<CompiledMetricValue> getRobotMetricSummary(Metric metric, Team robot, float compileWeight) {
        return getRobotMetricData(metric, robot).map(CompileUtil.metricValuesToCompiledValue(robot, metric, compileWeight));
    }

    public List<String> getRobotMatchComments(Team robot) {
        List<String> comments = Lists.newArrayList();
        QueryBuilder<MatchComment> matchCommentQueryBuilder = rxDbManager.getMatchCommentsTable().query(null, null, robot.getNumber(), 0);
        for (MatchComment matchComment : matchCommentQueryBuilder.list()) {
            comments.add(matchComment.getMatch_number() + ": " + matchComment.getComment());
        }
        return comments;
    }

    public Observable<List<Long>> getMatchNumbers(Team robot) {
        return rxDbManager.getMatchDataTable().query(robot.getNumber(), null, null, 0)
                .orderAsc(MatchDatumDao.Properties.Match_number)
                .rx()
                .list()
                .map(rxDbManager.getMatchDataTable()::getMatchNumbersFromMatchData);
    }

    public Observable<List<List<String>>> getSummaryExport() {
        final float compileWeight = getCompileWeight();
        final boolean compileTeamNames = PreferenceHelper.compileTeamNamesToExport(context);
        final boolean compileMatchMetric = PreferenceHelper.compileMatchMetricsToExport(context);
        final boolean compilePitMetric = PreferenceHelper.compilePitMetricsToExport(context);

        final Scheduler scheduler = Schedulers.from(Executors.newFixedThreadPool(10));

        //Cache metrics so we can use it over and over
        final Observable<Metric> metricObservable = getMetrics().flatMap(Observable::from).cache();

        return rxDbManager.getTeamsTable().loadAllObservable()
                .flatMap(Observable::from)
                .concatMap(robot ->
                        metricObservable
                                .concatMap(metric -> getRobotMetricSummary(metric, robot, compileWeight).map(CompileUtil.compiledMetricValueToString))
                                .toList()
                                .map(list -> {
                                    if (compileTeamNames) {
                                        list.add(0, robot.getName());
                                    }
                                    list.add(0, String.valueOf(robot.getNumber()));
                                    if (compileMatchMetric) {
                                        list.add(Joiner.on(", ").join(getRobotMatchComments(robot)));
                                    }
                                    if (compilePitMetric) {
                                        list.add(robot.getComments());
                                    }
                                    return list;
                                })
                                .subscribeOn(scheduler)
                )
                .toList()
                .map(lists -> {
                    Collections.sort(lists, (strings1, strings) -> Double.compare(Integer.parseInt(strings1.get(0)), Integer.parseInt(strings.get(0))));
                    return lists;
                })
                .zipWith(getMetrics(), summaryHeaderFunc);
    }

    public Observable<List<CompiledMetricValue>> getCompiledRobotSummary(long robot_id, final Observable<List<Metric>> metricListObservable) {
        return Observable.just(robot_id)
                .map(rxDbManager.getTeamsTable()::load)
                .concatMap(robot -> metricListObservable
                        .concatMap(Observable::from)
                        .concatMap(metric -> getRobotMetricSummary(metric, robot))
                        .toList());
    }

    public Observable<Map<String, String>> getCompiledMetricToHashMap(Observable<List<CompiledMetricValue>> metricValueObservable) {
        return metricValueObservable
                .concatMap(Observable::from)
                .map(CompileUtil.mapCompiledMetricValueToKeyValue)
                .toList()
                .map(CompileUtil.mapEntriesToMap);
    }

    public Observable<CompiledMetricValue> getRobotMetricSummary(Metric metric, Team robot) {
        return getRobotMetricSummary(metric, robot, getCompileWeight());
    }

    public float getCompileWeight() {
        return PreferenceHelper.compileWeight(context);
    }

    public Observable<List<String>> getRobotRawData(Team robot, Observable<List<Metric>> metricsObservable) {
        return getMatchNumbers(robot)
                .flatMap(Observable::from)
                .concatMap(matchNumber ->
                        metricsObservable
                                .flatMap(Observable::from)
                                .map(metric -> rxDbManager.getMatchDataTable().query(robot.getNumber(), metric.getId(), matchNumber, 0).unique())
                                .map(CompileUtil.convertMatchDataToStringFunc)
                                .toList()
                                .map(record -> {
                                    record.add(0, String.valueOf(matchNumber));
                                    record.add(0, String.valueOf(robot.getNumber()));
                                    MatchComment comment = rxDbManager.getMatchCommentsTable().query(matchNumber, null, robot.getNumber(), 0).unique();
                                    if (comment != null) {
                                        record.add(comment.getComment());
                                    }
                                    return record;
                                }));
    }

    public Observable<List<List<String>>> getRawExport() {
        final Scheduler scheduler = Schedulers.from(Executors.newFixedThreadPool(10));
        final Observable<List<Metric>> metricObservable = rxDbManager.getMetricsTable().query(MetricHelper.MATCH_PERF_METRICS, null, null).rx().list().cache();

        return rxDbManager.getTeamsTable().loadAllObservable()
                .flatMap(Observable::from)
                .concatMap(robot -> getRobotRawData(robot, metricObservable).subscribeOn(scheduler))
                .toList()
                .map(lists -> {
                    //Load metrics one more time
                    List<Metric> metrics = rxDbManager.getMetricsTable().query(MetricHelper.MATCH_PERF_METRICS, null, null).list();

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
    }
}
