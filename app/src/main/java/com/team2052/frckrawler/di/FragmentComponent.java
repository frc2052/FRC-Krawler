package com.team2052.frckrawler.di;

import com.team2052.frckrawler.activities.AddMetricActivity;
import com.team2052.frckrawler.activities.HomeActivity;
import com.team2052.frckrawler.activities.ImportMetricsActivity;
import com.team2052.frckrawler.activities.MetricInfoActivity;
import com.team2052.frckrawler.activities.MetricsActivity;
import com.team2052.frckrawler.activities.ScoutActivity;
import com.team2052.frckrawler.activities.ServerLogActivity;
import com.team2052.frckrawler.activities.SummaryDataActivity;
import com.team2052.frckrawler.activities.TeamInfoActivity;
import com.team2052.frckrawler.data.RxDBManager;
import com.team2052.frckrawler.di.binding.BinderModule;
import com.team2052.frckrawler.di.subscribers.SubscriberModule;
import com.team2052.frckrawler.fragments.SummaryFragment;
import com.team2052.frckrawler.fragments.dialog.ExportDialogFragment;
import com.team2052.frckrawler.fragments.metric.MetricInfoFragment;
import com.team2052.frckrawler.fragments.metric.MetricsFragment;
import com.team2052.frckrawler.fragments.scout.ScoutHomeFragment;
import com.team2052.frckrawler.fragments.team.TeamInfoFragment;
import com.team2052.frckrawler.metric.data.RxCompiler;

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

    RxCompiler compilerManager();

    void inject(HomeActivity activity);

    void inject(MetricsActivity metricsActivity);

    void inject(TeamInfoActivity teamInfoActivity);

    void inject(ScoutActivity scoutActivity);

    void inject(AddMetricActivity addMetricActivity);

    void inject(MetricInfoActivity metricInfoActivity);

    void inject(ImportMetricsActivity importMetricsActivity);

    void inject(SummaryDataActivity summaryDataActivity);

    void inject(MetricsFragment metricsFragment);

    void inject(SummaryFragment summaryFragment);

    void inject(TeamInfoFragment teamInfoFragment);

    void inject(MetricInfoFragment metricInfoFragment);

    void inject(ScoutHomeFragment scoutHomeFragment);

    void inject(ExportDialogFragment exportDialogFragment);

    void inject(ServerLogActivity serverLogActivity);

}
