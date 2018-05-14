package xyz.vulquery.datafeed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vulquery.ConfigProperties;

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

    @Autowired
    private ConfigProperties prop;

    private static final String DATAFEED_URL_ROOT = "https://static.nvd.nist.gov/feeds/xml/cve/2.0/nvdcve-2.0-";
    private static final int EARLIEST_FEED_YEAR = 2002;
    private static final int LATEST_FEED_YEAR = 2018;
    private static final String DOWNLOAD_PATH = "";
    private static final String NVD_JSON_PREFIX = "nvdcve-1.0-";
    private static final String EXT_JSON = ".json.zip";
    private static final String EXT_META = ".meta";

    private static final String DATAFEED_URL_MODIFIED = DATAFEED_URL_ROOT + "modified" + EXT_META;

    /**
     * Downloads latest modified/incremental data feed file.
     */
    public String downloadLatest() {

        String filePath = prop.getDataFeedPath()+ "modified";
        try {
            FileUtils.copyURLToFile(new URL(DATAFEED_URL_MODIFIED), new File(filePath));
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return filePath;
    }

    /**
     * Downloads all data feed files.
     */
    public List<String> downloadAll() {
        List<String> filePaths = new LinkedList<>();

        for (int year = EARLIEST_FEED_YEAR; year <= LATEST_FEED_YEAR; year++) {
            try {
                filePaths.add(downloadSpecific(year));
            } catch (MalformedURLException e) {
                // TODO: Skip over year, log error
            } catch (IOException e) {
                // TODO: Skip over year, log error
            }
        }
        return filePaths;
    }

    /**
     * Downloads specific data feed file by year.
     */
    public String downloadSpecific(int year) throws IOException {
        if (year < EARLIEST_FEED_YEAR && year > LATEST_FEED_YEAR) {
            throw new IllegalArgumentException("Year " + year + " is out of range. Please specify year between " + EARLIEST_FEED_YEAR + " and " + LATEST_FEED_YEAR);
        }

        StringBuffer urlSB = new StringBuffer();
        urlSB.append(DATAFEED_URL_ROOT);
        urlSB.append(year);
        urlSB.append(EXT_JSON);

        String filePath = prop.getDataFeedPath()+ NVD_JSON_PREFIX + year + EXT_JSON;
        FileUtils.copyURLToFile(new URL(urlSB.toString()), new File(filePath));

        return filePath;
    }
}
