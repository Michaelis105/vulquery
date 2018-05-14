package xyz.vulquery.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import xyz.vulquery.dependency.Dependency;

import java.util.Date;
import java.util.List;

@Component("dependencyDAO")
public class DependencyDAOImpl implements DependencyDAO {

    private static final Logger logger = LoggerFactory.getLogger(DependencyDAOImpl.class);

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
        // Convert to Timestamp date.
        // Update table.
    }

    /**
     * Retrieves sync date in database.
     * @return sync date
     */
    public Date getSyncDate() {
        // TODO: Implement.
        return null;
    }

}
