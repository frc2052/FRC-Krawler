package com.team2052.frckrawler.database.tables;

import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.db.ServerLogEntry;
import com.team2052.frckrawler.db.ServerLogEntryDao;

public class ServerLogEntries extends KeylessAbstractTable<ServerLogEntry, ServerLogEntryDao> {
    public ServerLogEntries(ServerLogEntryDao dao, DBManager dbManager) {
        super(dao, dbManager);
    }

    @Override
    public void delete(ServerLogEntry model) {
        dao.delete(model);
    }

    @Override
    public void insert(ServerLogEntry model) {
        dao.insert(model);
    }
}
