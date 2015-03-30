package com.team2052.frckrawler.core.database;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class FRCKrawlerDaoGenerator {

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(2, "com.team2052.frckrawler.db");

        Entity matchData = schema.addEntity("MatchData");
        matchData.implementsSerializable();
        matchData.addIdProperty();
        Property match_data_robot_id = matchData.addLongProperty("robotId").notNull().getProperty();
        Property match_data_metric_id = matchData.addLongProperty("metricId").notNull().getProperty();
        Property match_data_match_id = matchData.addLongProperty("matchId").notNull().getProperty();
        Property match_data_event_id = matchData.addLongProperty("eventId").notNull().getProperty();
        Property match_data_user_id = matchData.addLongProperty("userId").notNull().getProperty();
        matchData.addStringProperty("data");

        Entity pitData = schema.addEntity("PitData");
        pitData.implementsSerializable();
        pitData.addIdProperty();
        Property pit_data_robot_id = pitData.addLongProperty("robotId").notNull().getProperty();
        Property pit_data_metric_id = pitData.addLongProperty("metricId").notNull().getProperty();
        Property pit_data_event_id = pitData.addLongProperty("eventId").notNull().getProperty();
        Property pit_data_user_id = pitData.addLongProperty("userId").notNull().getProperty();
        pitData.addStringProperty("data");

        Entity matchComment = schema.addEntity("MatchComment");
        matchComment.implementsSerializable();
        matchComment.addIdProperty().autoincrement().unique();
        Property match_comment_match_id = matchComment.addLongProperty("matchId").getProperty();
        Property match_comment_robot_id = matchComment.addLongProperty("robotId").getProperty();
        Property match_comment_event_id = matchComment.addLongProperty("eventId").getProperty();
        matchComment.addStringProperty("comment");

        Entity robotEvent = schema.addEntity("RobotEvent");
        robotEvent.implementsSerializable();
        robotEvent.addIdProperty().unique().autoincrement();
        Property robot_event_robot_id = robotEvent.addLongProperty("robotId").notNull().getProperty();
        Property robot_event_event_id = robotEvent.addLongProperty("eventId").notNull().getProperty();
        robotEvent.addStringProperty("data");

        //Match
        Entity match = schema.addEntity("Match");
        match.implementsSerializable();
        match.addIdProperty().autoincrement().unique();
        match.addIntProperty("number");
        match.addStringProperty("key").unique();
        Property match_event_id = match.addLongProperty("eventId").notNull().getProperty();
        match.addToMany(matchData, match_data_match_id);
        match.addToMany(matchComment, match_comment_match_id);
        match.addStringProperty("data");
        match.addStringProperty("type");

        Entity picklist = schema.addEntity("PickList");
        picklist.addIdProperty().autoincrement().unique();
        picklist.addStringProperty("name");
        Property pick_list_event_id = picklist.addLongProperty("eventId").getProperty();
        picklist.addStringProperty("data");

        //Events
        Entity event = schema.addEntity("Event");
        event.addIdProperty().autoincrement().unique();
        event.implementsSerializable();
        event.addStringProperty("name");
        event.addStringProperty("fmsid").unique();
        Property event_game_id = event.addLongProperty("gameId").notNull().getProperty();
        event.addToMany(robotEvent, robot_event_event_id);
        event.addToMany(match, match_event_id);
        event.addToMany(matchData, match_data_event_id);
        event.addToMany(pitData, pit_data_event_id);
        event.addToMany(matchComment, match_comment_event_id);
        event.addToMany(picklist, pick_list_event_id);
        event.addStringProperty("data");
        event.addDateProperty("date");

        //Robots
        Entity robot = schema.addEntity("Robot");
        robot.implementsSerializable();
        robot.addIdProperty().unique().autoincrement();
        Property robot_team_id = robot.addLongProperty("teamId").notNull().getProperty();
        Property robot_game_id = robot.addLongProperty("gameId").notNull().getProperty();
        robot.addToMany(robotEvent, robot_event_robot_id);
        robot.addToMany(matchData, match_data_robot_id);
        robot.addToMany(pitData, pit_data_robot_id);
        robot.addToMany(matchComment, match_comment_robot_id);
        robot.addStringProperty("data");
        robot.addStringProperty("comments");

        Entity metric = schema.addEntity("Metric");
        metric.implementsSerializable();
        metric.addIdProperty().unique().autoincrement();
        metric.addStringProperty("name");
        metric.addIntProperty("category");
        metric.addToMany(matchData, match_data_metric_id);
        metric.addToMany(pitData, pit_data_metric_id);
        metric.addIntProperty("type");
        metric.addStringProperty("data");
        Property metric_game_id = metric.addLongProperty("gameId").notNull().getProperty();

        //Games
        Entity game = schema.addEntity("Game");
        game.implementsSerializable();
        game.addIdProperty().autoincrement().unique();
        game.addToMany(event, event_game_id);
        game.addToMany(robot, robot_game_id);
        game.addToMany(metric, metric_game_id);
        game.addStringProperty("name");

        //Contact
        Entity contact = schema.addEntity("Contact");
        contact.implementsSerializable();
        contact.addIdProperty().autoincrement().unique();
        Property contact_team_id = contact.addLongProperty("teamId").notNull().getProperty();
        contact.addStringProperty("data");

        //Team
        Entity team = schema.addEntity("Team");
        team.implementsSerializable();
        team.addLongProperty("number").unique().primaryKey();
        team.addStringProperty("teamkey").unique();
        team.addStringProperty("name");
        team.addToMany(robot, robot_team_id);
        team.addToMany(contact, contact_team_id);
        team.addStringProperty("data");

        //Not used, but still is wanted.
        Entity robotPhoto = schema.addEntity("RobotPhoto");
        robotPhoto.implementsSerializable();
        robotPhoto.addIdProperty().autoincrement().unique();
        robotPhoto.addStringProperty("location");
        robotPhoto.addToOne(robot, robotPhoto.addLongProperty("robotId").getProperty());
        robotPhoto.addStringProperty("title");
        robotPhoto.addDateProperty("date");

        Entity user = schema.addEntity("User");
        user.implementsSerializable();
        user.addIdProperty().autoincrement().unique();
        user.addToMany(matchData, match_data_user_id);
        user.addToMany(pitData, pit_data_user_id);
        user.addStringProperty("name");


        new DaoGenerator().generateAll(schema, args[0]);
    }
}
