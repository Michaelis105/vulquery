package xyz.vulquery;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "vq")
public class ConfigProperties {
    private String dbpath;
    private String datafeedpath;

    @Override
    public String toString() {
        return "ConfigProperties [ dbpath=" + dbpath +  " , datafeedpath=" + datafeedpath + " ]";
    }

    public void setDbpath(String dbpath) {
        this.dbpath = dbpath;
    }

    public String getDbpath() {
        return dbpath;
    }

    public void setDatafeedpath(String datafeedpath) {
        this.datafeedpath = datafeedpath;
    }

    public String getDatafeedpath() {
        return datafeedpath;
    }
}
