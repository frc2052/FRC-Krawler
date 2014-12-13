package com.team2052.frckrawler.db;

import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

/**
 * Entity mapped to table MATCH_COMMENT.
 */
public class MatchComment implements java.io.Serializable
{

    private Long matchId;
    private String comment;
    private Long robotId;

    /**
     * Used to resolve relations
     */
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    private transient MatchCommentDao myDao;

    private Match match;
    private Long match__resolvedKey;

    private Robot robot;
    private Long robot__resolvedKey;


    public MatchComment()
    {
    }

    public MatchComment(Long matchId, String comment, Long robotId)
    {
        this.matchId = matchId;
        this.comment = comment;
        this.robotId = robotId;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    public void __setDaoSession(DaoSession daoSession)
    {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMatchCommentDao() : null;
    }

    public Long getMatchId()
    {
        return matchId;
    }

    public void setMatchId(Long matchId)
    {
        this.matchId = matchId;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public Long getRobotId()
    {
        return robotId;
    }

    public void setRobotId(Long robotId)
    {
        this.robotId = robotId;
    }

    /**
     * To-one relationship, resolved on first access.
     */
    public Match getMatch()
    {
        Long __key = this.matchId;
        if (match__resolvedKey == null || !match__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MatchDao targetDao = daoSession.getMatchDao();
            Match matchNew = targetDao.load(__key);
            synchronized (this) {
                match = matchNew;
                match__resolvedKey = __key;
            }
        }
        return match;
    }

    public void setMatch(Match match)
    {
        synchronized (this) {
            this.match = match;
            matchId = match == null ? null : match.getId();
            match__resolvedKey = matchId;
        }
    }

    /**
     * To-one relationship, resolved on first access.
     */
    public Robot getRobot()
    {
        Long __key = this.robotId;
        if (robot__resolvedKey == null || !robot__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            RobotDao targetDao = daoSession.getRobotDao();
            Robot robotNew = targetDao.load(__key);
            synchronized (this) {
                robot = robotNew;
                robot__resolvedKey = __key;
            }
        }
        return robot;
    }

    public void setRobot(Robot robot)
    {
        synchronized (this) {
            this.robot = robot;
            robotId = robot == null ? null : robot.getId();
            robot__resolvedKey = robotId;
        }
    }

    /**
     * Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context.
     */
    public void delete()
    {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context.
     */
    public void update()
    {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /**
     * Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context.
     */
    public void refresh()
    {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

}