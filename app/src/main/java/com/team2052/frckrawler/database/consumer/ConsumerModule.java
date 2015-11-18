package com.team2052.frckrawler.database.consumer;

import dagger.Module;
import dagger.Provides;

@Module
public class ConsumerModule {
    @Provides
    public ListViewConsumer provideListViewConsumer() {
        return new ListViewConsumer();
    }
}
