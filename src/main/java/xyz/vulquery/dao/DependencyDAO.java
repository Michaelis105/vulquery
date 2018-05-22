package xyz.vulquery.dao;

import xyz.vulquery.dependency.Dependency;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public interface DependencyDAO {

    /**
     * Adds dependency to data storage. If it already exists, then update.
     */
    void addDependency(Dependency dependency);

    /**
     * Retrieves dependency information from data storage.
     * @return Dependency, null if dependency does not exist.
     */
    List<Dependency> getDependency(String groupId, String artifactId);

    /**
     * Removes dependency from data storage. Ideal when force updating a specific dependency.
     * Avoid dangling delete.
     */
    void deleteDependency();

    /**
     * Cleanses data storage of all dependency data. Ideal when force updating all dependencies from empty data state.
     * Avoid dangling cleansing.
     */
    void removeAll();

    /**
     * Updates sync date to data storage.
     */
    void updateSyncDate(Date date);

    /**
     * Retrives last sync date to data storage.
     * @return sync date
     */
    String getSyncDate();

    /**
     * Start up initialization of data storage.
     */
    void init(String url); // TODO: Should DAO handle db initialization?
}
