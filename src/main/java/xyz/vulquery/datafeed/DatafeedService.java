package xyz.vulquery.datafeed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import xyz.vulquery.dao.DependencyDAO;
import xyz.vulquery.dependency.Dependency;
import xyz.vulquery.util.HTTPResponseUtil;
import xyz.vulquery.util.StringUtils;

import com.google.gson.Gson;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@Component
public class DatafeedController {

    @Autowired
    private DependencyDAO dependencyDAO;

    /** RESTFUL METHODS **/

    /**
     * Retrives all vulnerability information
     * @param groupId
     * @param artifactId
     * @param version
     * @return
     */
    public static String getDependency(String groupId, String artifactId, String version) {
        if (StringUtils.isBlank(groupId)) {
            return HTTPResponseUtil.createJSONMessage("Group ID " + groupId + " is null or blank.");
        }

        if (StringUtils.isBlank(artifactId)) {
            return HTTPResponseUtil.createJSONMessage("Artifact ID " + artifactId + " is null or blank.");
        }

        Dependency d = new Dependency();
        Gson gson = new Gson();

        //return gson.toJson(d);

        throw new NotImplementedException();


    }

    /**
     * Retrives date of last dependency-vulnerability
     * @return date of last dependency-vulnerability
     */
    public static String getLastSyncDate() {
        throw new NotImplementedException();
    }

    /**
     * Tests connectivity to web service.
     * @return pong, delayed or timeout message suggest connectivity issues.
     */
    public static String ping() {
        return HTTPResponseUtil.createJSONMessage("pong");
    }

    /** ADMIN METHODS **/

    /**
     * Update dependency-vulnerability info with latest/modified state from data feed.
     * @param force true to force download regardless of any condition (e.g. update frequency, no difference),
     *              false otherwise.
     * @return
     */
    public static String sync(boolean force) {
        throw new NotImplementedException();
    }

    /**
     * Removes all dependency-vulnerability info from storage state.
     * then force updates from data feed.
     * @return
     */
    public static String cleanseAndUpdate() {
        throw new NotImplementedException();
    }

}
