package com.wso2.choreo.integration.common.choreoproject;

/**
 * A class to represent a Choreo component repository
 */
public class ComponentRepository {
    private String nameApp;
    private String nameConfig;
    private String organizationApp;
    private String organizationConfig;
    private String branch;
    private String branchApp;

    public String getNameApp() {
        return nameApp;
    }

    public void setNameApp(String nameApp) {
        this.nameApp = nameApp;
    }

    public String getNameConfig() {
        return nameConfig;
    }

    public void setNameConfig(String nameConfig) {
        this.nameConfig = nameConfig;
    }

    public String getOrganizationApp() {
        return organizationApp;
    }

    public void setOrganizationApp(String organizationApp) {
        this.organizationApp = organizationApp;
    }

    public String getBranch() {
        return branch;
    }
    public String getBranchApp() {
        return branchApp;
    }
    public void setBranchApp(String branchApp) {
        this.branchApp = branchApp;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getOrganizationConfig() {
        return organizationConfig;
    }

    public void setOrganizationConfig(String organizationConfig) {
        this.organizationConfig = organizationConfig;
    }
}
