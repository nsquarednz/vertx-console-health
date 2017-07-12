package in.yunyul.vertx.console.health;

import in.yunyul.vertx.console.base.ConsolePage;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.HealthChecks;
import io.vertx.ext.web.Router;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class HealthConsolePage implements ConsolePage {
    private static final String JSON_CONTENT_TYPE = "application/json";

    private final HealthChecks healthChecks;
    private final long uptimeCollectorInterval;
    private final long windowSize;

    public static HealthConsolePage create(HealthChecks healthChecks) {
        return new HealthConsolePage(healthChecks, 5000, 600000);
    }

    public static HealthConsolePage create(HealthChecks healthChecks, long uptimeCollectorInterval, long windowInterval) {
        return new HealthConsolePage(healthChecks, uptimeCollectorInterval, windowInterval);
    }

    public HealthConsolePage(HealthChecks healthChecks, long uptimeCollectorInterval, long windowInterval) {
        this.healthChecks = healthChecks;
        this.uptimeCollectorInterval = uptimeCollectorInterval;
        if (windowInterval % uptimeCollectorInterval != 0) {
            throw new IllegalArgumentException("Window interval is not divisible by collector interval");
        }
        this.windowSize = windowInterval / uptimeCollectorInterval;
    }

    private JsonObject transform(JsonObject json) {
        String status = json.getString("status");
        String outcome = json.getString("outcome");
        if (status != null && outcome == null) {
            json.put("outcome", status);
        }
        return json;
    }

    private Map<String, UptimeCounter> uptimes = new HashMap<>();

    private JsonArray updateUptimes(JsonObject json, boolean writeUptimes) {
        JsonArray checks = json.getJsonArray("checks");
        for (Object checkObj : checks) {
            JsonObject checkJson = (JsonObject) checkObj;
            String id = checkJson.getString("id");
            UptimeCounter counter = uptimes.computeIfAbsent(id, k -> new UptimeCounter(windowSize));
            boolean isUp = checkJson.getString("status").equals("UP");
            if (writeUptimes) {
                float uptimeRatio;
                try {
                    uptimeRatio = counter.getUptimeRatio();
                } catch (IllegalStateException e) {
                    uptimeRatio = isUp ? 1 : 0;
                }
                checkJson.put("uptimeRatio", uptimeRatio);
            } else {
                counter.updateStatus(isUp);
            }
        }
        return checks;
    }

    @Override
    public void mount(Vertx vertx, Router router, String basePath) {
        Handler<Long> uptimeTask = taskId -> healthChecks.invoke(json -> updateUptimes(json, false));
        uptimeTask.handle(0L);
        vertx.setPeriodic(uptimeCollectorInterval, uptimeTask);
        router.route(basePath + "/health/status").produces(JSON_CONTENT_TYPE).handler(ctx ->
                healthChecks.invoke(json -> {
                    ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, JSON_CONTENT_TYPE)
                            .end(updateUptimes(json, true).toBuffer());
                }));
    }

    @Override
    public String getLoaderFileName() {
        return "/js/health.js";
    }
}
