/*
 * Copyright (c) 2023, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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
package com.wso2.choreo.integration.models.endpoints;

import lombok.Data;

@Data
public class Endpoint {

    private String id;
    private String createdAt;
    private String updatedAt;
    private String releaseId;
    private int port;
    private String environmentId;
    private String displayName;
    private String invokeUrl;
    private String hostName;
    private String protocol;
    private String apiContext;
    private String apiDefinitionPath;
    private String visibility;
    private String type;
    private String apimId;
    private String apimRevisionId;
    private String apimName;
    private String projectUrl;
    private String organizationUrl;
    private String publicUrl;
    private String state;
    private boolean isDeleted;
    private String deletedAt;
}
