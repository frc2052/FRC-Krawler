package com.team2052.frckrawler.di;

import com.team2052.frckrawler.activities.AddMetricActivity;
import com.team2052.frckrawler.activities.EventInfoActivity;
import com.team2052.frckrawler.activities.GameInfoActivity;
import com.team2052.frckrawler.activities.HomeActivity;
import com.team2052.frckrawler.activities.MatchListActivity;
import com.team2052.frckrawler.activities.MetricInfoActivity;
import com.team2052.frckrawler.activities.RobotActivity;
import com.team2052.frckrawler.activities.ScoutActivity;
import com.team2052.frckrawler.activities.SummaryDataActivity;
import com.team2052.frckrawler.activities.TeamInfoActivity;
import com.team2052.frckrawler.binding.BinderModule;
import com.team2052.frckrawler.bluetooth.client.ScoutSyncHandler;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.metric.CompilerManager;
import com.team2052.frckrawler.fragments.EventInfoFragment;
import com.team2052.frckrawler.fragments.EventsFragment;
import com.team2052.frckrawler.fragments.GameInfoFragment;
import com.team2052.frckrawler.fragments.GamesFragment;
import com.team2052.frckrawler.fragments.MatchListFragment;
import com.team2052.frckrawler.fragments.MetricInfoFragment;
import com.team2052.frckrawler.fragments.MetricsFragment;
import com.team2052.frckrawler.fragments.RobotAttendingEventsFragment;
import com.team2052.frckrawler.fragments.RobotsFragment;
import com.team2052.frckrawler.fragments.ServerFragment;
import com.team2052.frckrawler.fragments.SummaryFragment;
import com.team2052.frckrawler.fragments.TeamInfoFragment;
import com.team2052.frckrawler.fragments.TeamsFragment;
import com.team2052.frckrawler.subscribers.SubscriberModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        FRCKrawlerModule.class,
        SubscriberModule.class,
        BinderModule.class,
}, dependencies = ApplicationComponent.class)
public interface FragmentComponent {
    DBManager dbManager();

    ScoutSyncHandler scoutSyncHander();

    CompilerManager compilerManager();

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

    void inject(ScoutActivity scoutActivity);

    void inject(GameInfoFragment gameInfoFragment);

    void inject(EventInfoFragment eventInfoFragment);

    void inject(TeamInfoFragment teamInfoFragment);

    void inject(AddMetricActivity addMetricActivity);

    void inject(MatchListActivity matchListActivity);

    void inject(MetricInfoActivity metricInfoActivity);

    void inject(SummaryDataActivity summaryDataActivity);

    void inject(MetricInfoFragment metricInfoFragment);
}
