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

import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.common.AccessTokenHandler;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.TokenHandler;
import com.wso2.choreo.integration.common.choreoproject.RestApiChoreoComponent;
import com.wso2.choreo.integration.common.email.EmailUtils;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import java.io.IOException;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;


/**
 * Contains a test to check if the multivariate anomaly detector detects a backend failure anomaly
 */
public class backendFailureAnomaly extends TestNGCitrusSpringSupport {

  private AccessTokenHandler invokeAccessTokenHandler;
  private String orgHandler;
  private String passthroughComponentId;
  private String passthroughReleaseId;
  private String projectsAPIAccessToken;
  private String projectId;
  private RestApiChoreoComponent restApiComponent;
  private long testStartTimestamp;

  private final static Logger log = LoggerFactory.getLogger(backendFailureAnomaly.class);

  @Autowired
  private HttpClient adPassthroughTestClient;

  @BeforeClass
  public void beforeClass() throws InterruptedException, IOException, TokenRetrievalException {
    TokenHandler tokenHandler = new TokenHandler();
    tokenHandler.setTestUserEmail(Configuration.ANOMALY_DETECTION.TEST_USER_EMAIL);
    tokenHandler.setTestUserPassword(Configuration.ANOMALY_DETECTION.TEST_USER_PASSWORD);
    tokenHandler.setTestChoreoOrgHandle(Configuration.ANOMALY_DETECTION.TEST_CHOREO_ORG_HANDLE);
    projectsAPIAccessToken = Constant.BEARER_PREFIX.concat(tokenHandler.getTestTokenForCPAPIs());
    ChoreoOrganization org = new ChoreoOrganization(Configuration.ANOMALY_DETECTION.TEST_CHOREO_ORG_HANDLE,
    String.valueOf(Configuration.ANOMALY_DETECTION.TEST_CHOREO_ORG_ID), Configuration.ANOMALY_DETECTION.TEST_CHOREO_ORG_UUID);
    orgHandler = org.getOrgHandle();
    passthroughComponentId = Configuration.ANOMALY_DETECTION.PASSTHROUGH_COMPONENT_ID;
    passthroughReleaseId = Configuration.ANOMALY_DETECTION.PASSTHROUGH_RELEASE_ID;
    projectId = Configuration.ANOMALY_DETECTION.PROJECT_ID;
    restApiComponent = new RestApiChoreoComponent();
    restApiComponent.setProjectId(projectId);
    restApiComponent.setOrgHandler(orgHandler);
    invokeAccessTokenHandler = new AccessTokenHandler(Configuration.ANOMALY_DETECTION.PASSTHROUGH_CLIENT_ID, Configuration.ANOMALY_DETECTION.PASSTHROUGH_CLIENT_SECRET);
    
    // Deployed components may get stopped automatically by Choreo. Hence redeploying the passthrough component before starting the test.
    restApiComponent.redeploy(projectsAPIAccessToken, passthroughComponentId, passthroughReleaseId, Configuration.ANOMALY_DETECTION.TEST_CHOREO_ORG_HANDLE);
    
    log.info("Waiting for redeployed component to become ready...");
    Thread.sleep(30000);
    testStartTimestamp = Instant.now().toEpochMilli();
}

  /**
   * Invokes the passthrough component to inject a backend failure anomaly 
   * 
   * @throws IOException
   * @throws InterruptedException
   */
  @Test(invocationCount = 200, threadPoolSize = 20) // Large thread pool size is to generate a high load to the
                                                    // component thereby causing a high error rate in it. If the
                                                    // error rate is small, Anomaly Detector does not detect them
                                                    // as anomalies
  @CitrusTest
  public void injectAnomaly() throws IOException, InterruptedException {
    $(http()
      .client(adPassthroughTestClient)
      .send()
      .post("/" + Configuration.ANOMALY_DETECTION.PASSTHROUGH_INVOKE_URL.split("(?<=choreoapis.dev)/")[1] + "/") // This extracts the path after the hostname and appends "/" before and after it
      .message()
      .header(HttpHeaders.AUTHORIZATION, "Bearer " + invokeAccessTokenHandler.getTestToken())
      .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
      .body("helloworld") // Just a random payload. Does not matter what is in the body.
      .accept(String.valueOf(MediaType.TEXT_PLAIN)));
  }
  
  /**
   * 
   * Log into the email account and check if the alert for the anomaly injected through injectAnomaly() above 
   * was received.
   *  
   * @throws Exception
   */
  @Test
  @CitrusTest
  public void testEmailAlert() throws Exception {
    
    log.info("Sleeping for 5 minutes to allow the anomaly to be detected...");
    Thread.sleep(300000);
    String searchString = "[Choreo ALERT] Anomaly detected in " + Configuration.ANOMALY_DETECTION.PASSTHROUGH_COMPONENT_NAME;
    log.info("Checking if the email alert was received...");
    boolean isMailReceived = EmailUtils.checkForMail(Constant.ANOMALY_DETECTION.MAIL_IMAP_HOST, 
                                                     Configuration.ANOMALY_DETECTION.MAIL_IMAP_PASS, 
                                                     Constant.ANOMALY_DETECTION.MAIL_IMAP_PORT, 
                                                     searchString, 
                                                     Constant.ANOMALY_DETECTION.MAIL_IMAP_USER,
                                                     testStartTimestamp);
    Assert.assertTrue(isMailReceived);
  }
}