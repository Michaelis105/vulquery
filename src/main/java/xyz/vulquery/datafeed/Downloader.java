package xyz.vulquery.datafeed;

import com.sun.javaws.exceptions.InvalidArgumentException;
import org.apache.commons.io.FileUtils;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Handles downloading of all dependency-vulnerability files from some data feed source.
 * Data feed is standardized to NVD NIST GOV.
 */
public class Downloader {
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
    public void downloadLatest() {
        try {
            FileUtils.copyURLToFile(new URL(DATAFEED_URL_MODIFIED), new File(""));
        } catch (IOException e) {
            //e.printStackTrace();
        }

        throw new NotImplementedException();
    }

    /**
     * Downloads all data feed files.
     */
    public void downloadAll() {
        for (int year = EARLIEST_FEED_YEAR; year <= LATEST_FEED_YEAR; year++) {
            try {
                downloadSpecific(year);
            } catch (InvalidArgumentException e) {
                // TODO: Skip over year, log error
                //e.printStackTrace();
            }
        }

        throw new NotImplementedException();
    }

    /**
     * Downloads specific data feed file by year.
     */
    public void downloadSpecific(int year) throws InvalidArgumentException {
        if (year < EARLIEST_FEED_YEAR && year > LATEST_FEED_YEAR) {
            throw new InvalidArgumentException(new String[]{"Year " + year + " is out of range. Please specify year between " + EARLIEST_FEED_YEAR + " and " + LATEST_FEED_YEAR});
        }

        StringBuffer urlSB = new StringBuffer();
        urlSB.append(DATAFEED_URL_ROOT);
        urlSB.append(year);
        urlSB.append(EXT_JSON);

        try {
            FileUtils.copyURLToFile(new URL(urlSB.toString()), new File(""));
        } catch (IOException e) {
            //e.printStackTrace();
        }

        throw new NotImplementedException();
    }
}
