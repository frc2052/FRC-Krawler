package com.team2052.frckrawler.di;

import com.team2052.frckrawler.activities.AddMetricActivity;
import com.team2052.frckrawler.activities.EventInfoActivity;
import com.team2052.frckrawler.activities.GameInfoActivity;
import com.team2052.frckrawler.activities.HomeActivity;
import com.team2052.frckrawler.activities.ImportMetricsActivity;
import com.team2052.frckrawler.activities.MetricInfoActivity;
import com.team2052.frckrawler.activities.RobotActivity;
import com.team2052.frckrawler.activities.RobotEventActivity;
import com.team2052.frckrawler.activities.ScoutActivity;
import com.team2052.frckrawler.activities.ServerLogActivity;
import com.team2052.frckrawler.activities.SummaryDataActivity;
import com.team2052.frckrawler.activities.TeamInfoActivity;
import com.team2052.frckrawler.binding.BinderModule;
import com.team2052.frckrawler.database.RxDBManager;
import com.team2052.frckrawler.database.metric.Compiler;
import com.team2052.frckrawler.fragments.EventInfoFragment;
import com.team2052.frckrawler.fragments.EventsFragment;
import com.team2052.frckrawler.fragments.GameInfoFragment;
import com.team2052.frckrawler.fragments.GamesFragment;
import com.team2052.frckrawler.fragments.MatchListFragment;
import com.team2052.frckrawler.fragments.MetricInfoFragment;
import com.team2052.frckrawler.fragments.MetricSummaryFragment;
import com.team2052.frckrawler.fragments.MetricsFragment;
import com.team2052.frckrawler.fragments.RobotEventMatchesFragment;
import com.team2052.frckrawler.fragments.robot.RobotAttendingEventsFragment;
import com.team2052.frckrawler.fragments.robot.RobotEventSummaryFragment;
import com.team2052.frckrawler.fragments.robot.RobotSummaryFragment;
import com.team2052.frckrawler.fragments.robot.RobotsFragment;
import com.team2052.frckrawler.fragments.ScoutHomeFragment;
import com.team2052.frckrawler.fragments.ServerFragment;
import com.team2052.frckrawler.fragments.SummaryFragment;
import com.team2052.frckrawler.fragments.TeamInfoFragment;
import com.team2052.frckrawler.fragments.TeamsFragment;
import com.team2052.frckrawler.fragments.dialog.ExportDialogFragment;
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
    RxDBManager dbManager();

    Compiler compilerManager();

    void inject(HomeActivity activity);

    void inject(GameInfoActivity gameInfoActivity);

    void inject(EventInfoActivity eventInfoActivity);

    void inject(TeamInfoActivity teamInfoActivity);

    void inject(ScoutActivity scoutActivity);

    void inject(RobotActivity robotActivity);

    void inject(AddMetricActivity addMetricActivity);

    void inject(MetricInfoActivity metricInfoActivity);

    void inject(ImportMetricsActivity importMetricsActivity);

    void inject(RobotEventActivity robotEventActivity);

    void inject(SummaryDataActivity summaryDataActivity);

    void inject(GamesFragment gamesFragment);

    void inject(EventsFragment eventsNewFragment);

    void inject(MetricsFragment metricsFragment);

    void inject(TeamsFragment teamsFragment);

    void inject(MatchListFragment matchListFragment);

    void inject(RobotAttendingEventsFragment robotAttendingEventsFragment);

    void inject(RobotsFragment robotsFragment);

    void inject(SummaryFragment summaryFragment);

    void inject(ServerFragment serverFragment);

    void inject(GameInfoFragment gameInfoFragment);

    void inject(EventInfoFragment eventInfoFragment);

    void inject(TeamInfoFragment teamInfoFragment);

    void inject(MetricInfoFragment metricInfoFragment);

    void inject(RobotSummaryFragment robotSummaryFragment);

    void inject(RobotEventSummaryFragment robotEventSummaryFragment);

    void inject(MetricSummaryFragment metricSummaryFragment);

    void inject(ScoutHomeFragment scoutHomeFragment);

    void inject(ExportDialogFragment exportDialogFragment);

    void inject(ServerLogActivity serverLogActivity);

    void inject(RobotEventMatchesFragment robotEventMatchesFragment);
}
