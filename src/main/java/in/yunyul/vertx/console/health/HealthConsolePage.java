package in.yunyul.vertx.console.health;

import in.yunyul.vertx.console.base.ConsolePage;
import io.vertx.core.Vertx;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.HealthChecks;
import io.vertx.ext.web.Router;

@SuppressWarnings("unused")
public class HealthConsolePage implements ConsolePage {
    private final HealthChecks healthChecks;

    /**
     * Creates the health console page, using the provided health checks
     *
     * @param healthChecks the health checks to retrieve data from
     * @return the console page
     */
    public static HealthConsolePage create(HealthChecks healthChecks) {
        return new HealthConsolePage(healthChecks);
    }

    private HealthConsolePage(HealthChecks healthChecks) {
        this.healthChecks = healthChecks;
    }

    @Override
    public void mount(Vertx vertx, Router router, String basePath) {
        router.route(basePath + "/healthchecks").produces("application/json")
                .handler(HealthCheckHandler.createWithHealthChecks(healthChecks));
    }

    @Override
    public String getLoaderFileName() {
        return "/js/health.js";
    }
}
