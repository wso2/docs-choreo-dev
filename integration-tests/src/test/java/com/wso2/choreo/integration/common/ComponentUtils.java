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


import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Optional;


public class ComponentUtils {

    public static ChoreoComponent getReusableComponent(String accessToken, String testName) throws Exception {
        ChoreoOrganization org = TestContext.getTestOrg();
        String projectName = "integration-test-project";

        Optional<ChoreoProject> existingProject = org.getProjectByName(accessToken, projectName);
        ChoreoProject project;
        if (existingProject.isEmpty()) {
            project = org.createProject(accessToken, projectName, projectName);
        } else {
            project = existingProject.get();
        }

        String componentName = testName + "component";
        Optional<ChoreoComponent> component = project.getComponentByName(accessToken, componentName);
        ChoreoComponent restAPI;

        if (component.isEmpty()) {
            restAPI = project.createRestAPI(accessToken, componentName, org);
        } else {
            restAPI = component.get();
        }

        restAPI.setOrganization(org);

        return restAPI;
    }

    public static String generateStringFromTemplate(String templateRelativePath, Map<String, String> params)
            throws IOException {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(templateRelativePath);
        Writer writer = new StringWriter();
        mustache.execute(writer, params).flush();
        return writer.toString();
    }

    public static String generateStringPayloadFromTemplate(String templateRelativePath, Map<String, Object> params)
            throws IOException {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(templateRelativePath);
        Writer writer = new StringWriter();
        mustache.execute(writer, params).flush();
        return writer.toString();
    }
}
