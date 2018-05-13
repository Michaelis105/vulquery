package xyz.vulquery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import xyz.vulquery.util.StringUtils;

import javax.annotation.PostConstruct;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@SpringBootApplication
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    @Autowired
    private ConfigProperties prop;

    /**
     * Creates database if it does not exists.
     * @throws ClassNotFoundException internal error in dependencies
     * @throws SQLException connection error to database
     * @throws URISyntaxException parsing path error
     */
    @PostConstruct
    private void initializeDataStore() throws ClassNotFoundException, SQLException, URISyntaxException {
        Class.forName("org.sqlite.JDBC");
        StringBuffer urlSB = new StringBuffer();

        urlSB.append("jdbc:sqlite:");
        urlSB.append(StringUtils.isBlank(prop.getDbpath()) ?
                Application.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath() : prop.getDbpath());
        urlSB.append("vulquery.db");

        logger.debug(urlSB.toString());

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

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
