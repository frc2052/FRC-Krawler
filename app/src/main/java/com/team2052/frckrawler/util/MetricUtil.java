package com.team2052.frckrawler.util;

/**
 * Created by adam on 3/28/15.
 */

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.tba.JSON;

import java.util.List;

/**
 * @author Adam
 * @since 12/21/2014.
 */
public class MetricUtil {
    public enum MetricCategory {
        MATCH_PERF_METRICS, ROBOT_METRICS;
        public int id = ordinal();
    }

    public enum MetricType {
        BOOLEAN, COUNTER, SLIDER, CHOOSER, CHECK_BOX;
        public int id = ordinal();
    }

    public static class MetricFactory {
        final Game game;
        MetricCategory metricCategory;
        MetricType metricType;
        String name;
        JsonObject data = new JsonObject();

        public MetricFactory(Game game, String name) {
            if (game == null || name.isEmpty())
                throw new IllegalStateException("Couldn't create MetricFactory");
            this.game = game;
            this.name = name;
        }

        public void setMetricType(MetricType metricType) {
            this.metricType = metricType;
        }

        public void setMetricCategory(MetricCategory metricCategory) {
            this.metricCategory = metricCategory;
        }

        public void setDataListIndexValue(List<String> names) {
            JsonElement jsonElements = JSON.getGson().toJsonTree(names);
            data.add("values", jsonElements);
        }

        public void setDataMinMaxInc(int min, int max, Optional<Integer> inc) {
            data.addProperty("min", min);
            data.addProperty("max", max);
            if (inc.isPresent())
                data.addProperty("inc", inc.get());
        }

        public void setDescription(String description) {
            data.addProperty("description", Strings.nullToEmpty(description));
        }

        public Metric buildMetric() {
            //Clean up data if needed
            if (!data.has("description"))
                data.addProperty("description", "");

            switch (metricType) {
                case BOOLEAN:
                    if (data.has("values"))
                        data.remove("values");
                    if (data.has("min"))
                        data.remove("min");
                    if (data.has("max"))
                        data.remove("max");
                    if (data.has("inc"))
                        data.remove("inc");
                    break;
                case SLIDER:
                case COUNTER:
                    if (data.has("values"))
                        data.remove("values");
                    break;
                case CHOOSER:
                case CHECK_BOX:
                    if (data.has("min"))
                        data.remove("min");
                    if (data.has("max"))
                        data.remove("max");
                    if (data.has("inc"))
                        data.remove("inc");
                    break;
            }

            return new Metric(
                    null,
                    name,
                    metricCategory.id,
                    metricType.id,
                    JSON.getGson().toJson(data),
                    game.getId());
        }
    }
}