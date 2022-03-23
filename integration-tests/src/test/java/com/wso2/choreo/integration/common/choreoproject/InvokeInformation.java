package com.wso2.choreo.integration.common.choreoproject;

/**
 * Invoke information of a component
 */
public class InvokeInformation {
    private String apiId;
//    private String apiRevision;
    private String environmentChoreoName;
    private String environmentId;
    private String environmentName;
    private String invokeUrl;
    private String isProxy;
    private String lifecycleStatus;
    private String releaseId;
    private String version;
    private String versionId;

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

//    public String getApiRevision() {
//        return apiRevision;
//    }

//    public void setApiRevision(String apiRevision) {
//        this.apiRevision = apiRevision;
//    }

    public String getEnvironmentChoreoName() {
        return environmentChoreoName;
    }

    public void setEnvironmentChoreoName(String environmentChoreoName) {
        this.environmentChoreoName = environmentChoreoName;
    }

    public String getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(String environmentId) {
        this.environmentId = environmentId;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }

    public String getInvokeUrl() {
        return invokeUrl;
    }

    public void setInvokeUrl(String invokeUrl) {
        this.invokeUrl = invokeUrl;
    }

    public String getIsProxy() {
        return isProxy;
    }

    public void setIsProxy(String isProxy) {
        this.isProxy = isProxy;
    }

    public String getLifecycleStatus() {
        return lifecycleStatus;
    }

    public void setLifecycleStatus(String lifecycleStatus) {
        this.lifecycleStatus = lifecycleStatus;
    }

    public String getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

}
