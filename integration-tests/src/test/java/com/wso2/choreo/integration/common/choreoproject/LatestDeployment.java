package com.wso2.choreo.integration.common.choreoproject;

/**
 * A class to represent the latest deployment of a release
 */
public class LatestDeployment {
    private String pending;
    private String deploymentHistoryId;
    private DeploymentHistory deploymentHistory;

    public String getPending() {
        return pending;
    }

    public void setPending(String pending) {
        this.pending = pending;
    }

    public String getDeploymentHistoryId() {
        return deploymentHistoryId;
    }

    public void setDeploymentHistoryId(String deploymentHistoryId) {
        this.deploymentHistoryId = deploymentHistoryId;
    }

    public DeploymentHistory getDeploymentHistory() {
        return deploymentHistory;
    }

    public void setDeploymentHistory(DeploymentHistory deploymentHistory) {
        this.deploymentHistory = deploymentHistory;
    }
}
