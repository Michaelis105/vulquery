package xyz.vulquery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import xyz.vulquery.util.StringUtils;

@SpringBootApplication
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    @Autowired
    private ConfigProperties prop;

    /**
     * Continue initialization after SpringBoot has started.
     */
    @PostConstruct
    private void init() throws Exception {
        String defaultPath = Application.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        initDataStore(StringUtils.isBlank(prop.getDbpath()) ? defaultPath : prop.getDbpath());
        initDataFeedDownloadDirectory(StringUtils.isBlank(prop.getDataFeedPath()) ? defaultPath : prop.getDbpath());
    }

    /**
     * Creates database if it does not exists.
     * @param url Absolute file path where SQLITE database file will be stored
     * @throws ClassNotFoundException internal error in dependencies
     * @throws SQLException connection error to database
     */
    private void initDataStore(String url) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        StringBuffer urlSB = new StringBuffer();

        urlSB.append("jdbc:sqlite:");
        urlSB.append(url);
        urlSB.append("vulquery.db");

        logger.debug("SQLITE URL: " + urlSB.toString());

        // TODO: Not final table
        String sql = "CREATE TABLE DEPENDENCY " +
                "(GROUPID       TEXT    NOT NULL, " +
                " ARTIFACTID    TEXT    NOT NULL, " +
                " VERSION       TEXT    NOT NULL)";

        try (Connection connection = DriverManager.getConnection(urlSB.toString());
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(sql);
        }

    }

    /**
     * Creates data feed download directory.
     * @param path Absolute file path where download directory will be stored
     */
    private void initDataFeedDownloadDirectory(String path) throws IOException {
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
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
