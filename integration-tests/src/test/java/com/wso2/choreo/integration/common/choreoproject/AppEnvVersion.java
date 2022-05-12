package com.wso2.choreo.integration.common.choreoproject;

import java.util.Objects;

/**
 * A class to represent a Choreo component application version of an API version
 */
public class AppEnvVersion {
    private String environmentId;
    private String releaseId;
    private Release release;
    private String environment;

    public boolean isDev() {
        String choreoEnv = getRelease().getMetadata().getChoreoEnv();
        return Objects.equals(choreoEnv, "dev");
    }

    public String getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(String environmentId) {
        this.environmentId = environmentId;
    }

    public String getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

    public Release getRelease() {
        return release;
    }

    public void setRelease(Release release) {
        this.release = release;
    }

    public String getEnvironment() {
        return getRelease().getMetadata().getChoreoEnv();
    }

    public void setEnvironment(String environment) {
        this.environment = getRelease().getMetadata().getChoreoEnv();
    }
}
