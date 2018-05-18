package com.team2052.frckrawler.core.data.tables;

import com.team2052.frckrawler.core.data.models.DBManager;
import com.team2052.frckrawler.core.data.models.ServerLogEntry;
import com.team2052.frckrawler.core.data.models.ServerLogEntryDao;

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
