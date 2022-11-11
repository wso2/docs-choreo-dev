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
import com.wso2.choreo.integration.apis.graphql.ComponentCreationGQL;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.exceptions.APIKeyGenerationCheckException;
import com.wso2.choreo.integration.common.exceptions.ApiKeyNotFoundException;
import com.wso2.choreo.integration.common.exceptions.InvokeAPICheckException;
import com.wso2.choreo.integration.common.exceptions.InvokeInformationNotFoundException;
import com.wso2.choreo.integration.common.exceptions.NoLatestApiVersionFoundException;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.invokeinfor.InvokeInformation;
import com.wso2.choreo.integration.models.testconfigs.TestConfigs;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;


public class ComponentUtils {

    static Logger l = Logger.getLogger(ComponentUtils.class.getName());

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
            restAPI.setProjectId(project.getId());
        } else {
            restAPI = component.get();
        }

        restAPI.setOrganization(org);

        return restAPI;
    }

    public static ChoreoComponent createRestAPI(String accessToken) throws Exception {
        ChoreoOrganization org = TestContext.getTestOrg();

        ChoreoProject project = org.createProject(accessToken);
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
        ChoreoComponent restAPI = project.createRestAPI(accessToken, componentName, org);

        restAPI.setOrganization(org);
        restAPI.setProjectId(project.getId());

        return restAPI;
    }

    public static void invokeApiEndpoint(String accessToken, ChoreoComponent component, Constant.Environment env) throws Exception {
        InvokeInformation invokeInformation = component.getInvokeInformation(accessToken,
                Constant.displayType.restAPI.name(), env.name());
        String requestURI = invokeInformation.getInvokeUrl();
        if (requestURI == null) {
            throw new InvokeInformationNotFoundException();
        }
        requestURI = requestURI.concat("/")
                .concat("greeting")
                .concat("?name=testUser");
        // Escaping the quotations
        String apiKey = component.getAPIKeyForInvoke(accessToken, invokeInformation.getApiId()).replace("\"", "");
        HttpGet request = new HttpGet(requestURI);
        request.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
        request.setHeader(HttpHeaders.CONTENT_TYPE, Constant.APPLICATION_JSON);
        request.setHeader("API-Key", apiKey);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
             CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());

            if (statusCode != HttpStatus.OK.value()) {
                throw new InvokeAPICheckException(statusCode, responseBody);
            }
        }

    }

    public static TestConfigs invokeEndpoint(ChoreoComponent component, String componentType, String accessToken) throws NoLatestApiVersionFoundException, IOException, InvokeInformationNotFoundException, ApiKeyNotFoundException, APIKeyGenerationCheckException {
        TestConfigs wrapper = new TestConfigs();
        InvokeInformation[] info = ComponentCreationGQL.getInvokeInformation(component, componentType, accessToken);
        String apiKey = component.getAPIKeyForInvoke(accessToken, component.getApiId()).replace("\"", "");
        for (InvokeInformation in : info) {
           wrapper.addConfig(in.getEnvironmentName(), TestConfigs.builder().invokeUrl(in.getInvokeUrl()).apiKey(apiKey).build());
        }
        return wrapper;
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
