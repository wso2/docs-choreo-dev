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

package com.wso2.choreo.integration.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GraphqlDTO {
    private String apiName;
    private int orgId;
    private String orgHandler;
    private String displayName;
    private String displayType;
    private String projectId;
    private String apiId;
    private String srcGitRepoUrl;
    private String repositorySubPath;
    private String repositoryType;
    private String repositoryBranch;
    private String componentId;
    private  String latestVersionId;
    private String componentHandler;
    private String orgUuid;
    private String componentType;
    private  String name;
    private String dockerfilePath;
    private  String environmentId;
    private  String sha;
    private String message;
    private String versionId;
    private String apiVersionId;
    private  String sourceReleaseId;
    private  String targetEnvironmentId;
    private  String devEnvIdToDeploy;
    private String branch;
    private String latestCommitSha;
    private String triggerChannels;
    private String triggerID;
    private  String description;
    private  String releaseId;
}
