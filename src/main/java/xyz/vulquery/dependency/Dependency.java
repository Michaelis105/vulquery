package xyz.vulquery.datafeed;

public class Dependency {

    private String groupId;
    private String artifactId;
    private String version;

    public Dependency() {}

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "Dependency [ groupId: " + groupId + ", artifactId: " + artifactId + ", version: " + version + " ]";
    }
}