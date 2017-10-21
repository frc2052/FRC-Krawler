package com.team2052.frckrawler.di;

import com.team2052.frckrawler.activities.AddMetricActivity;
import com.team2052.frckrawler.activities.EventInfoActivity;
import com.team2052.frckrawler.activities.HomeActivity;
import com.team2052.frckrawler.activities.ImportMetricsActivity;
import com.team2052.frckrawler.activities.MetricInfoActivity;
import com.team2052.frckrawler.activities.RobotActivity;
import com.team2052.frckrawler.activities.RobotEventActivity;
import com.team2052.frckrawler.activities.ScoutActivity;
import com.team2052.frckrawler.activities.SeasonInfoActivity;
import com.team2052.frckrawler.activities.ServerLogActivity;
import com.team2052.frckrawler.activities.SummaryDataActivity;
import com.team2052.frckrawler.activities.TeamInfoActivity;
import com.team2052.frckrawler.data.RxDBManager;
import com.team2052.frckrawler.di.binding.BinderModule;
import com.team2052.frckrawler.di.subscribers.SubscriberModule;
import com.team2052.frckrawler.fragments.SummaryFragment;
import com.team2052.frckrawler.fragments.dialog.ExportDialogFragment;
import com.team2052.frckrawler.fragments.event.EventInfoFragment;
import com.team2052.frckrawler.fragments.event.EventsInGameFragment;
import com.team2052.frckrawler.fragments.game.SeasonInfoFragment;
import com.team2052.frckrawler.fragments.metric.MetricInfoFragment;
import com.team2052.frckrawler.fragments.metric.MetricsFragment;
import com.team2052.frckrawler.fragments.robot.RobotAttendingEventsFragment;
import com.team2052.frckrawler.fragments.robot.RobotEventMatchesFragment;
import com.team2052.frckrawler.fragments.robot.RobotEventSummaryFragment;
import com.team2052.frckrawler.fragments.robot.RobotSummaryFragment;
import com.team2052.frckrawler.fragments.robot.RobotsFragment;
import com.team2052.frckrawler.fragments.scout.ScoutHomeFragment;
import com.team2052.frckrawler.fragments.team.TeamInfoFragment;
import com.team2052.frckrawler.metric.data.Compiler;

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

    void inject(SeasonInfoActivity seasonInfoActivity);

    void inject(EventInfoActivity eventInfoActivity);

    void inject(TeamInfoActivity teamInfoActivity);

    void inject(ScoutActivity scoutActivity);

    void inject(RobotActivity robotActivity);

    void inject(AddMetricActivity addMetricActivity);

    void inject(MetricInfoActivity metricInfoActivity);

    void inject(ImportMetricsActivity importMetricsActivity);

    void inject(RobotEventActivity robotEventActivity);

    void inject(SummaryDataActivity summaryDataActivity);

    void inject(EventsInGameFragment eventsNewFragment);

    void inject(MetricsFragment metricsFragment);

    void inject(RobotAttendingEventsFragment robotAttendingEventsFragment);

    void inject(RobotsFragment robotsFragment);

    void inject(SummaryFragment summaryFragment);

    void inject(SeasonInfoFragment seasonInfoFragment);

    void inject(EventInfoFragment eventInfoFragment);

    void inject(TeamInfoFragment teamInfoFragment);

    void inject(MetricInfoFragment metricInfoFragment);

    void inject(RobotSummaryFragment robotSummaryFragment);

    void inject(RobotEventSummaryFragment robotEventSummaryFragment);

    void inject(ScoutHomeFragment scoutHomeFragment);

    void inject(ExportDialogFragment exportDialogFragment);

    void inject(ServerLogActivity serverLogActivity);

    void inject(RobotEventMatchesFragment robotEventMatchesFragment);
}
