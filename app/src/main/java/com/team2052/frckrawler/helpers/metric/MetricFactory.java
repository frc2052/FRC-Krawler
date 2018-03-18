package com.team2052.frckrawler.helpers.metric;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.team2052.frckrawler.data.tba.v3.JSON;
import com.team2052.frckrawler.models.Metric;

import java.util.List;

public class MetricFactory {
    @MetricHelper.MetricCategory
    int metricCategory;
    @MetricHelper.MetricType
    int metricType;
    String name;
    JsonObject data = new JsonObject();
    private Long game_id = 0L;

    public MetricFactory(String name) {
        if (name.isEmpty())
            throw new IllegalStateException("Couldn't create MetricFactory");
        this.name = name;
    }

    public void setMetricType(@MetricHelper.MetricType int metricType) {
        this.metricType = metricType;
    }

    public void setMetricCategory(@MetricHelper.MetricCategory int metricCategory) {
        this.metricCategory = metricCategory;
    }

    public void setDataListIndexValue(List<String> names) {
        JsonElement jsonElements = JSON.getGson().toJsonTree(names);
        data.add("values", jsonElements);
    }

    public void setDataMinMaxInc(int min, int max, Integer inc) {
        data.addProperty("min", min);
        data.addProperty("max", max);
        if (inc != null)
            data.addProperty("inc", inc);
    }

    public void setDescription(String description) {
        data.addProperty("description", Strings.nullToEmpty(description));
    }

    private void clean() {
        //Clean up data if needed
        if (!data.has("description"))
            data.addProperty("description", "");

        switch (metricType) {
            case MetricHelper.TEXT_FIELD:
            case MetricHelper.STOP_WATCH:
            case MetricHelper.BOOLEAN:
                if (data.has("values"))
                    data.remove("values");
                if (data.has("min"))
                    data.remove("min");
                if (data.has("max"))
                    data.remove("max");
                if (data.has("inc"))
                    data.remove("inc");
                break;
            case MetricHelper.SLIDER:
            case MetricHelper.COUNTER:
                if (data.has("values"))
                    data.remove("values");
                break;
            case MetricHelper.CHOOSER:
            case MetricHelper.CHECK_BOX:
                if (data.has("min"))
                    data.remove("min");
                if (data.has("max"))
                    data.remove("max");
                if (data.has("inc"))
                    data.remove("inc");
                break;
        }
    }

    public void setDataRaw(String string) {
        if (string.startsWith("\"")) {
            string = string.substring(1);
        }

        if (string.endsWith("\"")) {
            string = string.substring(0, string.length() - 1);
        }
        System.out.println(string);
        data = JSON.getAsJsonObject(string);
    }

    public void setGameId(Long game_id) {
        this.game_id = game_id;
    }

    public Metric buildMetric() {
        clean();
        return new Metric(
                null,
                name,
                metricCategory,
                metricType,
                JSON.getGson().toJson(data),
                true,
                0);
    }
}
