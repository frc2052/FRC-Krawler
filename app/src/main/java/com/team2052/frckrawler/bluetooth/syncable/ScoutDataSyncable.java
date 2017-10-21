package com.team2052.frckrawler.bluetooth.syncable;

import android.content.Context;

import com.google.common.collect.Lists;
import com.team2052.frckrawler.bluetooth.BluetoothConstants;
import com.team2052.frckrawler.bluetooth.model.Schedule;
import com.team2052.frckrawler.data.RxDBManager;
import com.team2052.frckrawler.helpers.ScoutHelper;
import com.team2052.frckrawler.models.Event;
import com.team2052.frckrawler.models.Metric;
import com.team2052.frckrawler.models.Robot;
import com.team2052.frckrawler.models.RobotEvent;
import com.team2052.frckrawler.models.Season;
import com.team2052.frckrawler.models.Team;

import java.util.List;


public class ScoutDataSyncable extends ScoutSyncable {
    private final Season game;
    private final Event event;
    private final List<Metric> metrics;
    private final List<RobotEvent> robot_events;
    private final List<Robot> robots;
    private final Schedule schedule;
    private final List<Team> teams;

    public ScoutDataSyncable(Context context, Event currentEvent) {
        super(BluetoothConstants.ReturnCodes.OK);
        RxDBManager dbManager = RxDBManager.Companion.getInstance(context);

        this.game = dbManager.getSeasonsTable().load(currentEvent.getSeason_id());
        this.event = currentEvent;
        schedule = new Schedule(event);

        game.resetMetricList();
        metrics = game.getMetricList();

        robot_events = event.getRobotEventList();
        robots = Lists.newArrayList();
        for (RobotEvent robotEvent : robot_events) {
            robots.add(dbManager.getRobotEventsTable().getRobot(robotEvent));
        }
        teams = Lists.newArrayList();
        for (RobotEvent robotEvent : robot_events) {
            teams.add(dbManager.getRobotEventsTable().getTeam(robotEvent));
        }
    }

    @Override
    public void saveToScout(Context context) {
        RxDBManager dbManager = RxDBManager.Companion.getInstance(context);

        dbManager.runInTx(() -> {
            for (int i = 0; i < metrics.size(); i++) {
                dbManager.getMetricsTable().insert(metrics.get(i));
                    }

            for (int i = 0; i < robot_events.size(); i++) {
                dbManager.getRobotEventsTable().insert(robot_events.get(i));
                    }

            for (int i = 0; i < robots.size(); i++) {
                dbManager.getRobotsTable().insert(robots.get(i));
                    }

            for (int i = 0; i < teams.size(); i++) {
                dbManager.getTeamsTable().insert(teams.get(i));
                    }

                    dbManager.getEventsTable().insert(event);
                    dbManager.getSeasonsTable().insert(game);
                }
        );

        ScoutHelper.setEvent(context, event);
    }
}
