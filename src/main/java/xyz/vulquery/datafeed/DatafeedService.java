package xyz.vulquery.datafeed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import xyz.vulquery.dao.DependencyDAO;
import xyz.vulquery.dependency.Dependency;
import xyz.vulquery.util.HTTPResponseUtil;
import xyz.vulquery.util.StringUtils;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Logic-tier to RESTFUL API
 */
@Component("dataFeedService")
public class DatafeedService {

    private final Logger logger = LoggerFactory.getLogger(DatafeedService.class);

    @Autowired
    private DependencyDAO dependencyDAO;

    @Autowired
    private Downloader downloader;

    /*
    #############################################################################
    # RESTFUL METHODS
    #############################################################################
    */

    /**
     * Retrieves all dependency-vulnerability information of matching dependency group and artifact id
     * and provide upgrade path recommendations based on vulnerability states of all versions
     * in relation to provided version.
     * @param groupId project unique identification
     * @param artifactId name of jar/dependency
     * @param version version id of jar/dependency
     * @return JSON dependency-vulnerability details in addition to upgrade paths
     */
    public String getDependency(String groupId, String artifactId, String version) {
        logger.debug("Group ID: " + groupId);
        logger.debug("Artifact ID: " + artifactId);
        logger.debug("Version: " + version);

        if (StringUtils.isBlank(groupId)) {
            return HTTPResponseUtil.createJSONMessage("Group ID " + groupId + " is null or blank.");
        }

        if (StringUtils.isBlank(artifactId)) {
            return HTTPResponseUtil.createJSONMessage("Artifact ID " + artifactId + " is null or blank.");
        }

        List<Dependency> depList = dependencyDAO.getDependency(groupId, artifactId);
        if (depList == null) {
            return HTTPResponseUtil.createJSONMessage("Internal Service Error with null dependency List."); // TODO: Replace with different/better message.
        } else if (depList.size() == 0) {
            return HTTPResponseUtil.createJSONMessage("Failed to find any dependency with given group and artifact ID.");
        }

        if (StringUtils.isBlank(version)) {
            // TODO: Analyze all dependencies for vulnerabilities, then determine best upgrade.
        } else {
            // TODO: Analyze all dependencies for vulnerabilities, then determine best upgrade based on current version.
        }

        // TODO: Convert analysis into JSON in some format (TBD).
        //Gson gson = new Gson();
        //return gson.toJson(d);

        throw new UnsupportedOperationException();
    }

    /**
     * Retrieves date of last dependency-vulnerability
     * @return date of last dependency-vulnerability
     */
    public String getLastSyncDate() {
        return HTTPResponseUtil.createJSONMessage(dependencyDAO.getSyncDate());
    }

    /**
     * Tests connectivity to web service.
     * @return pong, delayed or timeout message suggests connectivity issues.
     */
    public String ping() {
        return HTTPResponseUtil.createJSONMessage("pong");
    }

    /*
    #############################################################################
    # ADMIN/BATCH METHODS
    #############################################################################
    */

    /**
     * Updates dependency-vulnerability info with latest/modified state from data feed.
     * @param force true to force download regardless of any condition (e.g. update frequency, no difference),
     *              false otherwise.
     */
    public void sync(boolean force) {
        String filePath = downloader.downloadLatest();
        if (StringUtils.isBlank(filePath)) {
            // TODO: Throw error/exception
        }

        // TODO: Pass file path to parser, then return a (deserialized) dependency object.
        //dependencyDAO.addDependency();

        dependencyDAO.updateSyncDate(new Date());
    }

    /**
     * Removes all dependency-vulnerability info from storage state.
     * then force updates from data feed.
     */
    public void cleanseAndUpdate() {
        dependencyDAO.removeAll();
        fullSync();
    }

    /**
     * Updates dependency-vulnerability info with all
     */
    public void fullSync() {
        List<String> filePaths = downloader.downloadAndExtractAll();
        if (filePaths == null) {
            // TODO: Throw error/exception
        } else if (filePaths.size() == 0) {
            // TODO: Throw error/exception???
        }

        for (String filePath : filePaths) {
            // TODO: Pass file path to parser, then return a (deserialized) dependency object.
            //dependencyDAO.addDependency();
        }

        dependencyDAO.updateSyncDate(new Date());
    }

}
