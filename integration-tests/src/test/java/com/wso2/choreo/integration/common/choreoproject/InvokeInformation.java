/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you’ve
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */

package com.wso2.choreo.integration.common.choreoproject;

/**
 * Invoke information of a component
 */
public class InvokeInformation {
    private String apiId;
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
