package com.team2052.frckrawler.database.tables;

import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.db.GameDao;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.Robot;

import java.util.List;

public class Games extends Table<Game, GameDao> {
    public Games(GameDao dao, DBManager dbManager) {
        super(dao, dbManager);
    }

    @Override
    public Game load(long id) {
        return dao.load(id);
    }

    @Override
    public void delete(Game model) {
        model.resetEventList();
        model.resetRobotList();
        model.resetMetricList();
        dbManager.getEventsTable().delete(model.getEventList());
        dbManager.getRobotsTable().delete(model.getRobotList());
        dbManager.getMetricsTable().delete(model.getMetricList());
        dao.delete(model);
    }

    @Override
    public void insert(Game model) {
        dao.insertOrReplace(model);
    }

    public List<Robot> getRobots(Game game) {
        game.resetRobotList();
        return game.getRobotList();
    }

    public List<Event> getEvents(Game game) {
        game.resetEventList();
        return game.getEventList();
    }

    public List<Metric> getMetrics(Game game) {
        game.resetMetricList();
        return game.getMetricList();
    }
}
