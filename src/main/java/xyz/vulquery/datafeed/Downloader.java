package xyz.vulquery.datafeed;

import org.springframework.stereotype.Component;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.vulquery.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * Handles downloading of all dependency-vulnerability files from some data feed source (NVD NIST GOV).
 */
@Component("downloader")
public class Downloader {

    private final Logger logger = LoggerFactory.getLogger(Downloader.class);

    private static final String DATAFEED_URL_ROOT = "https://nvd.nist.gov/feeds/json/cve/1.0/nvdcve-1.0-";
    private static final int EARLIEST_FEED_YEAR = 2002;
    private static final int LATEST_FEED_YEAR = 2018; // TODO: Make this maximum dynamic.

    private static final String NVD_JSON_PREFIX = "nvdcve-1.0-";
    private static final String EXT_JSON = ".json.zip";
    private static final String EXT_META = ".meta";

    private static final String DATAFEED_URL_MODIFIED = DATAFEED_URL_ROOT + "modified" + EXT_META;

    private static String DOWNLOAD_PATH;

    public void init(String path) throws IOException {
        if (StringUtils.isBlank(path)) {
            throw new IllegalArgumentException("Download directory path is null or empty.");
        }

        path += File.separator + "datafeed";

        logger.debug("Download directory: " + path);
        File downloadDir = new File(path);
        if (!downloadDir.exists()) {
            logger.debug("Download directory does not exist, creating...");
            if (!downloadDir.mkdirs()) {
                throw new IOException("Error creating download directory.");
            } else {
                logger.debug("Download directory created.");
            }
        } else {
            logger.debug("Download directory already exists.");
        }

        DOWNLOAD_PATH = path;
    }

    /**
     * Downloads latest modified/incremental data feed file.
     * @return path where data feed file was downloaded
     */
    public String downloadLatest() {
        String filePath = DOWNLOAD_PATH + File.separator + "modified";

        logger.debug("Modified file download path: " + filePath);

        // Ensure using latest modified file by base-lining staging modified feed.
        File modifiedFile = new File(filePath);
        if (modifiedFile.exists()) {
            logger.debug("Modified file exists, deleting..." + filePath);
            modifiedFile.delete();
        }

        try {
            FileUtils.copyURLToFile(new URL(DATAFEED_URL_MODIFIED), modifiedFile);
        } catch (IOException e) {
            logger.error("Error downloading modified file feed at path: " + filePath);
        }
        return filePath;
    }

    /**
     * Downloads all data feed files.
     *
     * Total feed size is about 50 MB as of 05/19/2018.
     *
     * @return list of paths where data feed files were downloaded
     */
    public List<String> downloadAll() {
        List<String> filePaths = new LinkedList<>();
        logger.debug("Downloading all data feed files...");
        for (int year = EARLIEST_FEED_YEAR; year <= LATEST_FEED_YEAR; year++) {
            try {
                filePaths.add(downloadSpecific(year));
            } catch (MalformedURLException e) {
                logger.error("Incorrect URL formed for year " + year);
            } catch (IOException e) {
                logger.error("Failed to download data feed file for year " + year);
            }
        }
        return filePaths;
    }

    /**
     * Downloads specific data feed file by year.
     * @return path where data feed file was downloaded
     */
    public String downloadSpecific(int year) throws IOException {

        logger.debug("Downloading data feed files year: " + year);

        if (year < EARLIEST_FEED_YEAR && year > LATEST_FEED_YEAR) {
            throw new IllegalArgumentException("Year " + year + " is out of range. Please specify year between " + EARLIEST_FEED_YEAR + " and " + LATEST_FEED_YEAR);
        }

        StringBuffer urlSB = new StringBuffer();
        urlSB.append(DATAFEED_URL_ROOT);
        urlSB.append(year);
        urlSB.append(EXT_JSON);

        String filePath = DOWNLOAD_PATH + File.separator + NVD_JSON_PREFIX + year + EXT_JSON;

        logger.debug("Downloading year " + year + " data feed file... from URL " + urlSB.toString() + " to file path " + filePath);

        // Baseline data feed.
        File file = new File(filePath);
        if (file.exists()) {
            logger.debug("Year file exists, deleting..." + filePath);
            file.delete();
        }

        FileUtils.copyURLToFile(new URL(urlSB.toString()), file);

        return filePath;
    }
}
