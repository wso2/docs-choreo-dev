/*
 * Copyright (c) 2025, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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
package com.wso2.choreo.integration.tests.byoi;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.apis.devops.DevopsPortalApi;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ApiVersion;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.utils.NameGenerator;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.config.OptionalConfigDefinition;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.devopsportalapi.Image;
import com.wso2.choreo.integration.models.devopsportalapi.ThirdPartyContainerRegistryDTO;
import com.wso2.choreo.integration.models.endpoints.ByoiEndpoint;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import com.wso2.choreo.integration.models.graphql.CreateByoiComponentResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TestBYOIExternalCI extends TestNGCitrusSpringSupport {
   
    private String orgHandle;
    private String orgId;
    private String orgUUID;
    private String projectId;
    private String containerRegistryId;
    private String componentHandler;
    private String componentId;
    private static String accessToken;
    private ChoreoComponent testComponent;
    private ApiVersion apiVersion;
    private boolean isPDP;
    private Image image;
    private String releaseId;
    private ComponentDeploymentStatusDTO componentDeployment;
    private String token;
    private String imageUrl;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @Autowired
    private HttpClient choreoProjectsTestClient;

    @BeforeClass
    public void setup_TestCreateBYOIComponent()
            throws Exception {

        orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        orgId = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID);
        orgUUID = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        Optional<String> isPDPEnabled = Configuration.getOptionalConfig(OptionalConfigDefinition.IS_PDP_ENABLED);
        if (isPDPEnabled.isPresent()) {
            isPDP = Boolean.parseBoolean(isPDPEnabled.get());
        }
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
    }

    @Test
    @CitrusTest
    public void getRegistries_TestCreateBYOIComponent() throws Exception {
        List<ThirdPartyContainerRegistryDTO> containerRegistries = DevopsPortalApi.getThirdPartyRegistryCredentials(this, accessToken, orgUUID);
        if (containerRegistries.size() > 0) {
            if (isPDP) {
                containerRegistryId = containerRegistries.stream()
                    .filter(registry -> "Choreo Samples Registry".equals(registry.getName()))
                    .findFirst()
                    .orElseThrow(() -> new Exception("Container registry with name 'Choreo Samples Registry' not found"))
                    .getId();
            } else {
                throw new Exception("This test if only supported for PDP");
            }
        } else {
            throw new Exception("No container registries found");
        }
    }

    @Test(dependsOnMethods = {"getRegistries_TestCreateBYOIComponent"})
    @CitrusTest
    public void createProject_TestCreateBYOIComponent() throws Exception {
        ChoreoProject project = ComponentUtils.createProject(this, citrusClients, accessToken, 
            Constant.region.US.toString());
        projectId = project.getId();
    }

    @Test(dependsOnMethods = {"createProject_TestCreateBYOIComponent"})
    @CitrusTest
    public void createComponent_TestCreateBYOIComponent() throws Exception {
        String componentName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_COMPONENT_NAME);
        final String imageUrl = "choreoanonymouspullable.azurecr.io/react-spa:v0.9";
        GraphqlDTO graphqlDTO = ComponentUtils.createBYOIComponentRequest(componentName, projectId, imageUrl, containerRegistryId, Constant.displayType.byoiWebApp.name());

        Optional<CreateByoiComponentResponseDTO> byoiComponent = GraphQL.createBYOIComponent(this,
                choreoProjectsTestClient, graphqlDTO, accessToken);
        componentHandler = byoiComponent.get().getHandle();
        componentId = byoiComponent.get().getId();
    }

    @Test(dependsOnMethods = { "createComponent_TestCreateBYOIComponent" })
    @CitrusTest
    public void componentRetrieval_TestCreateBYOIComponent() throws Exception {
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().projectId(projectId).componentHandler(componentHandler).build();
        testComponent = GraphQL.retrieveComponent(this, choreoProjectsTestClient, accessToken,
                graphqlDTO);
    }

    @Test(dependsOnMethods = {"componentRetrieval_TestCreateBYOIComponent"})
    @CitrusTest
    public void getImages_TestCreateBYOIComponent() throws Exception {
        List<Image> images = DevopsPortalApi.getImages(this, accessToken, orgUUID, projectId, componentId, apiVersion.getId());
        image = images.get(images.size() - 1);
    }

    @Test(dependsOnMethods = {"getImages_TestCreateBYOIComponent"})
    @CitrusTest
    public void deployImage_TestCreateBYOIComponent() throws Exception {
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(componentId).releaseId(releaseId).imageUrl(image.getImageNameWithTag()).build();
        GraphQL.deployImage(this, choreoProjectsTestClient, graphqlDTO, accessToken);
    }

    @Test(dependsOnMethods = {"deployImage_TestCreateBYOIComponent"})
    @CitrusTest
    public void checkDeploymentStatus_TestCreateBYOIComponent() throws Exception {
        componentDeployment = GraphQL.componentDeployment(testComponent, "dev", accessToken);
        Assert.assertEquals(componentDeployment.getDeploymentStatusV2(), "ACTIVE");
    }

    @Test(dependsOnMethods = {"checkDeploymentStatus_TestCreateBYOIComponent"})
    @CitrusTest
    public void checkEndpointStatus_TestCreateBYOIComponent() throws Exception {
        ComponentUtils.validateEndpoints(this, citrusClients, accessToken, testComponent, componentDeployment);
    }

    @Test(dependsOnMethods = {"checkEndpointStatus_TestCreateBYOIComponent"})
    @CitrusTest
    public void generateBYOECIToken_TestCreateBYOIComponent() throws Exception {
        token = DevopsPortalApi.generateBYOECIToken(this, accessToken, orgUUID, projectId, componentId, "test-token");
        Assert.assertNotNull(token);
    }

    @Test(dependsOnMethods = {"generateBYOECIToken_TestCreateBYOIComponent"})
    @CitrusTest
    public void triggerExternalCI_TestCreateBYOIComponent() throws Exception {
        imageUrl = "choreoanonymouspullable.azurecr.io/react-spa:latest";
        DevopsPortalApi.triggerExternalCI(this, token, orgUUID, projectId, componentId, apiVersion.getId(), imageUrl);
    }

    @Test(dependsOnMethods = {"triggerExternalCI_TestCreateBYOIComponent"})
    @CitrusTest
    public void checkDeployedImage_TestCreateBYOIComponent() throws Exception {
        componentDeployment = GraphQL.componentDeployment(testComponent, "dev", accessToken);
        Assert.assertEquals(componentDeployment.getImageUrl(), imageUrl);
    }

    @Test(dependsOnMethods = {"checkDeployedImage_TestCreateBYOIComponent"})
    @CitrusTest
    public void deleteComponent_TestCreateBYOIComponent() throws Exception {
        GraphQL.deleteComponent(componentId, projectId, accessToken);
    }    
}
