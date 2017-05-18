package com.team2052.frckrawler.data.tables;

import android.support.annotation.Nullable;

import com.team2052.frckrawler.bluetooth.model.RobotComment;
import com.team2052.frckrawler.data.DBManager;
import com.team2052.frckrawler.models.Game;
import com.team2052.frckrawler.models.Robot;
import com.team2052.frckrawler.models.RobotDao;
import com.team2052.frckrawler.models.RobotEvent;
import com.team2052.frckrawler.models.Team;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Func1;

public class Robots extends AbstractTable<Robot, RobotDao> {
    public Func1<Robot, Team> mapRobotToTeam = Robot::getTeam;
    public Func1<Robot, Game> mapRobotToGame = Robot::getGame;
    public Func1<Robot, List<RobotEvent>> mapRobotToRobotEvents = Robot::getRobotEventList;

    public Robots(RobotDao dao, DBManager dbManager) {
        super(dao, dbManager);
    }

    public Game getGame(Robot mRobot) {
        return dbManager.getGamesTable().load(mRobot.getGame_id());
    }

    public Team getTeam(Robot robot) {
        return dbManager.getTeamsTable().load(robot.getTeam_id());
    }

    public List<RobotComment> getRobotComments() {
        List<Robot> robots = loadAll();
        List<RobotComment> robotComments = new ArrayList<>();

        for (Robot robot : robots) {
            robotComments.add(getRobotComment(robot));
        }

        return robotComments;
    }

    public RobotComment getRobotComment(Robot robot) {
        return new RobotComment(robot.getId(), robot.getComments());
    }

    public QueryBuilder<Robot> query(@Nullable Long id, @Nullable Long team_number, @Nullable Long game_id) {
        QueryBuilder<Robot> robotQueryBuilder = getQueryBuilder();
        if (team_number != null)
            robotQueryBuilder.where(RobotDao.Properties.Team_id.eq(team_number));
        if (game_id != null)
            robotQueryBuilder.where(RobotDao.Properties.Game_id.eq(game_id));
        if (id != null)
            robotQueryBuilder.where(RobotDao.Properties.Id.eq(id));
        return robotQueryBuilder;
    }

    @Override
    public void delete(List<Robot> robots) {
        for (Robot robot : robots) {
            delete(robot);
        }
    }

    public void insert(Robot robot) {
        dao.insertOrReplace(robot);
    }

    public void update(Robot robot) {
        dao.update(robot);
    }

    @Override
    public Robot load(long id) {
        return dao.load(id);
    }

    @Override
    public void delete(Robot robot) {
        dao.delete(robot);
    }
}
