package xyz.vulquery.dependency;

public class Dependency {

    private String groupId;
    private String artifactId;
    private String version;

    private double averageBaseScore;
    private int vulnerabilityCount;

    public Dependency() {
        averageBaseScore = 0.0;
        vulnerabilityCount = 0;
    }

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

    // Use addBaseScore() instead.
    public void setAverageBaseScore(double averageBaseScore) { this.averageBaseScore = averageBaseScore; }

    public double getAverageBaseScore() { return averageBaseScore; }

    public void addBaseScore(double baseScore) {
        if (baseScore < 0) {
            throw new IllegalArgumentException("Cannot add negative additive base score");
        }
        this.averageBaseScore = (averageBaseScore + baseScore) / (++vulnerabilityCount);
    }

    public String getFullName() {
        StringBuffer sb = new StringBuffer();
        sb.append(groupId);
        sb.append(":");
        sb.append(artifactId);
        sb.append(":");
        sb.append(version);
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Dependency [ groupId: " + groupId + ", artifactId: " + artifactId + ", version: " + version + " ]";
    }
}
