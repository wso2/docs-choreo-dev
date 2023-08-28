package com.wso2.choreo.integration.tests.dp;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.apimanager.KeyData;
import com.wso2.choreo.integration.models.code.Repository;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import com.wso2.choreo.integration.tests.byoc.TestHelper;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TestBYOCDp extends TestBase {


    private static final String DOCKER_FILE_PATH = "byoc-test/Dockerfile";
    private String accessToken;
    private final List<DataProviderWrapper> dps = new ArrayList<>();
    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_TestBYOCEUDp() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
    }

    @DataProvider(name = "dps")
    public Object[][] provideData() {
        return this.setUp();
    }

    @Test(dataProvider = "dps")
    @CitrusTest
    public void createComponent_TestBYOCDp(DataProviderWrapper dp) throws Exception {
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
        ChoreoProject project = ComponentUtils.createProject(this, citrusClients, accessToken, dp.getRegion());

        Repository repo = Repository.builder().
                repoUrl("https://github.com/choreo-test-apps/byor-greetings-app2").
                oasFilePath("byoc-test/oas.yaml").
                dockerContext("byoc-test").
                dockerfilePath(DOCKER_FILE_PATH).build();

        GraphqlDTO dto = ComponentUtils.createByocComponentRequest(componentName, project, repo);

        ChoreoComponent choreoComponent = ComponentUtils.createComponent(this, citrusClients, accessToken,
                dto, ComponentFlavour.BYOC);
        dp.setChoreoProject(project);
        dp.setChoreoComponent(choreoComponent);
        Assert.assertNotNull(choreoComponent.getId());

        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients,
                accessToken, choreoComponent);
        dp.setEnvironments(environments);
    }

    @Test(dependsOnMethods = {"createComponent_TestBYOCDp"}, dataProvider = "dps")
    @CitrusTest
    public void deployComponent_TestBYOCDp(DataProviderWrapper dp) throws Exception {
        ComponentDeploymentStatusDTO statusDTO = ComponentUtils.deployComponent(this, citrusClients,
                accessToken, dp.getChoreoComponent(), dp.getEnvironments(), ComponentFlavour.BYOC);
        dp.setDeploymentStatusDTO(statusDTO);
    }

    @Test(dependsOnMethods = {"deployComponent_TestBYOCDp"}, dataProvider = "dps")
    @CitrusTest
    public void promoteComponent_TestBYOCDp(DataProviderWrapper dp) throws Exception {
        List<ComponentDeploymentStatusDTO> statusDTO = ComponentUtils.promoteComponent(this, citrusClients,
                accessToken, dp.getChoreoComponent(), dp.getEnvironments(), ComponentFlavour.BYOC);
        dp.setPromoteStatusDTO(statusDTO);
    }

    @Test(dependsOnMethods = {"promoteComponent_TestBYOCDp"}, dataProvider = "dps")
    @CitrusTest
    public void invokeAPIDev_TestBYOCDp(DataProviderWrapper dp) throws Exception {
        Pair<String, KeyData> invokeData = ComponentUtils.getInvokeInfo(this, citrusClients, accessToken,
                dp.getChoreoComponent(), dp.getDeploymentStatusDTO(), dp.getEnvironments());
        String expectedResponse = TestHelper.getExpectedResponse();
        ComponentUtils.invokeApiGET(this, invokeData.getRight().getApikey(), invokeData.getLeft(), "/movies",
                expectedResponse);
    }

    @Test(dependsOnMethods = {"invokeAPIDev_TestBYOCDp"}, dataProvider = "dps")
    @CitrusTest
    public void invokeAPIProd_TestBYOCDp(DataProviderWrapper dp) throws Exception {
        String expectedResponse = TestHelper.getExpectedResponse();
        for (ComponentDeploymentStatusDTO statusDTO :dp.getPromoteStatusDTO()) {
            Pair<String, KeyData> invokeData = ComponentUtils.getInvokeInfo(this, citrusClients, accessToken,
                    dp.getChoreoComponent(), statusDTO, dp.getEnvironments());
            ComponentUtils.invokeApiGET(this, invokeData.getRight().getApikey(), invokeData.getLeft(), "/movies",
                    expectedResponse);
        }
    }

}
