package com.wso2.choreo.integration.common.choreoproject;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to represent the deployment history of the latest deployment
 */
public class DeploymentHistory {
    private String id;
    private String appEnvironment;
    private String appEnvironmentId;
    private String changeMessage;
    private List<ContainerImage> containerImages = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppEnvironment() {
        return appEnvironment;
    }

    public void setAppEnvironment(String appEnvironment) {
        this.appEnvironment = appEnvironment;
    }

    public String getAppEnvironmentId() {
        return appEnvironmentId;
    }

    public void setAppEnvironmentId(String appEnvironmentId) {
        this.appEnvironmentId = appEnvironmentId;
    }

    public String getChangeMessage() {
        return changeMessage;
    }

    public void setChangeMessage(String changeMessage) {
        this.changeMessage = changeMessage;
    }

    public List<ContainerImage> getContainerImages() {
        return containerImages;
    }

    public void setContainerImages(List<ContainerImage> containerImages) {
        this.containerImages = containerImages;
    }
}
