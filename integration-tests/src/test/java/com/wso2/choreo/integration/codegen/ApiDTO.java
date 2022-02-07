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

public class ApiDTO {
    private String apiName;
    private String version;
    private String context;
    private String productionEndpoint;
    private String sandboxEndpoint;

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getApiName() {
        return apiName;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getContext() {
        return context;
    }

    public void setProductionEndpoint(String productionEndpoint) {
        this.productionEndpoint = productionEndpoint;
    }

    public String getProductionEndpoint() {
        return productionEndpoint;
    }

    public void setSandboxEndpoint(String sandboxEndpoint) {
        this.sandboxEndpoint = sandboxEndpoint;
    }

    public String getSandboxEndpoint() {
        return sandboxEndpoint;
    }
}
