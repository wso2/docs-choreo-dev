package com.wso2.choreo.integration.tests.anomalyDetector;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.common.exceptions.GetApiTestTokenStatusCheckException;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Class containing methods to invoke APIs for anomaly detection tests
 */
public class InvokeApi {

    private static final Logger log = LogManager.getLogger(InvokeApi.class);

    /**
     * Invokes the passthrough component from a pool of threads concurrently 
     *
     * @param clientId
     * @param clientSecret
     * @param invokeUrl
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws GetApiTestTokenStatusCheckException
     */
    public static void invokePassthroughComponentConcurrently(String clientId, String clientSecret, String invokeUrl) throws IOException, InterruptedException, ExecutionException, GetApiTestTokenStatusCheckException {
      ExecutorService executor = Executors.newFixedThreadPool(20);
      String authorizationBearerToken = getApiTestToken(clientId, clientSecret);
      log.info("Starting to send requests to the passthrough component...");
      for (int i = 0; i < 500; i++) {
        Runnable worker = new InvokePassthroughComponent(invokeUrl + "/", authorizationBearerToken, "helloword");
        executor.execute(worker);
      }
      executor.shutdown();
      executor.awaitTermination(10, TimeUnit.MINUTES);
      log.info("Finished sending requests to the passthrough component");
    }

    /**
     * Obtain a test token to invoke an exposed API
     *
     * @return Test Token
     * @throws IOException
     * @throws InterruptedException
     * @throws GetApiTestTokenStatusCheckException
     */
    private static String getApiTestToken(String clientId, String clientSecret) throws IOException, InterruptedException, GetApiTestTokenStatusCheckException {
        String authorizationBasicToken = Base64.getEncoder().encodeToString(clientId.concat(":").concat(clientSecret).getBytes());
        HttpPost request = new HttpPost(Configuration.getConfig(ConfigDefinition.STS_ENDPOINT)
                .concat(Constant.TOKEN_ENDPOINT_SUFFIX));
        request.setHeader("Content-type", "application/x-www-form-urlencoded");
        request.setHeader("Authorization", "Basic ".concat(authorizationBasicToken));
        StringEntity requestEntity = new StringEntity("grant_type=client_credentials", ContentType.APPLICATION_FORM_URLENCODED);
        request.setEntity(requestEntity);
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CloseableHttpResponse response = httpClient.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        String responseBody = EntityUtils.toString(response.getEntity());
        if (statusCode != HttpStatus.OK.value()) {
            throw new GetApiTestTokenStatusCheckException(statusCode, responseBody);
        }
        JsonObject responseBodyJson = new JsonParser().parse(responseBody).getAsJsonObject();
        return responseBodyJson.get("access_token").toString().replaceAll("\"", "");
    }
}
