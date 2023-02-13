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
import com.wso2.choreo.integration.common.TokenHandler;
import com.wso2.choreo.integration.common.choreoproject.RestApiChoreoComponent;
import com.wso2.choreo.integration.common.email.EmailUtils;
import com.wso2.choreo.integration.common.exceptions.GetApiTestTokenStatusCheckException;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Instant;


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

  private static final Logger log = LogManager.getLogger(BackendFailureAnomaly.class);

  @BeforeClass
  public void beforeClass() throws  Exception {
      invokeAccessTokenHandler =  new TokenHandler.Builder(
              Configuration.getConfig(ConfigDefinition.ANOMALY_DETECTION_TEST_CHOREO_ORG_HANDLE),
              Configuration.getConfig(ConfigDefinition.ANOMALY_DETECTION_TEST_USER_EMAIL),
              Configuration.getConfig(ConfigDefinition.ANOMALY_DETECTION_TEST_USER_PASSWORD))
              .asgardeoClientId(Configuration.getConfig(ConfigDefinition.ASGARDEO_CLIENT_ID))
              .asgardeoClientSecret(Configuration.getConfig(ConfigDefinition.ASGARDEO_CLIENT_SECRET))
              .cpAppClientId(Configuration.getConfig(ConfigDefinition.CP_APP_CLIENT_ID))
              .cpAppClientSecret(Configuration.getConfig(ConfigDefinition.CP_APP_CLIENT_SECRET)).build();

      String orgUuid = Configuration.getConfig(ConfigDefinition.ANOMALY_DETECTION_TEST_CHOREO_ORG_UUID);
      String passthorughVersionId = Configuration.getConfig(ConfigDefinition.ANOMALY_DETECTION_PASSTHROUGH_VERSION_ID);
      orgHandler = Configuration.getConfig(ConfigDefinition.ANOMALY_DETECTION_TEST_CHOREO_ORG_HANDLE);

      projectsAPIAccessToken = invokeAccessTokenHandler.getTestTokenForCPAPIs();
      passthroughComponentId = Configuration.getConfig(ConfigDefinition.ANOMALY_DETECTION_PASSTHROUGH_COMPONENT_ID);
      passthroughReleaseId = Configuration.getConfig(ConfigDefinition.ANOMALY_DETECTION_PASSTHROUGH_RELEASE_ID);
      projectId = Configuration.getConfig(ConfigDefinition.ANOMALY_DETECTION_PROJECT_ID);
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
            restApiComponent.redeploy(projectsAPIAccessToken, passthroughComponentId, passthroughReleaseId, orgHandler);
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
      InvokeApi.invokePassthroughComponentConcurrently(
              Configuration.getConfig(ConfigDefinition.ANOMALY_DETECTION_PASSTHROUGH_CLIENT_ID),
              Configuration.getConfig(ConfigDefinition.ANOMALY_DETECTION_PASSTHROUGH_CLIENT_SECRET),
              Configuration.getConfig(ConfigDefinition.ANOMALY_DETECTION_PASSTHROUGH_INVOKE_URL));
      log.info("Waiting for 5 minutes to allow the anomaly to be detected...");
      Thread.sleep(300000);
      String searchString = "[Choreo ALERT] Anomaly detected in " +
              Configuration.getConfig(ConfigDefinition.ANOMALY_DETECTION_PASSTHROUGH_COMPONENT_NAME);
      log.info("Starting to check if the email alert was received...");
      boolean isMailReceived = EmailUtils.checkForMail(Constant.ANOMALY_DETECTION.MAIL_IMAP_HOST,
              Configuration.getConfig(ConfigDefinition.ANOMALY_DETECTION_MAIL_IMAP_PASS),
                                                       Constant.ANOMALY_DETECTION.MAIL_IMAP_PORT,
                                                       searchString,
                                                       Constant.ANOMALY_DETECTION.MAIL_IMAP_USER,
                                                       testStartTimestamp);
      Assert.assertTrue(isMailReceived);
  }
}
