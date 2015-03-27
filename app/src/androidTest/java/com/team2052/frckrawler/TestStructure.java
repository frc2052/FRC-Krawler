package com.team2052.frckrawler;

import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.db.GameDao;

import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.test.AbstractDaoTestLongPk;

/**
 * Created by adam on 3/26/15.
 */
public class TestStructure extends AbstractDaoTestLongPk<GameDao, Game>{

    public TestStructure() {
        super(GameDao.class);
    }

    @Override
    protected Game createEntity(Long key) {
        Game game = new Game();
        game.setId(key);
        game.setName("game");
        return game;
    }
}
