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

import com.wso2.choreo.integration.apis.balregistry.BallerinaRegistry;
import com.wso2.choreo.integration.apis.marketplace.ConnectionService;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.marketplace.ConnectionInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Is responsible for cleaning up reoccurring data that is introduced by integration tests.
 */
public class DataCleaner  {
    private static final Logger log = LogManager.getLogger(DataCleaner.class);
    private static final int hourInMilliseconds = 60 * 60 * 1000;

    public static void removeOldTestData(ChoreoOrganization org) throws Exception {
        TokenHandler tokenHandler = TestContext.getTestUserTokenHandler();

        List<ChoreoProject> projects = org.getProjects(tokenHandler.getTestTokenForCPAPIs());

        log.info("Total number of projects: " + projects.size());

        int numberOfTestProjects = 0;
        int numberOfTestProjectsDeleted = 0;
        for (ChoreoProject project: projects) {
            String name = project.getName();

            if (name.startsWith(Constant.TEST_OLD_PROJECT_NAME_PREFIX) ||
                name.startsWith(Constant.TEST_PROJECT_NAME_PREFIX)) {

                ++numberOfTestProjects;
                if (shouldProjectBeDeleted(project.getName())) {
                    //get all connections visible to that project
                    ConnectionInfo[] connectionInfo= ConnectionService.getChoreoConnections(tokenHandler.getTestTokenForCPAPIs(),project.getId());
                    for (ConnectionInfo connection: connectionInfo){
                       ConnectionService.deleteChoreoConnection(tokenHandler.getTestTokenForCPAPIs(),connection.getGroupUuid());
                    }
                    List<ChoreoComponent> components = project.getComponents(tokenHandler.getTestTokenForCPAPIs());

                    for (ChoreoComponent component : components) {
                        project.deleteComponent(tokenHandler.getTestTokenForCPAPIs(), component.getId());
                    }

                    if (org.deleteProject(tokenHandler.getTestTokenForCPAPIs(), project.getId())) {
                        ++numberOfTestProjectsDeleted;
                    }
                }
            }
        }
        log.info("Total number of test projects: " + numberOfTestProjects);
        log.info("Total number of test projects deleted: " + numberOfTestProjectsDeleted);
    }
    private static boolean shouldProjectBeDeleted(String projectName) throws ParseException {
        // Projects that can be deleted that were created with the Old project name prefix have already been removed.
        // What remains are those that cannot be deleted due to connectors being published.
        if (projectName.startsWith(Constant.TEST_OLD_PROJECT_NAME_PREFIX)) {
            return false;
        }

        String[] parts = projectName.split("_");
        long createdDateTime = 0;
        if (parts.length >= 3) {
            String timestampPart = parts[1];
            createdDateTime = Long.parseLong(timestampPart);
        } else {
            createdDateTime = Long.parseLong(projectName.split(Constant.TEST_PROJECT_NAME_PREFIX)[1]);
        }

        String dateFormatStr = "Jan 01 2023 00:00:01.000 UTC";
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy HH:mm:ss.SSS zzz");
        long twenty23BeginDateTime = dateFormat.parse(dateFormatStr).getTime();

        long currentDateTime = new Date().getTime();

        // Delete projects created after beginning of 2023(some older project data can't be deleted)
        // and are 1 hour older than current time
        return createdDateTime > twenty23BeginDateTime && currentDateTime - createdDateTime > hourInMilliseconds;
    }

}
