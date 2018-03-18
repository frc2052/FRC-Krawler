package com.team2052.frckrawler.data;


import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Property;
import org.greenrobot.greendao.generator.Schema;

public class FRCKrawlerDaoGenerator {
    public static final String jsonPropertyConverter = "com.team2052.frckrawler.database.converters.JsonPropertyConverter";
    public static final String jsonElementType = "com.google.gson.JsonElement";

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(1, "com.team2052.frckrawler.models");

        Entity team = schema.addEntity("Team");
        Entity metric = schema.addEntity("Metric");
        Entity matchDatum = schema.addEntity("MatchDatum");
        Entity matchComment = schema.addEntity("MatchComment");
        Entity pitDatum = schema.addEntity("PitDatum");
        Entity serverLogEntry = schema.addEntity("ServerLogEntry");

        //Match data
        matchDatum.implementsSerializable();
        matchDatum.addIdProperty();
        Property match_data_team_id = matchDatum.addLongProperty("team_id").notNull().getProperty();
        matchDatum.addToOne(team, match_data_team_id);
        Property match_data_metric_id = matchDatum.addLongProperty("metric_id").notNull().getProperty();
        matchDatum.addToOne(metric, match_data_metric_id);

        matchDatum.addIntProperty("match_type").notNull().getProperty();
        matchDatum.addLongProperty("match_number").notNull();
        matchDatum.addDateProperty("last_updated");
        matchDatum.addStringProperty("data");

        //Pit data
        pitDatum.implementsSerializable();
        pitDatum.addIdProperty();
        Property pit_data_team_id = pitDatum.addLongProperty("team_id").notNull().getProperty();
        pitDatum.addToOne(team, pit_data_team_id);
        Property pit_data_metric_id = pitDatum.addLongProperty("metric_id").notNull().getProperty();
        pitDatum.addToOne(metric, pit_data_metric_id);
        pitDatum.addStringProperty("data");
        pitDatum.addDateProperty("last_updated");

        //Match Comment
        matchComment.implementsSerializable();
        matchComment.addIdProperty();

        matchComment.addLongProperty("match_number");
        matchComment.addIntProperty("match_type");

        Property match_comment_team_id = matchComment.addLongProperty("team_id").getProperty();
        matchComment.addToOne(team, match_comment_team_id);

        matchComment.addStringProperty("comment");
        matchComment.addDateProperty("last_updated");

        //Metric
        metric.implementsSerializable();
        metric.addIdProperty().unique().autoincrement();
        metric.addStringProperty("name");
        metric.addIntProperty("category");
        metric.addToMany(matchDatum, match_data_metric_id);
        metric.addToMany(pitDatum, pit_data_metric_id);
        metric.addIntProperty("type");
        metric.addStringProperty("data");
        metric.addBooleanProperty("enabled").notNull();
        metric.addIntProperty("priority").notNull();

        //Team
        team.implementsSerializable();
        team.addLongProperty("number").unique().primaryKey();
        team.addStringProperty("teamkey").unique();
        team.addStringProperty("name");
        team.addStringProperty("data");
        team.addDateProperty("last_updated");
        team.addStringProperty("comments");

        //Server Log
        serverLogEntry.addDateProperty("time");
        serverLogEntry.addStringProperty("message");

        new DaoGenerator().generateAll(schema, args[0]);
    }
}
