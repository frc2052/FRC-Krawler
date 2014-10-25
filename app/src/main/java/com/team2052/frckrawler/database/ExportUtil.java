package com.team2052.frckrawler.database;

import com.team2052.frckrawler.util.LogHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import au.com.bytecode.opencsv.CSVWriter;
import frckrawler.DaoSession;
import frckrawler.Event;
import frckrawler.Metric;
import frckrawler.MetricDao;
import frckrawler.RobotEvent;
import frckrawler.RobotEventDao;

/**
 * @author Adam
 * @since 10/4/2014
 */
public class ExportUtil
{
    public static void exportEventDataToCSV(Event event, File location, DaoSession daoSession)
    {
        final List<Metric> metrics = daoSession.getMetricDao().queryBuilder().where(MetricDao.Properties.GameId.eq(event.getGameId())).list();
        List<RobotEvent> robotEvents = daoSession.getRobotEventDao().queryBuilder().where(RobotEventDao.Properties.EventId.eq(event.getId())).list();

        Map<Long, List<CompiledMetricValue>> robots = new TreeMap<>();

        for (RobotEvent robotEvent : robotEvents) {
            robots.put(robotEvent.getRobot().getTeam().getNumber(), MetricCompiler.getCompiledRobot(event, robotEvent.getRobot(), daoSession));
        }

        try {
            CSVWriter writer = new CSVWriter(new FileWriter(location), ',');

            List<String> header = new ArrayList<>();

            header.add("Team");

            for (Metric metric : metrics) {
                header.add(metric.getName());
            }

            writer.writeNext(Arrays.copyOf(header.toArray(), header.size(), String[].class));

            for (Map.Entry<Long, List<CompiledMetricValue>> entry : robots.entrySet()) {
                List<String> record = new ArrayList<>();
                record.add(String.valueOf(entry.getKey()));

                for (CompiledMetricValue metricValue : entry.getValue()) {
                    record.add(metricValue.compiledValue);
                }

                writer.writeNext(Arrays.copyOf(record.toArray(), record.size(), String[].class));
            }

            LogHelper.debug("Exported to " + location.getAbsolutePath());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
