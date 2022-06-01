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

package com.wso2.choreo.integration.tests.anomalyDetector;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.TokenHandler;
import com.wso2.choreo.integration.common.choreoproject.RestApiChoreoComponent;
import com.wso2.choreo.integration.common.email.EmailUtils;
import com.wso2.choreo.integration.common.exceptions.ComponentDeploymentStatusCheckException;
import com.wso2.choreo.integration.common.exceptions.ComponentDeploymentTimeoutException;
import com.wso2.choreo.integration.common.exceptions.GetApiTestTokenStatusCheckException;
import com.wso2.choreo.integration.common.exceptions.GetDeploymentsStatusCheckException;
import com.wso2.choreo.integration.common.exceptions.RedeployException;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import java.io.IOException;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;


/**
 * Contains a test to check if the multivariate anomaly detector detects a backend failure anomaly
 */
public class BackendFailureAnomaly extends TestNGCitrusSpringSupport {

  private TokenHandler invokeAccessTokenHandler;
  private String orgHandler;
  private String passthroughComponentId;
  private String passthroughReleaseId;
  private String projectsAPIAccessToken;
  private String projectId;
  private RestApiChoreoComponent restApiComponent;
  private long testStartTimestamp;

  private final static Logger log = LoggerFactory.getLogger(BackendFailureAnomaly.class);

  @BeforeClass
  public void beforeClass() throws InterruptedException, IOException, TokenRetrievalException, RedeployException, ComponentDeploymentStatusCheckException, ComponentDeploymentTimeoutException, GetDeploymentsStatusCheckException {
      String orgUuid = Configuration.ANOMALY_DETECTION.TEST_CHOREO_ORG_UUID;
      String passthorughVersionId = Configuration.ANOMALY_DETECTION.PASSTHROUGH_VERSION_ID;

      projectsAPIAccessToken = TestContext.getAnomalyDetectionUserTokenHandler().getTestTokenForCPAPIs();
      ChoreoOrganization org = new ChoreoOrganization(Configuration.ANOMALY_DETECTION.TEST_CHOREO_ORG_HANDLE,
                                                      String.valueOf(Configuration.ANOMALY_DETECTION.TEST_CHOREO_ORG_ID),
                                                      orgUuid);
      orgHandler = org.getOrgHandle();
      passthroughComponentId = Configuration.ANOMALY_DETECTION.PASSTHROUGH_COMPONENT_ID;
      passthroughReleaseId = Configuration.ANOMALY_DETECTION.PASSTHROUGH_RELEASE_ID;
      projectId = Configuration.ANOMALY_DETECTION.PROJECT_ID;
      restApiComponent = new RestApiChoreoComponent();
      restApiComponent.setProjectId(projectId);
      restApiComponent.setOrgHandler(orgHandler);
      restApiComponent.setId(passthroughComponentId);

      // Deployed components may get stopped automatically by Choreo. Therefore check if it's stopped (SUSPENDED) and redeploy if so
      JsonArray deployments = restApiComponent.getDeployments(projectsAPIAccessToken, orgHandler, orgUuid, passthorughVersionId);
      JsonElement passthroughDeployment = new JsonParser().parse("{}");
      for (JsonElement jsonElement : deployments) {
        if (jsonElement.getAsJsonObject().getAsJsonPrimitive("releaseId").getAsString().equals(passthroughReleaseId)) {
          passthroughDeployment = jsonElement;
          if (passthroughDeployment.getAsJsonObject().getAsJsonPrimitive("deploymentStatus").getAsString().equals("SUSPENDED")){
            log.info("Deployment is currently stopped. Redeploying now...");
            restApiComponent.redeploy(projectsAPIAccessToken, passthroughComponentId, passthroughReleaseId, Configuration.ANOMALY_DETECTION.TEST_CHOREO_ORG_HANDLE);
          }
          while (!passthroughDeployment.getAsJsonObject().getAsJsonPrimitive("deploymentStatus").getAsString().equals("ACTIVE")){
            log.info("Waiting for redeployed component to become ready...");
            Thread.sleep(10000);
            deployments = restApiComponent.getDeployments(projectsAPIAccessToken, orgHandler, orgUuid, passthorughVersionId);
            for (JsonElement deployment : deployments) {
              if (deployment.getAsJsonObject().getAsJsonPrimitive("releaseId").getAsString().equals(passthroughReleaseId)) {
                passthroughDeployment = deployment;
              }
            }
          }
        }
      }
      testStartTimestamp = Instant.now().toEpochMilli();
  }

  /**
   * 
   * Log into the email account and check if the alert for the anomaly injected through injectAnomaly() above 
   * was received.
   *  
   * @throws Exception
   * @throws GetApiTestTokenStatusCheckException
   */
  @Test
  @CitrusTest
  public void testEmailAlert() throws Exception, GetApiTestTokenStatusCheckException {
      InvokeApi.invokePassthroughComponentConcurrently(Configuration.ANOMALY_DETECTION.PASSTHROUGH_CLIENT_ID,
                                                       Configuration.ANOMALY_DETECTION.PASSTHROUGH_CLIENT_SECRET, 
                                                       Configuration.ANOMALY_DETECTION.PASSTHROUGH_INVOKE_URL);
      log.info("Waiting for 5 minutes to allow the anomaly to be detected...");
      Thread.sleep(300000);
      String searchString = "[Choreo ALERT] Anomaly detected in " + Configuration.ANOMALY_DETECTION.PASSTHROUGH_COMPONENT_NAME;
      log.info("Starting to check if the email alert was received...");
      boolean isMailReceived = EmailUtils.checkForMail(Constant.ANOMALY_DETECTION.MAIL_IMAP_HOST, 
                                                       Configuration.ANOMALY_DETECTION.MAIL_IMAP_PASS, 
                                                       Constant.ANOMALY_DETECTION.MAIL_IMAP_PORT, 
                                                       searchString, 
                                                       Constant.ANOMALY_DETECTION.MAIL_IMAP_USER,
                                                       testStartTimestamp);
      Assert.assertTrue(isMailReceived);
  }
}
