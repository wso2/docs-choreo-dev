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

import java.util.Objects;

/**
 * A class to represent a Choreo component application version of an API version
 */
public class AppEnvVersion {
    private String environmentId;
    private String releaseId;
    private Release release;
    private String environment;

    public boolean isDev() {
        String choreoEnv = getRelease().getMetadata().getChoreoEnv();
        return Objects.equals(choreoEnv, "dev");
    }

    public String getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(String environmentId) {
        this.environmentId = environmentId;
    }

    public String getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

    public Release getRelease() {
        return release;
    }

    public void setRelease(Release release) {
        this.release = release;
    }

    public String getEnvironment() {
        return getRelease().getMetadata().getChoreoEnv();
    }

    public void setEnvironment(String environment) {
        this.environment = getRelease().getMetadata().getChoreoEnv();
    }
}
