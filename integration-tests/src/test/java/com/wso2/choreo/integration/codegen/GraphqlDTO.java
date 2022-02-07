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

package com.wso2.choreo.integration.codegen;

public class GraphqlDTO {
    private String apiName;
    private int orgId;
    private String orgHandler;
    private String displayName;
    private String displayType;
    private String projectId;
    private String apiId;

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getApiName() {
        return apiName;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public int getOrgId() {
        return orgId;
    }

    public void setOrgHandler(String orgHandler) {
        this.orgHandler = orgHandler;
    }

    public String getOrgHandler() {
        return orgHandler;
    }

    public void setDisplayType(String displayType) {
        this.displayType = displayType;
    }

    public String getDisplayType() {
        return displayType;
    }

    public void setDiaplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public String getApiId() {
        return apiId;
    }
}
