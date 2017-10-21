package com.team2052.frckrawler.data;


import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Property;
import org.greenrobot.greendao.generator.Schema;

public class FRCKrawlerDaoGenerator {
    public static final String jsonPropertyConverter = "com.team2052.frckrawler.database.converters.JsonPropertyConverter";
    public static final String jsonElementType = "com.google.gson.JsonElement";

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(7, "com.team2052.frckrawler.models");

        Entity game = schema.addEntity("Season");
        Entity event = schema.addEntity("Event");
        Entity team = schema.addEntity("Team");
        Entity metric = schema.addEntity("Metric");
        Entity matchDatum = schema.addEntity("MatchDatum");
        Entity matchComment = schema.addEntity("MatchComment");
        Entity robot = schema.addEntity("Robot");
        Entity robotEvent = schema.addEntity("RobotEvent");
        Entity pitDatum = schema.addEntity("PitDatum");
        Entity serverLogEntry = schema.addEntity("ServerLogEntry");

        //Match data
        matchDatum.implementsSerializable();
        matchDatum.addIdProperty();

        Property match_data_event_id = matchDatum.addLongProperty("event_id").notNull().getProperty();
        matchDatum.addToOne(event, match_data_event_id);

        Property match_data_robot_id = matchDatum.addLongProperty("robot_id").notNull().getProperty();
        matchDatum.addToOne(robot, match_data_robot_id);

        Property match_data_metric_id = matchDatum.addLongProperty("metric_id").notNull().getProperty();
        matchDatum.addToOne(metric, match_data_metric_id);

        matchDatum.addIntProperty("match_type").notNull().getProperty();
        matchDatum.addLongProperty("match_number").notNull();
        matchDatum.addDateProperty("last_updated");
        matchDatum.addStringProperty("data");

        //Pit data
        pitDatum.implementsSerializable();
        pitDatum.addIdProperty();

        Property pit_data_robot_id = pitDatum.addLongProperty("robot_id").notNull().getProperty();
        pitDatum.addToOne(robot, pit_data_robot_id);

        Property pit_data_metric_id = pitDatum.addLongProperty("metric_id").notNull().getProperty();
        pitDatum.addToOne(metric, pit_data_metric_id);

        Property pit_data_event_id = pitDatum.addLongProperty("event_id").notNull().getProperty();
        pitDatum.addToOne(event, pit_data_event_id);

        pitDatum.addStringProperty("data");
        pitDatum.addDateProperty("last_updated");

        //Match Comment
        matchComment.implementsSerializable();
        matchComment.addIdProperty();

        matchComment.addLongProperty("match_number");
        matchComment.addIntProperty("match_type");

        Property match_comment_robot_id = matchComment.addLongProperty("robot_id").getProperty();
        matchComment.addToOne(robot, match_comment_robot_id);

        Property match_comment_event_id = matchComment.addLongProperty("event_id").getProperty();
        matchComment.addToOne(event, match_comment_event_id);

        matchComment.addStringProperty("comment");
        matchComment.addDateProperty("last_updated");

        //Robot Event
        robotEvent.implementsSerializable();
        robotEvent.addIdProperty().unique().autoincrement();

        Property robot_event_robot_id = robotEvent.addLongProperty("robot_id").notNull().getProperty();
        robotEvent.addToOne(robot, robot_event_robot_id);

        Property robot_event_event_id = robotEvent.addLongProperty("event_id").notNull().getProperty();
        robotEvent.addToOne(event, robot_event_event_id);

        robotEvent.addStringProperty("data");

        //Events
        event.implementsSerializable();
        event.addIdProperty();
        event.addStringProperty("fmsid").unique();
        event.addStringProperty("name");
        Property event_game_id = event.addLongProperty("season_id").notNull().getProperty();
        event.addToOne(game, event_game_id);
        event.addToMany(matchComment, match_comment_event_id);
        event.addStringProperty("data");
        event.addDateProperty("date");
        event.addStringProperty("unique_hash");

        event.addToMany(robotEvent, robot_event_event_id);
        event.addToMany(matchDatum, match_data_event_id);
        event.addToMany(pitDatum, pit_data_event_id);

        //Robots
        robot.implementsSerializable();
        robot.addIdProperty().unique().autoincrement();
        Property robot_team_id = robot.addLongProperty("team_id").notNull().getProperty();
        Property robot_game_id = robot.addLongProperty("season_id").notNull().getProperty();

        robot.addStringProperty("data");
        robot.addStringProperty("comments");
        robot.addDateProperty("last_updated");

        robot.addToOne(game, robot_game_id);
        robot.addToOne(team, robot_team_id);
        robot.addToMany(robotEvent, robot_event_robot_id);
        robot.addToMany(matchDatum, match_data_robot_id);
        robot.addToMany(pitDatum, pit_data_robot_id);
        robot.addToMany(matchComment, match_comment_robot_id);

        //Metric
        metric.implementsSerializable();
        metric.addIdProperty().unique().autoincrement();
        metric.addStringProperty("name");
        metric.addIntProperty("category");
        metric.addToMany(matchDatum, match_data_metric_id);
        metric.addToMany(pitDatum, pit_data_metric_id);
        metric.addIntProperty("type");
        metric.addStringProperty("data");
        Property metric_game_id = metric.addLongProperty("season_id").notNull().getProperty();
        metric.addToOne(game, metric_game_id);
        metric.addBooleanProperty("enabled").notNull();
        metric.addIntProperty("priority").notNull();

        //Games
        game.implementsSerializable();
        game.addIdProperty().autoincrement().unique();
        game.addToMany(event, event_game_id);
        game.addToMany(robot, robot_game_id);
        game.addToMany(metric, metric_game_id);
        game.addStringProperty("name");

        //Team

        team.implementsSerializable();
        team.addLongProperty("number").unique().primaryKey();
        team.addStringProperty("teamkey").unique();
        team.addStringProperty("name");
        team.addToMany(robot, robot_team_id);
        team.addStringProperty("data");

        //Server Log
        serverLogEntry.addDateProperty("time");
        serverLogEntry.addStringProperty("message");

        new DaoGenerator().generateAll(schema, args[0]);
    }
}
