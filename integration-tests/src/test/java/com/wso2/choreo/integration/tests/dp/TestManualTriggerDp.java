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
import com.wso2.choreo.integration.models.code.Repository;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TestManualTriggerDp extends TestBase {


    private String accessToken;
    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @DataProvider(name = "dps")
    public Object[][] provideData() {
        return this.setUp();
    }

    @BeforeClass
    public void setup_TestManualTriggerDp() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
    }


    @Test(dataProvider = "dps")
    @CitrusTest
    public void createUserManagedRestAPI_TestManualTriggerDp(DataProviderWrapper dp) throws Exception {
        ChoreoProject project = ComponentUtils.createProject(this, citrusClients, accessToken, dp.getRegion());
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));

        Repository repo = Repository.builder().repoUrl("https://github.com/choreo-test-apps/manual-trigger").branch("main").subPath("").build();
        GraphqlDTO dto = ComponentUtils.createManualTriggerComponentRequest(componentName, project, repo);
        ChoreoComponent choreoComponent = ComponentUtils.createComponent(this, citrusClients, accessToken, dto, ComponentFlavour.STANDARD);
        dp.setChoreoProject(project);
        dp.setChoreoComponent(choreoComponent);
        Assert.assertNotNull(choreoComponent.getId());

        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken, choreoComponent);
        dp.setEnvironments(environments);
    }

    @Test(dependsOnMethods = {"createUserManagedRestAPI_TestManualTriggerDp"}, dataProvider = "dps")
    @CitrusTest
    public void componentDeploy_TestManualTriggerDp(DataProviderWrapper dp) throws Exception {
        ComponentDeploymentStatusDTO statusDTO = ComponentUtils.deployComponent(this, citrusClients, accessToken,
                dp.getChoreoComponent(), dp.getEnvironments(), ComponentFlavour.STANDARD);
        dp.setDeploymentStatusDTO(statusDTO);
    }


    @Test(dependsOnMethods = {"componentDeploy_TestManualTriggerDp"}, dataProvider = "dps")
    @CitrusTest
    public void promote_TestManualTriggerDp(DataProviderWrapper dp) throws Exception {
        List<ComponentDeploymentStatusDTO> statusDTO = ComponentUtils.promoteComponent(this, citrusClients,
                accessToken, dp.getChoreoComponent(), dp.getEnvironments(), ComponentFlavour.STANDARD);
        dp.setPromoteStatusDTO(statusDTO);
    }
}
