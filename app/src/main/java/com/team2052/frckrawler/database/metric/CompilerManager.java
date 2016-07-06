package com.team2052.frckrawler.database.metric;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.MatchComment;
import com.team2052.frckrawler.db.MatchData;
import com.team2052.frckrawler.db.MatchDataDao;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.PitData;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.fragments.dialog.events.ProgressDialogUpdateEvent;
import com.team2052.frckrawler.tba.JSON;
import com.team2052.frckrawler.util.PreferenceUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

import au.com.bytecode.opencsv.CSVWriter;
import de.greenrobot.dao.query.QueryBuilder;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

public class CompilerManager {
    private static final String TAG = "CompilerManager";

    private Context context;
    private DBManager dbManager;

    public CompilerManager(Context context, DBManager dbManager) {
        this.context = context;
        this.dbManager = dbManager;
    }

    public Observable<List<Metric>> metricListObservable(Event event) {
        Observable<List<Metric>> matchMetricsObservable = PreferenceUtil.compileMatchMetricsToExport(context) ? dbManager.metricsInGame(event.getGame_id(), MetricHelper.MATCH_PERF_METRICS) : Observable.just(Lists.newArrayListWithCapacity(0));
        Observable<List<Metric>> pitMetricsObservable = PreferenceUtil.compilePitMetricsToExport(context) ? dbManager.metricsInGame(event.getGame_id(), MetricHelper.ROBOT_METRICS) : Observable.just(Lists.newArrayListWithCapacity(0));
        return Observable.combineLatest(matchMetricsObservable, pitMetricsObservable, (matchMetrics, pitMetrics) -> {
            List<Metric> metrics = Lists.newArrayListWithCapacity(matchMetrics.size() + pitMetrics.size());
            metrics.addAll(matchMetrics);
            metrics.addAll(pitMetrics);
            return metrics;
        });
    }

    public Observable<List<CompiledMetricValue>> compiledMetricObservable(Event event, Metric metric) {
        float compileWeight = getCompileWeight();
        return Observable.just(event).flatMap(event1 -> Observable.from(dbManager.getEventsTable().getRobots(event1)))
                .concatMap(robot -> compiledRobotMetricObservable(event, metric, robot, compileWeight).subscribeOn(Schedulers.computation()))
                .toList();
    }


    public Observable<CompiledMetricValue> compiledRobotMetricObservable(Event event, Metric metric, Robot robot, float compileWeight) {
        return Observable.defer(() -> {
            Observable<List<MetricValue>> metricValueListObservable = Observable.empty();
            if (metric.getCategory() == MetricHelper.MATCH_PERF_METRICS) {
                QueryBuilder<MatchData> queryBuilder = dbManager.getMatchDataTable().query(robot.getId(), metric.getId(), null, 0, event.getId(), null).orderAsc(MatchDataDao.Properties.Match_number);

                metricValueListObservable = Observable.from(queryBuilder.list())
                        .map(matchData -> new MetricValue(metric, JSON.getAsJsonObject(matchData.getData())))
                        .toList();
            } else if (metric.getCategory() == MetricHelper.ROBOT_METRICS) {
                QueryBuilder<PitData> queryBuilder = dbManager.getPitDataTable().query(robot.getId(), metric.getId(), event.getId(), null);

                metricValueListObservable = Observable.from(queryBuilder.list())
                        .map(pitData -> new MetricValue(metric, JSON.getAsJsonObject(pitData.getData())))
                        .toList();
            }
            return metricValueListObservable;
        }).map(metricValues -> new CompiledMetricValue(robot, metric, metricValues, compileWeight));
    }

    public List<String> getRobotMatchComments(Event event, Robot robot) {
        List<String> comments = Lists.newArrayList();
        QueryBuilder<MatchComment> matchCommentQueryBuilder = dbManager.getMatchComments().query(null, null, robot.getId(), event.getId());
        for (MatchComment matchComment : matchCommentQueryBuilder.list()) {
            comments.add(matchComment.getMatch_number() + ": " + matchComment.getComment());
        }
        return comments;
    }

    public Observable<List<List<String>>> fullExportObservable(Event event) {
        final float compileWeight = getCompileWeight();
        final boolean compileTeamNames = PreferenceUtil.compileTeamNamesToExport(context);
        final boolean compileMatchMetric = PreferenceUtil.compileMatchMetricsToExport(context);
        final boolean compilePitMetric = PreferenceUtil.compilePitMetricsToExport(context);

        EventBus eventBus = EventBus.getDefault();

        final Scheduler scheduler = Schedulers.from(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));

