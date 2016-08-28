package com.team2052.frckrawler.firebase.models;

import java.util.List;

/**
 * Model for a set of metrics for the Firebase application
 *
 * This model is used to keep sets of metrics with a name and description value
 */
public class MetricImportModel {
    public String name;
    public String description;
    public List<FirebaseMetric> metrics;

    public MetricImportModel() {
    }

}
