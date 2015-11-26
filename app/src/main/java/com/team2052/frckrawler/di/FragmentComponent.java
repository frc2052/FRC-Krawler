package com.team2052.frckrawler.di;

import com.team2052.frckrawler.activities.EventInfoActivity;
import com.team2052.frckrawler.activities.GameInfoActivity;
import com.team2052.frckrawler.activities.HomeActivity;
import com.team2052.frckrawler.activities.RobotActivity;
import com.team2052.frckrawler.activities.TeamInfoActivity;
import com.team2052.frckrawler.bluetooth.client.ScoutSyncHandler;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.consumer.ConsumerModule;
import com.team2052.frckrawler.database.subscribers.SubscriberModule;
import com.team2052.frckrawler.fragments.EventsFragment;
import com.team2052.frckrawler.fragments.GamesFragment;
import com.team2052.frckrawler.fragments.MatchListFragment;
import com.team2052.frckrawler.fragments.MetricsFragment;
import com.team2052.frckrawler.fragments.RobotAttendingEventsFragment;
import com.team2052.frckrawler.fragments.RobotsFragment;
import com.team2052.frckrawler.fragments.ServerFragment;
import com.team2052.frckrawler.fragments.SummaryFragment;
import com.team2052.frckrawler.fragments.TeamsFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        FRCKrawlerModule.class,
        SubscriberModule.class,
        ConsumerModule.class,
}, dependencies = ApplicationComponent.class)
public interface FragmentComponent {
    DBManager dbManager();

    ScoutSyncHandler scoutSyncHander();

    void inject(HomeActivity activity);

    void inject(GamesFragment gamesFragment);

    void inject(GameInfoActivity gameInfoActivity);

    void inject(EventsFragment eventsNewFragment);

    void inject(EventInfoActivity eventInfoActivity);

    void inject(MetricsFragment metricsFragment);

    void inject(TeamsFragment teamsFragment);

    void inject(MatchListFragment matchListFragment);

    void inject(RobotAttendingEventsFragment robotAttendingEventsFragment);

    void inject(RobotsFragment robotsFragment);

    void inject(SummaryFragment summaryFragment);

    void inject(TeamInfoActivity teamInfoActivity);

    void inject(RobotActivity robotActivity);

    void inject(ServerFragment serverFragment);
}
