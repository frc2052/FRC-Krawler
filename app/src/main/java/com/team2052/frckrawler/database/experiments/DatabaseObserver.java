package com.team2052.frckrawler.database.experiments;

import com.facebook.stetho.inspector.protocol.module.Database;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.db.Game;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Action;

/**
 * Created by adam on 11/16/15.
 */
@Singleton
public class DatabaseObserver {
    private final DBManager mDbManager;

    @Inject
    public DatabaseObserver(DBManager dbManager) {
        mDbManager = dbManager;
    }

    public Observable<List<Game>> fetchGameList() {
        return Observable.create(subscriber -> {
            try {
                List<Game> games = mDbManager.getGamesTable().loadAll();
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(games);
                    subscriber.onCompleted();
                }
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }
}
