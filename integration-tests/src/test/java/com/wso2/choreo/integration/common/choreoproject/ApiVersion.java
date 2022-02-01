package com.wso2.choreo.integration.common.choreoproject;

/**
 * A class to represent a Choreo component API version
 */
public class ApiVersion {
    private String apiVersion;
    private String proxyName;
    private String proxyUrl;
    private String proxyId;
    private String id;
    private String state;
    private boolean latest;
    private AppEnvVersion[] appEnvVersions;

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getProxyName() {
        return proxyName;
    }

    public void setProxyName(String proxyName) {
        this.proxyName = proxyName;
    }

    public String getProxyUrl() {
        return proxyUrl;
    }

    public void setProxyUrl(String proxyUrl) {
        this.proxyUrl = proxyUrl;
    }

    public String getProxyId() {
        return proxyId;
    }

    public void setProxyId(String proxyId) {
        this.proxyId = proxyId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isLatest() {
        return latest;
    }

    public void setLatest(boolean latest) {
        this.latest = latest;
    }

    public AppEnvVersion[] getAppEnvVersions() {
        return appEnvVersions;
    }

    public void setAppEnvVersions(AppEnvVersion[] appEnvVersions) {
        this.appEnvVersions = appEnvVersions;
    }

}
