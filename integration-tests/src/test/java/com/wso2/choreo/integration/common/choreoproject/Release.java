package com.wso2.choreo.integration.common.choreoproject;

/**
 * A class to represent a release of a Choreo application environment version
 */
public class Release {
    private String id;
    private Metadata metadata;
    private String environmentId;
    private String environment;
    private String gitHash;
    private String gitOpsHash;
    private LatestDeployment latestDeployment;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public String getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(String environmentId) {
        this.environmentId = environmentId;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getGitHash() {
        return gitHash;
    }

    public void setGitHash(String gitHash) {
        this.gitHash = gitHash;
    }

    public String getGitOpsHash() {
        return gitOpsHash;
    }

    public void setGitOpsHash(String gitOpsHash) {
        this.gitOpsHash = gitOpsHash;
    }

    public LatestDeployment getLatestDeployment() {
        return latestDeployment;
    }

    public void setLatestDeployment(LatestDeployment latestDeployment) {
        this.latestDeployment = latestDeployment;
    }
}
