package xyz.vulquery.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import xyz.vulquery.dependency.Dependency;
import xyz.vulquery.util.StringUtils;

import javax.sql.DataSource;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("dependencyDAO")
public class DependencyDAOImpl implements DependencyDAO {

    private final Logger logger = LoggerFactory.getLogger(DependencyDAOImpl.class);

    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String DATABASE_DRIVER_CLASS_NAME = "org.sqlite.JDBC";

    // TODO: Not final table
    private static final String CREATE_DEPENDENCY_TABLE_SQL = "CREATE TABLE IF NOT EXISTS DEPENDENCY " +
            "(GROUPID       TEXT    NOT NULL, " +
            " ARTIFACTID    TEXT    NOT NULL, " +
            " VERSION       TEXT    NOT NULL)";

    private static final String CREATE_LAST_SYNC_DATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS LASTSYNCDATE" +
            "(LASTSYNCDATE  TEXT    NOT NULL)";

    private static final String INIT_LAST_SYNC_DATE_TABLE_SQL = "INSERT INTO LASTSYNCDATE VALUES (?)";

    private static final String DELETE_LAST_SYNC_DATE_TABLE_SQL = "DELETE FROM LASTSYNCDATE";

    private static final String UPDATE_LAST_SYNC_DATE_SQL = "UPDATE LASTSYNCDATE SET LASTSYNCDATE = :lastsyncdate";

    private static final String LASTSYNCDATE_COL_NAME = "LASTSYNCDATE";

    private static final String GET_LAST_SYNC_DATE_SQL = "SELECT " + LASTSYNCDATE_COL_NAME + " FROM LASTSYNCDATE";

    private static final String DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";

    /**
     * Initializes database and sets data source.
     * @param url SQLite JDBC URL
     */
    @Override
    public void init(String url) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("Database URL is null or empty.");
        }

        DriverManagerDataSource driverManagerDataSourcedataSource = new DriverManagerDataSource();
        driverManagerDataSourcedataSource.setDriverClassName(DATABASE_DRIVER_CLASS_NAME);

        StringBuffer urlSB = new StringBuffer();
        urlSB.append("jdbc:sqlite:");
        urlSB.append(url);
        urlSB.append("/vulquery.db");

        driverManagerDataSourcedataSource.setUrl(urlSB.toString());

        dataSource = driverManagerDataSourcedataSource;
        jdbcTemplate = new JdbcTemplate(dataSource);
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

        jdbcTemplate.execute(CREATE_DEPENDENCY_TABLE_SQL);
        jdbcTemplate.execute(CREATE_LAST_SYNC_DATE_TABLE_SQL);

        jdbcTemplate.update(DELETE_LAST_SYNC_DATE_TABLE_SQL);
        jdbcTemplate.update(INIT_LAST_SYNC_DATE_TABLE_SQL, "placeholder");
    }


    /**
     * Adds dependency to database. If it already exists, then update.
     */
    @Override
    public void addDependency() {

    }

    /**
     * Retrives dependency information from database.
     * @return Dependency, null if dependency does not exist.
     */
    @Override
    public List<Dependency> getDependency(String groupId, String artifactId) {
        return null;
    }

    /**
     * Removes dependency from database. Ideal when force updating a specific dependency.
     * Avoid dangling delete.
     */
    @Override
    public void deleteDependency() {

    }

    /**
     * Cleanses database of all dependency data. Ideal when force updating all dependencies from empty data state.
     * Avoid dangling cleansing.
     */
    @Override
    public void removeAll() {

    }

    /**
     * Updates sync date to database.
     */
    public void updateSyncDate(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Sync date was null.");
        }

        Map<String, Object> syncParam = new HashMap<String, Object>(1);
        syncParam.put("lastsyncdate", new SimpleDateFormat(DATE_FORMAT).format(date));
        namedParameterJdbcTemplate.update(UPDATE_LAST_SYNC_DATE_SQL, syncParam);
    }

    /**
     * Retrieves sync date in database.
     * @return sync date
     */
    public String getSyncDate() {
        return jdbcTemplate.queryForObject(GET_LAST_SYNC_DATE_SQL, String.class);
    }

}
