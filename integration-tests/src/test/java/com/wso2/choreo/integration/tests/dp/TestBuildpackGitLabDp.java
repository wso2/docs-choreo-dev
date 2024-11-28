package com.wso2.choreo.integration.tests.dp;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.wso2.choreo.integration.apis.apimanager.ApiManager;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.utils.NameGenerator;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.code.Repository;
import com.wso2.choreo.integration.models.environments.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestBuildpackGitLabDp extends TestBase {

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
    public void createComponent_TestBuildpackGitLabDp(DataProviderWrapper dp) throws Exception {
        String componentName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_COMPONENT_NAME);
        ChoreoProject project = ComponentUtils.createProject(this, citrusClients, accessToken, dp.getRegion());
        String secretRef = Configuration.getConfig(ConfigDefinition.GITLAB_SECRETREF);
        Repository repo = Repository.builder().
                repoUrl("https://gitlab.preview-dv.choreo.dev/Administrator/choreo-samples-new").
                buildContext("greeting-service-go").build();

        GraphqlDTO dto = ComponentUtils.createBuildpackComponentRequestWithSecretRef(componentName, project, repo, secretRef, Buildpack.GOLANG);

        ChoreoComponent choreoComponent = ComponentUtils.createComponent(this, citrusClients, accessToken,
                dto, ComponentFlavour.BUILDPACK);
        dp.setChoreoProject(project);
        dp.setChoreoComponent(choreoComponent);
        Assert.assertNotNull(choreoComponent.getId());

        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients,
                accessToken, choreoComponent);
        dp.setEnvironments(environments);
    }
}
