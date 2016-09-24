package com.team2052.frckrawler.binding;

import android.support.v7.widget.RecyclerView;

import dagger.Module;
import dagger.Provides;

@Module
public class BinderModule {
    @Provides
    public ListViewBinder provideListViewConsumer() {
        return new ListViewBinder();
    }

    @Provides
    public SpinnerBinder provideSpinnerConsumer() {
        return new SpinnerBinder();
    }

    @Provides
    public ServerFragmentBinder provideServerFragmentConsumer() {
        return new ServerFragmentBinder();
    }

    @Provides
    public RecyclerViewBinder provideRecyclerViewBinder() {
        return new RecyclerViewBinder();
    }
}
