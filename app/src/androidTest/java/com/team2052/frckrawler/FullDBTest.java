package com.team2052.frckrawler;

import com.team2052.frckrawler.core.database.DBManager;
import com.team2052.frckrawler.db.DaoMaster;
import com.team2052.frckrawler.db.DaoSession;
import com.team2052.frckrawler.db.PitData;
import com.team2052.frckrawler.db.PitDataDao;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.test.AbstractDaoSessionTest;

/**
 * Created by adam on 3/26/15.
 */
public class FullDBTest extends AbstractDaoSessionTest<DaoMaster, DaoSession>{

    public FullDBTest() {
        super(DaoMaster.class);
    }

    public void test(){
        pitDataUpdate();
    }

    /**
     * Check if the pit data can update correctly and insert correctly while still keeping the update function
     */
    public void pitDataUpdate(){
        DBManager manager = DBManager.getInstance(getContext(), daoSession);

        //Insert data
        PitData test = new PitData(0l, 0l, 0l, 0l, 0l, "0");
        boolean inserted = manager.insertPitData(test);
        assertEquals(inserted, true);

        //Insert data over other data
        PitData other = new PitData(0l, 0l, 0l, 0l, 0l, "1");
        inserted = manager.insertPitData(other);
        assertEquals(inserted, false);

        //Check if it was actually updated
        PitData load = daoSession.getPitDataDao().load(0l);
        assertEquals(load.getData(), "1");
    }

}
