/*
 *
 *  Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *  This software is the property of WSO2 Inc. and its suppliers, if any.
 *  Dissemination of any information or reproduction of any material contained
 *  herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 *  You may not alter or remove any copyright or other notice from copies of this content.
 *
 */

package com.wso2.choreo.integration.tests.insights;

/**
 * A class to represent an environment derived from insights.
 */
public class Environment {

    private String id;
    private String externalEnvId;
    private String internalEnvId;
    private String sandboxEnvId;
    private String name;
    private String type;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getExternalEnvId() {
        return externalEnvId;
    }

    public String getInternalEnvId() {
        return internalEnvId;
    }

    public String getSandboxEnvId() {
        return sandboxEnvId;
    }
}
