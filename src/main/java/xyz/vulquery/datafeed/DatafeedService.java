package xyz.vulquery.datafeed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import xyz.vulquery.dao.DependencyDAO;
import xyz.vulquery.dependency.Dependency;

import xyz.vulquery.parser.DataFeedParser;
import xyz.vulquery.parser.NVDNISTJSONParser;
import xyz.vulquery.util.HTTPResponseUtil;
import xyz.vulquery.util.StringUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

/**
 * Logic-tier to RESTFUL API
 */
@Component("datafeedService")
public class DatafeedService {

    private final Logger logger = LoggerFactory.getLogger(DatafeedService.class);

    @Autowired
    private DependencyDAO dependencyDAO;

    @Autowired
    private Downloader downloader;

    @Autowired
    private NVDNISTJSONParser datafeedParser; // TODO: Use generic interface instead.


    /*
    #############################################################################
    # RESTFUL METHODS
    #############################################################################
    */

    /**
     * Retrieves all dependency-vulnerability information of matching dependency group and artifact id
     * and provide upgrade path recommendations based on vulnerability states of all versions
     * in relation to provided version.
     * @param groupId project unique identifier
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

        logger.debug("Conducting sync...");

        String filePath = null;
        try {
            filePath = downloader.downloadAndExtractLatest();
            if (StringUtils.isBlank(filePath)) {
                logger.error("Failed to sync - Extracted modified data feed file path was null or empty.");
            } else {
                //dependencyDAO.addDependency();

                // TODO: Pass file path to parser, then return a (deserialized) dependency object.

                dependencyDAO.updateSyncDate(new Date());
            }
        } catch (IOException e) {
            logger.error("Failed to sync - " + e.getMessage());
        }
    }

    /**
     * Removes all dependency-vulnerability info from storage state.
     * then force updates from data feed.
     */
    public void cleanseAndUpdate() {
        logger.debug("Conducting cleanse and full sync...");
        dependencyDAO.removeAll();
        fullSync();
    }

    /**
     * Updates dependency-vulnerability info with all data feeds.
     */
    public void fullSync() {

        logger.debug("Conducting full sync...");

        List<String> dataFeedFilePaths = downloader.downloadAndExtractAll();
        if (dataFeedFilePaths == null || dataFeedFilePaths.size() == 0) {
            logger.error("Extracted data feed file paths were null or empty.");
        } else {
            int failures = 0;
            for (String filePath : dataFeedFilePaths) {
                if (StringUtils.isBlank(filePath)) {
                    logger.error("File path for dependency decoding was null or blank.");
                    failures++;
                } else {
                    List<Dependency> dependencies = null;
                    try {
                        dependencies = datafeedParser.decode(readFile(filePath, Charset.defaultCharset()));
                    } catch (IOException e) {
                        logger.error("Error obtaining JSON string from file: " + filePath);
                        failures++;
                    }

                    if (dependencies == null || dependencies.size() == 0) {
                        logger.error("No dependencies or internal error during decoding.");
                        failures = dataFeedFilePaths.size();
                    } else {
                        for (Dependency dependency : dependencies) {
                            dependencyDAO.addDependency(dependency);
                        }
                    }
                }
            }

            if (failures == 0) {
                logger.info("All files decoded and synced with database");
                dependencyDAO.updateSyncDate(new Date());
            } else if (failures > 0 && failures < dataFeedFilePaths.size()) {
                logger.error(failures + " files failed to decode or sync with database.");
                dependencyDAO.updateSyncDate(new Date());
            } else {
                logger.error("All files failed to decode or sync with database: " + failures);
            }

        }
    }

    /**
     * Stores entire JSON data into a single string.
     * TODO: Is there a more efficient way to do this than storing entire string in memory?
     * TODO: Okay for now since each file size is not significantly large.
     *
     * Borrowed from Stack Overflow at
     * https://stackoverflow.com/questions/326390/how-do-i-create-a-java-string-from-the-contents-of-a-file
     *
     * @param path absolute path to file
     * @param encoding type of encoding to convert String
     * @return Contents of file as a single string
     * @throws IOException
     */
    private String readFile(String path, Charset encoding) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), encoding);
    }

}
