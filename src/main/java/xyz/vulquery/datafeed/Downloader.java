package xyz.vulquery.datafeed;

import org.springframework.stereotype.Component;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.vulquery.util.StringUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
    private static final String EXT_JSON = ".json";
    private static final String EXT_JSON_ZIP = ".json.zip";
    private static final String EXT_META = ".meta";

    private static final String DATAFEED_DIRECTORY_NAME = "datafeed";

    private static final String DATAFEED_URL_MODIFIED = DATAFEED_URL_ROOT + "modified" + EXT_META;

    private static String DOWNLOAD_PATH;

    public void init(String path) throws IOException {
        if (StringUtils.isBlank(path)) {
            throw new IllegalArgumentException("Download directory path is null or empty.");
        }

        path += File.separator + DATAFEED_DIRECTORY_NAME;

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
     * Total feed size is about 50 MB compressed, 1 GB uncompressed as of 05/19/2018
     * @return list of paths where data feed files were downloaded and extracted
     */
    public List<String> downloadAndExtractAll() {
        List<String> filePaths = new LinkedList<>();
        logger.debug("Downloading all data feed files...");

        // TODO: Use multithreading to download and extract files.
        for (int year = EARLIEST_FEED_YEAR; year <= LATEST_FEED_YEAR; year++) {
            try {
                String zipPath = downloadSpecific(year);
                File zipPathFile = new File(zipPath);
                filePaths.add(extractSpecific(zipPath));
                if (!zipPathFile.delete()) {
                    logger.warn("Failed to clean up zip data feed file at " + zipPathFile.getCanonicalPath());
                }
            } catch (MalformedURLException e) {
                logger.error("Incorrect URL formed for year " + year);
            } catch (IOException e) {
                logger.error("Failed to download or extract data feed file for year " + year);
            }
        }
        return filePaths;
    }

    /**
     * Downloads specific data feed file by year.
     * @return path where data feed file was downloaded
     */
    public String downloadSpecific(int year) throws IOException {

        logger.debug("Downloading data feed file year: " + year);

        if (year < EARLIEST_FEED_YEAR && year > LATEST_FEED_YEAR) {
            throw new IllegalArgumentException("Year " + year + " is out of range. Please specify year between " + EARLIEST_FEED_YEAR + " and " + LATEST_FEED_YEAR);
        }

        StringBuffer urlSB = new StringBuffer();
        urlSB.append(DATAFEED_URL_ROOT);
        urlSB.append(year);
        urlSB.append(EXT_JSON_ZIP);

        String filePath = DOWNLOAD_PATH + File.separator + NVD_JSON_PREFIX + year + EXT_JSON_ZIP;

        logger.debug("Downloading year " + year + " data feed file from URL " + urlSB.toString() + " to file path " + filePath);

        // Baseline data feed.
        File file = new File(filePath);
        if (file.exists()) {
            logger.debug("Year file exists, deleting..." + filePath);
            file.delete();
        }

        try {
            FileUtils.copyURLToFile(new URL(urlSB.toString()), file);
        } catch (IOException e) {
            logger.error("Error downloading year " + year + " data feed file from URL " + urlSB.toString() + " to file path " + filePath);
            throw new IOException(e);
        }

        return filePath;
    }

    public String extractSpecific(String filePathYear) {

        logger.debug("Extracting data feed file: " + filePathYear);

        if (StringUtils.isBlank(filePathYear)) {
            throw new IllegalArgumentException("File path year of data feed was blank or null.");
        }

        byte[] buffer = new byte[1024];

        try (ZipInputStream zis = new ZipInputStream((new FileInputStream(filePathYear)))) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {

                String extractedFilePath = DATAFEED_DIRECTORY_NAME + File.separator + zipEntry.getName();

                // Zip file should only have one files contained.
                File f = new File(extractedFilePath);
                try (FileOutputStream fos = new FileOutputStream(f)) {
                    int length;
                    while ((length = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                    }
                    fos.close();
                    return f.getCanonicalPath();
                }
            }
        } catch (IOException e) {
            logger.error("Error extracting data feed file path " + filePathYear);
        }

        // Path to single file in zip file should have already been returned. Should not be here.
        // Change this if/when multiple files extracted, then return parent directory path.
        return "";
    }
}
