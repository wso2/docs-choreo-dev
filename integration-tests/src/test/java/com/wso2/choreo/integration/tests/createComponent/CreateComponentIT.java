package com.wso2.choreo.integration.tests.createComponent;

import com.consol.citrus.annotations.CitrusTest;

import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.apis.Orgs;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.exceptions.ProjectCreationException;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.common.exceptions.UnexpectedResponseException;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;

import java.io.IOException;

import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.Response;
import com.wso2.choreo.integration.models.componentstatus.Status;
import com.wso2.choreo.integration.models.createcomponentresponse.ComponentCreationResponse;

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
  private String orgHandle;
  private String orgId;
  private String projectId;
  ComponentCreationResponse response;


  @BeforeClass
  public void beforeClassCCIT()
          throws IOException, InterruptedException, ProjectCreationException, TokenRetrievalException {
    accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
    orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
    orgId = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID);
    String orgUuid = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
    ChoreoOrganization org = new ChoreoOrganization(orgHandle, orgId, orgUuid);
    ChoreoProject project = org.createProject(accessToken);
    projectId = project.getId();
  }

  @Test
  @CitrusTest
  public void testCreateRESTComponentCCIT() throws IOException {
    String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
    GraphqlDTO dto = GraphqlDTO.builder().name(componentName).triggerID(null).projectId(projectId).displayType(Constant.displayType.restAPI.name()).build();
    response = GraphQL.createChoreoManagedComponent(dto, accessToken);
    Assert.assertEquals(response.getProjectId(), projectId);
    Assert.assertNotNull(response.getId());
  }

  @Test(dependsOnMethods = {"testCreateRESTComponentCCIT"})
  @CitrusTest
  public void testCreatedComponentStatusCCIT() throws  UnexpectedResponseException {
    Status status = Orgs.createdComponentStatus(projectId, response.getId(), accessToken);
    Assert.assertTrue(status.isSuccess());
  }

  @Test(dependsOnMethods = {"testCreatedComponentStatusCCIT"})
  @CitrusTest
  public void testDeleteRestApiComponentCCIT() throws IOException {
    Response response1 = GraphQL.deleteComponent(response.getId(), projectId, accessToken);
    ChoreoComponent[] components = GraphQL.getProjectComponents(projectId, accessToken);
    Assert.assertEquals(response1.getStatusCode(), HttpStatus.OK.value());
    Assert.assertEquals(components.length, 0);
  }
}
