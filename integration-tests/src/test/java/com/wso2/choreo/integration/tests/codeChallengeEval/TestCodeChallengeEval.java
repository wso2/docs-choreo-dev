package com.wso2.choreo.integration.tests.codeChallengeEval;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.apis.codeChallengeEval.CodeChallengeEval;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

public class TestCodeChallengeEval extends TestNGCitrusSpringSupport {

    private String accessToken;
    private String orgUuid;
    private String scoreResponse;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_TestCodeChallengeEvalTestCase() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        orgUuid = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        scoreResponse = new String(new ClassPathResource(
                "templates/codeChallengeEval/scoreResponse.json").getInputStream().readAllBytes());;
    }

    @Test
    @CitrusTest
    public void getScore_TestCodeChallengeEval() throws Exception {
        HttpClient codeChallengeEvalClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String resp  = CodeChallengeEval.getScoreSummary(this, codeChallengeEvalClient, accessToken, orgUuid);
        Assert.assertEquals(resp, scoreResponse);
    }
}
