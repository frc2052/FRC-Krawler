package com.team2052.frckrawler.core.metrics.data;

public class Compiler {
    /*Context context;
    RxDBManager rxDBManager;

    public List<String> getHeader(Context context) {
        List<String> header = Lists.newArrayList();
        //TODO
        return header;
    }

    public Compiler(Context context, RxDBManager rxDBManager) {
        this.context = context;
        this.rxDBManager = rxDBManager;
    }

    @WorkerThread
    public List<Metric> getMetrics() {
        QueryBuilder<Metric> query = rxDBManager.getMetricsTable().getQueryBuilder();

        if (PreferenceHelper.compileMatchMetricsToExport(context)) {
            query.where(MetricDao.Properties.Category.eq(MetricHelper.MATCH_PERF_METRICS));
        }

        if (PreferenceHelper.compileMatchMetricsToExport(context)) {
            query.where(MetricDao.Properties.Category.eq(MetricHelper.ROBOT_METRICS));
        }

        return query.list();
    }

    @WorkerThread
    public List<CompiledMetricValue> getMetricSummary(Metric metric) {
        float compile_weight = PreferenceHelper.compileWeight(context);
        List<Team> teams = rxDBManager.getTeamsTable().loadAll();
        for (int i = 0; i < teams.size(); i++) {

        }
    }

    public CompiledMetricValue getTeamMetricSummary(@NonNull Metric metric, @NonNull Team team) {

    }

    @WorkerThread
    public List<MetricValue> getTeamMetricData(@NonNull Metric metric, @NonNull Team team) {
        List<MetricValue> metricValues = Lists.newArrayList();

        switch (metric.getCategory()) {
            case MetricHelper.ROBOT_METRICS:
                List<MatchDatum> match_datum = rxDBManager.getMatchDataTable()
                        .query(team.getNumber(), metric.getId(), null, MetricHelper.MATCH_GAME_TYPE)
                        .orderAsc(MatchDatumDao.Properties.Match_number)
                        .list();
                for (int i = 0; i < match_datum.size(); i++) {
                    MetricDataHelper.INSTANCE.getMapMatchDataToMetricValue()
                }
            case MetricHelper.MATCH_PERF_METRICS:

                break;
        }
        return;
    }*/
}
