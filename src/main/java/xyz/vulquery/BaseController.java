package xyz.vulquery;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import xyz.vulquery.datafeed.DatafeedController;

/**
 * Handles web requests to endpoints
 */
@Controller
public class BaseController {

    String homePageFileName = "index.html";

    /**
     * @return Home console page or usage statement.
     */
    @RequestMapping("/")
    public String home() {
        return homePageFileName;
    }

    /**
     * Obtains all vulnerability information pertaining to dependency.
     *
     * @param groupId project name
     * @param artifactId name of jar
     * @param version version of jar
     * @return Dependency and vulnerability related info
     */
    @GetMapping(value = "/dep", produces = "application/json")
    public String processDependency(@RequestParam(name = "groupid") String groupId,
                                    @RequestParam(name = "artifactid") String artifactId,
                                    @RequestParam(name = "version", required = false) String version) {
        return DatafeedController.getDependency(groupId, artifactId, version);
    }

    /**
     * @return Last date of dependency data feed sync.
     */
    @GetMapping(value = "/lastSyncDate", produces = "application/json")
    public String lastSyncDate() {
        return DatafeedController.getLastSyncDate();
    }
}
