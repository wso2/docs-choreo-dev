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
    private String apiId;
    private String apiName;
    private String apiVersionId;
    private String branch;
    private String componentHandler;
    private String componentId;
    private String componentType;
    private String description;
    private String devEnvIdToDeploy;
    private String displayName;
    private String displayType;
    private String dockerContext;
    private String dockerfilePath;
    private String environmentId;
    private String latestCommitSha;
    private String latestVersionId;
    private String message;
    private String name;
    private String oasFilePath;
    private String orgHandler;
    private String orgUuid;
    private String projectId;
    private String region;
    private String releaseId;
    private String releaseIds;
    private String repositoryBranch;
    private String repositorySubPath;
    private String repositoryType;
    private String sha;
    private String shaDate;
    private String sourceReleaseId;
    private String srcGitRepoUrl;
    private String targetEnvironmentId;
    private String triggerChannels;
    private String triggerID;
    private String versionId;
    private String imageId;
    private boolean enableCellDiagram;
    private int orgId;
    private String version;
}