        return Observable.combineLatest(metricListObservable(event), dbManager.robotsAtEvent(event.getId()), FullExportParams::new)
                .flatMap(fullExportParams -> {
                    Observable<List<List<String>>> listObservable = Observable.from(fullExportParams.robots)
                            .concatMap(robot -> {
                                eventBus.post(new ProgressDialogUpdateEvent("Compiling Team " + robot.getTeam_id()));
                                return Observable.from(fullExportParams.metrics)
                                        .concatMap(metric -> compiledRobotMetricStringObservable(compiledRobotMetricObservable(event, metric, robot, compileWeight)))
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
                                        .subscribeOn(scheduler);
                            })
                            .toList()
                            .map(list -> {
                                List<String> header = Lists.newArrayList();

                                header.add("Team Number");

                                if (PreferenceUtil.compileTeamNamesToExport(context)) {
                                    header.add("Team Names");
                                }

                                for (int i = 0; i < fullExportParams.metrics.size(); i++) {
                                    Metric metric = fullExportParams.metrics.get(i);
                                    header.add(metric.getName());
                                }

                                if (PreferenceUtil.compileMatchMetricsToExport(context)) {
                                    header.add("Match Comments");
                                }

                                if (PreferenceUtil.compilePitMetricsToExport(context)) {
                                    header.add("Robot Comments");
                                }
                                list.add(0, header);
                                return list;
                            });
                    return listObservable;
                });
    }

    public Observable<File> writeToFile(Observable<List<List<String>>> listObservable, Observable<File> fileObservable) {
        return listObservable.flatMap(lists -> fileObservable.map(file -> {
            CSVWriter writer;
            try {
                writer = new CSVWriter(new FileWriter(file), ',');

                for (int i = 0; i < lists.size(); i++) {
                    List<String> record = lists.get(i);
                    writer.writeNext(Arrays.copyOf(record.toArray(), record.size(), String[].class));
                }

                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return file;
        }));
    }

    private Observable<List<String>> getHeader(List<Metric> metrics) {
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

        return Observable.just(header);
    }

    public Observable<String> compiledRobotMetricStringObservable(Observable<CompiledMetricValue> compiledMetricValueObservable) {
        return compiledMetricValueObservable.map(CompiledMetricValue::getCompiledValue);
    }

    public Observable<CompiledMetricValue> compiledRobotMetricObservable(Event event, Metric metric, Robot robot) {
        return compiledRobotMetricObservable(event, metric, robot, getCompileWeight());
    }

    public float getCompileWeight() {
        return PreferenceUtil.compileWeight(context);
    }

    public Observable<List<List<String>>> getRawExport(Event event) {
        final Scheduler scheduler = Schedulers.from(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
        return Observable.just(event.getId()).flatMap(eventId -> {
            final List<Metric> metrics = dbManager.getMetricsTable().query(MetricHelper.MATCH_PERF_METRICS, null, event.getGame_id(), null).list();
            return dbManager.robotsAtEvent(eventId)
                    .flatMap(Observable::from)
                    .concatMap(robot -> Observable.defer(() -> Observable.just(dbManager.getMatchDataTable().query(robot.getId(), null, null, null, eventId, null).list()))
                            .map(matchDatas -> {
                                List<Long> matchNumbers = Lists.newArrayListWithExpectedSize(10);
                                for (int i = 0; i < matchDatas.size(); i++) {
                                    MatchData matchData = matchDatas.get(i);
                                    if (!matchNumbers.contains(matchData.getMatch_number())) {
                                        matchNumbers.add(matchData.getMatch_number());
                                    }
                                }
                                Collections.sort(matchNumbers);
                                return matchNumbers;
                            })
                            .concatMap(Observable::from)
                            .concatMap(matchNumber ->
                                    Observable.from(metrics)
                                            .map(metric -> {
                                                MatchData matchData = dbManager.getMatchDataTable().query(robot.getId(), metric.getId(), matchNumber, null, eventId, null).unique();
                                                if (matchData == null) {
                                                    return "";
                                                }

                                                JsonObject data = JSON.getAsJsonObject(matchData.getData());

                                                if (metric.getType() < 3) {
                                                    return data.get("value").toString();
                                                } else {
                                                    JsonObject metricData = JSON.getAsJsonObject(metric.getData());
                                                    JsonArray dataIndexes = data.getAsJsonArray("values");
                                                    JsonArray valueArray = metricData.getAsJsonArray("values");
                                                    List<String> selected = Lists.newArrayListWithExpectedSize(dataIndexes.size());
                                                    for (int i = 0; i < dataIndexes.size(); i++) {
                                                        int dataIndex = dataIndexes.get(i).getAsInt();
                                                        selected.add(valueArray.get(dataIndex).toString());
                                                    }
                                                    return Joiner.on(", ").join(selected);
                                                }
                                            })
                                            .toList()
                                            .map(record -> {
                                                record.add(0, String.valueOf(matchNumber));
                                                record.add(0, String.valueOf(robot.getTeam_id()));
                                                MatchComment comment = dbManager.getMatchComments().query(matchNumber, null, robot.getId(), eventId).unique();
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

    public Observable<File> getSummaryFile(@NonNull Event event) {
        return Observable.just(event)
                .map(event1 -> {
                    File fileSystem = Environment.getExternalStorageDirectory();
                    File file = null;
                    if (fileSystem.canWrite()) {
                        File directory = new File(fileSystem, "/FRCKrawler/Summaries/" + event.getGame().getName() + "/");
                        if (!directory.exists()) {
                            directory.mkdirs();
                        }
                        try {
                            file = File.createTempFile(
                                    dbManager.getGamesTable().load(event.getGame_id()).getName() + "_" + event.getName() + "_" + "Summary",  /* prefix */
                                    ".csv",         /* suffix */
                                    directory      /* directory */
                            );
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return file;
                });
    }

    public Observable<File> getRawExportFile(@NonNull Event event) {
        return Observable.just(event)
                .map(event1 -> {
                    File fileSystem = Environment.getExternalStorageDirectory();
                    File file = null;
                    if (fileSystem.canWrite()) {
                        File directory = new File(fileSystem, "/FRCKrawler/RawExport/" + event.getGame().getName() + "/");
                        if (!directory.exists()) {
                            directory.mkdirs();
                        }
                        try {
                            file = File.createTempFile(
                                    dbManager.getGamesTable().load(event.getGame_id()).getName() + "_" + event.getName() + "_" + "RawExport",  /* prefix */
                                    ".csv",         /* suffix */
                                    directory      /* directory */
                            );
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return file;
                });
    }

    private static class FullExportParams {
        List<Metric> metrics;
        List<Robot> robots;

        public FullExportParams(List<Metric> metrics, List<Robot> robots) {
            this.metrics = metrics;
            this.robots = robots;
        }

    }
}
