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

package com.wso2.choreo.integration.common;

import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.config.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

public class DataCleaner  {
    private final static Logger log = LoggerFactory.getLogger(DataCleaner.class);
    private final static int hourInMilliseconds = 60 * 60 * 1000;

    public static void removeOldTestData(String accessToken, ChoreoOrganization org) throws Exception {
        List<ChoreoProject> projects = org.getProjects(accessToken);

        log.info("Total number of projects: " + projects.size());

        int numberOfTestProjects = 0;
        int numberOfTestProjectsDeleted = 0;
        for (ChoreoProject project: projects) {
            String name = project.getName();

            if (name.contains(Constant.TEST_OLD_PROJECT_NAME_PREFIX) ||
                name.contains(Constant.TEST_PROJECT_NAME_PREFIX)) {

                ++numberOfTestProjects;
                if (shouldProjectBeDeleted(project.getName())) {
                    List<ChoreoComponent> components = project.getComponents(accessToken);

                    for (ChoreoComponent component : components) {
                        project.deleteComponent(accessToken, component.getId());
                    }

                    if (org.deleteProject(accessToken, project.getId())) {
                        ++numberOfTestProjectsDeleted;
                    }
                }
            }
        }

        log.info("Total number of test projects: " + numberOfTestProjects);
        log.info("Total number of test projects deleted: " + numberOfTestProjectsDeleted);
    }

    private static boolean shouldProjectBeDeleted(String projectName) {
        // Projects that can be deleted that were created with the Old project name prefix have already been removed.
        // What remains are those that cannot be deleted due to connectors being published.
        if (projectName.contains(Constant.TEST_OLD_PROJECT_NAME_PREFIX)) {
            return false;
        }

        long createdDateTime = Long.parseLong(projectName.split(Constant.TEST_PROJECT_NAME_PREFIX)[1]);

        long currentDatTime = new Date().getTime();

        return currentDatTime - createdDateTime > hourInMilliseconds;
    }

}
