package com.wso2.choreo.integration.tests.createComponent;

import com.consol.citrus.annotations.CitrusTest;

import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.apis.Orgs;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.exceptions.UnexpectedResponseException;
import com.wso2.choreo.integration.config.Constant;

import java.io.IOException;

import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.response.Response;
import com.wso2.choreo.integration.models.componentstatus.Status;

import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Date;

import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;

/**
 * $(http()
 * <p>
 * component creation related tests
 */
public class CreateComponentIT extends TestNGCitrusSpringSupport {

    private static String accessToken;
    private String projectId;
    ChoreoComponent choreoComponent;


    @BeforeClass
    public void setup_CreateComponentIT() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        ChoreoProject project = GraphQL.createProject(accessToken);
        projectId = project.getId();
    }

    @Test
    @CitrusTest
    public void createRESTComponent_CreateComponentIT() throws IOException {
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
        GraphqlDTO dto = GraphqlDTO.builder().name(componentName).triggerID(null).projectId(projectId).displayType(Constant.displayType.restAPI.name()).build();
        choreoComponent = GraphQL.createChoreoManagedComponent(dto, accessToken);
        Assert.assertEquals(choreoComponent.getProjectId(), projectId);
        Assert.assertNotNull(choreoComponent.getId());
    }

    @Test(dependsOnMethods = {"createRESTComponent_CreateComponentIT"})
    @CitrusTest
    public void createdComponentStatus_CreateComponentIT() throws UnexpectedResponseException {
        Status status = Orgs.createdComponentStatus(projectId, choreoComponent.getId(), accessToken);
        Assert.assertTrue(status.isSuccess());
    }

    @Test(dependsOnMethods = {"createdComponentStatus_CreateComponentIT"})
    @CitrusTest
    public void deleteRestApiComponent_CreateComponentIT() throws IOException {
        Response response1 = GraphQL.deleteComponent(choreoComponent.getId(), projectId, accessToken);
        ChoreoComponent[] components = GraphQL.getProjectComponents(projectId, accessToken);
        Assert.assertEquals(response1.getStatusCode(), HttpStatus.OK.value());
        Assert.assertEquals(components.length, 0);
    }
}
