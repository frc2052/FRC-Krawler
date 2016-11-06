package com.team2052.frckrawler.database.metric;

import android.os.Environment;
import android.support.annotation.NonNull;

import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.Robot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVWriter;
import rx.Observable;
import rx.functions.Func1;

public class CompileUtil {

    public static Func1<CompiledMetricValue, AbstractMap.SimpleEntry<String, String>> mapCompiledMetricValueToKeyValue = compiledMetricValue -> {
        return new AbstractMap.SimpleEntry<>(compiledMetricValue.getMetric().getName(), compiledMetricValue.getCompiledValue());
    };

    public static Func1<List<AbstractMap.SimpleEntry<String, String>>, Map<String, String>> mapEntriesToMap = simpleEntries -> {
        Map<String, String> map = new LinkedHashMap<>();
        for (AbstractMap.SimpleEntry<String, String> simpleEntry :
                simpleEntries) {
            map.put(simpleEntry.getKey(), simpleEntry.getValue());
        }
        return map;
    };

    public static Func1<CompiledMetricValue, String> compiledMetricValueToString = CompiledMetricValue::getCompiledValue;

    public static Func1<List<MetricValue>, CompiledMetricValue> metricValuesToCompiledValue(Robot robot, Metric metric, float compileWeight) {
        return metricValues -> new CompiledMetricValue(robot, metric, metricValues, compileWeight);
    }

    public static Observable<File> writeToFile(Observable<List<List<String>>> listObservable, Observable<File> fileObservable) {
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

    public static Observable<File> getSummaryFile(@NonNull Event event) {
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
                                    String.format("%s_%s_Summary", event.getGame().getName(), event.getName()),  /* prefix */
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

    public static Observable<File> getRawExportFile(@NonNull Event event) {
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
                                    String.format("%s_%s_RawExport", event.getGame().getName(), event.getName()),  /* prefix */
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
}
