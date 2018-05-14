package xyz.vulquery;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Handles all environment configuration
 */
@Configuration
@ConfigurationProperties(prefix = "vq")
public class ConfigProperties {
    private String dbPath;
    private String dataFeedPath;

    @Override
    public String toString() {
        return "ConfigProperties [ dbpath=" + dbPath +  " , datafeedpath=" + dataFeedPath + " ]";
    }

    public void setDbPath(String dbPath) {
        this.dbPath = dbPath;
    }

    public String getDbPath() {
        return dbPath;
    }

    public void setDataFeedPath(String dataFeedPath) {
        this.dataFeedPath = dataFeedPath;
    }

    public String getDataFeedPath() {
        return dataFeedPath;
    }
}
