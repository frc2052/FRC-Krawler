package com.team2052.frckrawler.database;

import com.team2052.frckrawler.database.models.MetricMatchData;

import java.util.List;

/**
 * @author Adam
 */
public class MetricCompiler {
    /**
     * Compile all the corresponding data in the list and return the compiled value
     * Make sure the MatchData is corresponding to the metric and the same robot
     *
     * @param metricMatchDataList
     * @return
     */
    public static String compileData(List<MetricMatchData> metricMatchDataList) {
        for (MetricMatchData data : metricMatchDataList) {
            //TODO
        }
        return "";
    }
}
