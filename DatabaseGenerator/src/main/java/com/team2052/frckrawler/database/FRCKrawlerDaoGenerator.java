package com.team2052.frckrawler.database;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class FRCKrawlerDaoGenerator {
    public static final String jsonPropertyConverter = "com.team2052.frckrawler.database.converters.JsonPropertyConverter";
    public static final String jsonElementType = "com.google.gson.JsonElement";

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(2, "com.team2052.frckrawler.db");

        Entity game = schema.addEntity("Game");
        Entity event = schema.addEntity("Event");
        Entity team = schema.addEntity("Team");
        Entity user = schema.addEntity("User");
        Entity metric = schema.addEntity("Metric");
        Entity match = schema.addEntity("Match");
        Entity matchData = schema.addEntity("MatchData");
        Entity matchComment = schema.addEntity("MatchComment");
        Entity robot = schema.addEntity("Robot");
        Entity robotEvent = schema.addEntity("RobotEvent");
        Entity pitData = schema.addEntity("PitData");

        //Match data
        matchData.implementsSerializable();
        matchData.addIdProperty();

        Property match_data_event_id = matchData.addLongProperty("event_id").notNull().getProperty();
        matchData.addToOne(event, match_data_event_id);

        Property match_data_robot_id = matchData.addLongProperty("robot_id").notNull().getProperty();
        matchData.addToOne(robot, match_data_robot_id);

        Property match_data_user_id = matchData.addLongProperty("user_id").getProperty();
        matchData.addToOne(user, match_data_user_id);

        Property match_data_metric_id = matchData.addLongProperty("metric_id").notNull().getProperty();
        matchData.addToOne(metric, match_data_metric_id);

        matchData.addIntProperty("match_type").notNull().getProperty();
        matchData.addLongProperty("match_number").notNull();
        matchData.addDateProperty("last_updated");
        matchData.addStringProperty("data");

        //Pit data
        pitData.implementsSerializable();
        pitData.addIdProperty();

        Property pit_data_robot_id = pitData.addLongProperty("robot_id").notNull().getProperty();
        pitData.addToOne(robot, pit_data_robot_id);

        Property pit_data_metric_id = pitData.addLongProperty("metric_id").notNull().getProperty();
        pitData.addToOne(metric, pit_data_metric_id);

        Property pit_data_event_id = pitData.addLongProperty("event_id").notNull().getProperty();
        pitData.addToOne(event, pit_data_event_id);

        Property pit_data_user_id = pitData.addLongProperty("user_id").getProperty();
        pitData.addToOne(user, pit_data_user_id);

        pitData.addStringProperty("data");
        pitData.addDateProperty("last_updated");


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

        //Match
        match.implementsSerializable();
        match.addIdProperty();
        match.addStringProperty("match_key").unique();
        match.addStringProperty("match_type");
        match.addIntProperty("match_number");

        Property match_event_id = match.addLongProperty("event_id").notNull().getProperty();
        match.addToOne(event, match_event_id);

        match.addStringProperty("data");

        //Events
        event.implementsSerializable();
        event.addIdProperty();
        event.addStringProperty("fmsid").unique();
        event.addStringProperty("name");
        Property event_game_id = event.addLongProperty("game_id").notNull().getProperty();
        event.addToOne(game, event_game_id);
        event.addToMany(matchComment, match_comment_event_id);
        event.addStringProperty("data");
        event.addDateProperty("date");

        event.addToMany(robotEvent, robot_event_event_id);
        event.addToMany(match, match_event_id);
        event.addToMany(matchData, match_data_event_id);
        event.addToMany(pitData, pit_data_event_id);

        //Robots
        robot.implementsSerializable();
        robot.addIdProperty().unique().autoincrement();
        Property robot_team_id = robot.addLongProperty("team_id").notNull().getProperty();
        Property robot_game_id = robot.addLongProperty("game_id").notNull().getProperty();

        robot.addStringProperty("data");
        robot.addStringProperty("comments");
        robot.addDateProperty("last_updated");

        robot.addToOne(game, robot_game_id);
        robot.addToOne(team, robot_team_id);
        robot.addToMany(robotEvent, robot_event_robot_id);
        robot.addToMany(matchData, match_data_robot_id);
        robot.addToMany(pitData, pit_data_robot_id);
        robot.addToMany(matchComment, match_comment_robot_id);

        //Metric
        metric.implementsSerializable();
        metric.addIdProperty().unique().autoincrement();
        metric.addStringProperty("name");
        metric.addIntProperty("category");
        metric.addToMany(matchData, match_data_metric_id);
        metric.addToMany(pitData, pit_data_metric_id);
        metric.addIntProperty("type");
        metric.addStringProperty("data");
        Property metric_game_id = metric.addLongProperty("game_id").notNull().getProperty();
        metric.addToOne(game, metric_game_id);
        metric.addBooleanProperty("enabled").notNull();

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

        //User
        user.implementsSerializable();
        user.addIdProperty().autoincrement().unique();
        user.addToMany(matchData, match_data_user_id);
        user.addToMany(pitData, pit_data_user_id);
        user.addStringProperty("name");

        new DaoGenerator().generateAll(schema, args[0]);
    }
}
